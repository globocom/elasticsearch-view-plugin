package org.elasticsearch.view.mustache;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ConcurrentCollections;
import org.elasticsearch.view.ViewEngineService;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class MustacheViewEngineService extends AbstractComponent implements
		ViewEngineService {

	private final DefaultMustacheFactory factory;

	private final ConcurrentMap<String, String> staticCache = ConcurrentCollections
			.newConcurrentMap();

	public String render(String view, @Nullable Map<String, Object> vars) {
		Writer writer = new StringWriter();
		Mustache mustache = factory.compile(new StringReader(view), "render");
		mustache.execute(writer, vars);
		return writer.toString();
	}

	@Inject
	public MustacheViewEngineService(Settings settings) {
		super(settings);
		factory = new DefaultMustacheFactory() {
			@Override
			public Reader getReader(String resourceName) {
				if (staticCache.containsKey(resourceName)) {
					return new StringReader(staticCache.get(resourceName));
				}
				return super.getReader(resourceName);
			}
		};
	}

	public String[] types() {
		return new String[] { "mustache" };
	}

	public String[] extensions() {
		return new String[] { "mustache" };
	}

	public String contentType() {
		return "text/html;charset=utf8";
	}

	public void load(String name, String view) {
		staticCache.put(name, view);
	}
}