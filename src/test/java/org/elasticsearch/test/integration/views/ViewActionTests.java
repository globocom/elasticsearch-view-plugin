package org.elasticsearch.test.integration.views;

import static junit.framework.Assert.assertEquals;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.view.ViewAction;
import org.elasticsearch.action.view.ViewRequest;
import org.elasticsearch.action.view.ViewResponse;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ViewActionTests {

    private static final String TEST_TYPE = "car";
	private static final String TEST_INDEX = "testview";
	private Node node = null;

    protected Settings buildNodeSettings() {
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
                .put("node.name", "node-test")
                .put("node.data", true)
                .put("cluster.name", "cluster-test-" + NetworkUtils.getLocalAddress().getHostName())
                .put("index.store.type", "memory")
                .put("index.store.fs.memory.enabled", "true")
                .put("gateway.type", "none")
                .put("path.data", "./target/elasticsearch-test/data")
                .put("path.work", "./target/elasticsearch-test/work")
                .put("path.logs", "./target/elasticsearch-test/logs")
                .put("index.number_of_shards", "1")
                .put("index.number_of_replicas", "0")
                .put("cluster.routing.schedule", "50ms")
                .put("node.local", true);
        
		return builder.build();
    }
    
    @Before
    public void setUp() {
    	node = NodeBuilder.nodeBuilder().settings(buildNodeSettings()).node();
		node.client().admin().indices().prepareDelete().execute().actionGet();

		indexTestDocument(TEST_INDEX, TEST_TYPE, "1", "ix-35", "hyundai");
		indexTestDocument(TEST_INDEX, TEST_TYPE, "2", "sportage", "kia");
		createView();
    }

	private void createView() {
		XContentBuilder xbMapping = null;
		try {
			xbMapping = jsonBuilder().startObject()
				.startObject(TEST_TYPE)
					.startObject("_meta")
						.startObject("views")
							.startObject("default")
								.field("view", "Car: {{_source.name}}, Brand: {{_source.brand}}")
							.endObject()
						.endObject()
					.endObject()
				.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		putMapping(xbMapping);
	}
	
	private void indexQuery() {
		XContentBuilder xbMapping = null;
		try {
			xbMapping = jsonBuilder().startObject()
				.startObject("query_views")
					.startObject("default")
						.startObject("queries")
							.startObject("all_documents")
								.field("indices", TEST_INDEX)
								.startArray("types")
									.value(TEST_TYPE)
								.endArray()
								.startObject("query")
									.startObject("match_all")
								.endObject()
							.endObject()
						.endObject()
					.endObject()
					.field("view", "[{{#_queries}}{{#all_documents}}{{#_source}}{\"Car\": \"{{name}}\"}{{^last}},{{/last}}{{/_source}}{{/all_documents}}{{/_queries}}]")
				.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		node.client().prepareIndex("queryindex", "queries", "all").setRefresh(true).setSource(xbMapping)
				.execute().actionGet();
	}

	private void putMapping(XContentBuilder xbMapping) {
		node.client().admin().indices().preparePutMapping(TEST_INDEX)
				.setType(TEST_TYPE).setSource(xbMapping).execute().actionGet();
	}

	private void indexTestDocument(String index, String type, String id, String name, String brand) {
		try {
			node.client().prepareIndex(index, type, id).setRefresh(true).setSource(jsonBuilder()
					.startObject().field("name", name).field("brand", brand).endObject())
					.execute().actionGet();
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Test
    public void testDefaultView() throws Exception {
        ViewResponse response = node.client().execute(ViewAction.INSTANCE, new ViewRequest(TEST_INDEX, TEST_TYPE, "1")).get();
        assertEquals("Car: ix-35, Brand: hyundai", new String(response.getSourceTransformResult()));
        ViewResponse response2 = node.client().execute(ViewAction.INSTANCE, new ViewRequest(TEST_INDEX, TEST_TYPE, "2")).get();
        assertEquals("Car: sportage, Brand: kia", new String(response2.getSourceTransformResult()));
    }

    @Test
    public void testUndefinedView() throws Exception {
        try {
        	String indexWithoutViews = "indexwithoutviews";
        	indexTestDocument(indexWithoutViews, TEST_TYPE, "1", "ix-35", "hyundai");
            node.client().execute(ViewAction.INSTANCE, new ViewRequest(indexWithoutViews, TEST_TYPE, "1")).get();
            fail("Exception expected!");
        } catch (Exception e) {
			assertEquals(
					"org.elasticsearch.view.exception.ElasticSearchViewNotFoundException: "
							+ "No view [default] found for document type [car]",
					e.getMessage());
        }
    }

    @Test
    public void testCustomView() throws Exception {
    	indexQuery();

        ViewResponse response = node.client().execute(ViewAction.INSTANCE, new ViewRequest("queryindex", "queries", "all")).get();
        assertEquals("[{\"Car\": \"ix-35\"},{\"Car\": \"sportage\"}]", new String(response.getSourceTransformResult()));
    }

    @After
    public void tearDown() throws Exception {
    	node.close();
    }
}
