package org.elasticsearch.action.view;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.Client;

public class ViewAction  extends Action<ViewRequest, ViewResponse, ViewRequestBuilder> {


    public static final ViewAction INSTANCE = new ViewAction();
    public static final String NAME = "transform";

    private ViewAction() {
        super(NAME);
    }

    @Override
    public ViewResponse newResponse() {
        return new ViewResponse();
    }

    @Override
    public ViewRequestBuilder newRequestBuilder(Client client) {
        return new ViewRequestBuilder(client);
    }
}
