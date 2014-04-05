package org.elasticsearch.rest.action.view;

import static org.elasticsearch.rest.RestRequest.Method.GET;

import java.io.IOException;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.view.ViewAction;
import org.elasticsearch.action.view.ViewRequest;
import org.elasticsearch.action.view.ViewResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.StringRestResponse;
import org.elasticsearch.rest.XContentThrowableRestResponse;

public class RestSourceTransformAction extends BaseRestHandler {

	@Inject
	public RestSourceTransformAction(Settings settings, Client client,
			RestController controller) {
		super(settings, client);
		controller
				.registerHandler(GET, "/_view/{index}/{type}/{id}", this);
	}

	public void handleRequest(final RestRequest request,
			final RestChannel channel) {
		final ViewRequest getRequest = new ViewRequest(
				request.param("index"), request.param("type"),
				request.param("id"));
		getRequest.listenerThreaded(false);
		getRequest.operationThreaded(true);
		getRequest.refresh(request.paramAsBoolean("refresh",
				getRequest.refresh()));
		getRequest.routing(request.param("routing")); // order is important, set
														// it after routing, so
														// it will set the
														// routing
		getRequest.parent(request.param("parent"));
		getRequest.preference(request.param("preference"));
		getRequest.realtime(request.paramAsBooleanOptional("realtime", null));

		String sField = request.param("fields");
		if (sField != null) {
			String[] sFields = Strings.splitStringByCommaToArray(sField);
			if (sFields != null) {
				getRequest.fields(sFields);
			}
		}

		client.execute(ViewAction.INSTANCE, getRequest,
				new ActionListener<ViewResponse>() {
					public void onResponse(ViewResponse response) {
						try {
							if (!response.isExists()) {
								channel.sendResponse(new StringRestResponse(
										RestStatus.NOT_FOUND,
										"{\"message\": \"Not Found\"}"));
							} else {
								channel.sendResponse(new StringRestResponse(
										RestStatus.OK, response
												.getSourceTransformResult()));
							}
						} catch (Throwable e) {
							onFailure(e);
						}
					}

					public void onFailure(Throwable e) {
						try {
							channel.sendResponse(new XContentThrowableRestResponse(
									request, e));
						} catch (IOException e1) {
							logger.error("Failed to send failure response", e1);
						}
					}
				});
	}
}