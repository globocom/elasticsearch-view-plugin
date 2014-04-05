package org.elasticsearch.action.view;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.single.shard.SingleShardOperationRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.internal.InternalClient;
import org.elasticsearch.common.Nullable;

public class ViewRequestBuilder
		extends
		SingleShardOperationRequestBuilder<ViewRequest, ViewResponse, ViewRequestBuilder> {

	public ViewRequestBuilder(Client client) {
		super((InternalClient) client, new ViewRequest());
	}

	public ViewRequestBuilder(Client client, @Nullable String index) {
		super((InternalClient) client, new ViewRequest(index));
	}

	/**
	 * Sets the type of the document to fetch. If set to <tt>null</tt>, will use
	 * just the id to fetch the first document matching it.
	 */
	public ViewRequestBuilder setType(@Nullable String type) {
		request.type(type);
		return this;
	}

	/**
	 * Sets the id of the document to fetch.
	 */
	public ViewRequestBuilder setId(String id) {
		request.id(id);
		return this;
	}

	/**
	 * Sets the parent id of this document. Will simply set the routing to this
	 * value, as it is only used for routing with delete requests.
	 */
	public ViewRequestBuilder setParent(String parent) {
		request.parent(parent);
		return this;
	}

	/**
	 * Controls the shard routing of the request. Using this value to hash the
	 * shard and not the id.
	 */
	public ViewRequestBuilder setRouting(String routing) {
		request.routing(routing);
		return this;
	}

	/**
	 * Sets the preference to execute the search. Defaults to randomize across
	 * shards. Can be set to <tt>_local</tt> to prefer local shards,
	 * <tt>_primary</tt> to execute only on primary shards, or a custom value,
	 * which guarantees that the same order will be used across different
	 * requests.
	 */
	public ViewRequestBuilder setPreference(String preference) {
		request.preference(preference);
		return this;
	}

	/**
	 * Explicitly specify the fields that will be returned. By default, the
	 * <tt>_source</tt> field will be returned.
	 */
	public ViewRequestBuilder setFields(String... fields) {
		request.fields(fields);
		return this;
	}

	/**
	 * Should a refresh be executed before this get operation causing the
	 * operation to return the latest value. Note, heavy get should not set this
	 * to <tt>true</tt>. Defaults to <tt>false</tt>.
	 */
	public ViewRequestBuilder setRefresh(boolean refresh) {
		request.refresh(refresh);
		return this;
	}

	public ViewRequestBuilder setRealtime(Boolean realtime) {
		request.realtime(realtime);
		return this;
	}

	@Override
	protected void doExecute(ActionListener<ViewResponse> listener) {
		((Client) client).execute(ViewAction.INSTANCE, request, listener);
	}
}
