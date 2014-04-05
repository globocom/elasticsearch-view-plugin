package org.elasticsearch.plugin.view;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.action.view.ViewAction;
import org.elasticsearch.action.view.TransportViewAction;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.rest.RestModule;
import org.elasticsearch.rest.action.view.RestSourceTransformAction;

public class ViewPlugin extends AbstractPlugin {

	@Inject
	public ViewPlugin() {
	}

	public String name() {
		return "view-plugin";
	}

	public String description() {
		return "Source Transform Plugin";
	}

	@Override
	public void processModule(Module module) {
		if (module instanceof RestModule) {
			((RestModule) module)
					.addRestAction(RestSourceTransformAction.class);
		}
		if (module instanceof ActionModule) {
			((ActionModule) module).registerAction(
					ViewAction.INSTANCE,
					TransportViewAction.class);
		}
	}
}
