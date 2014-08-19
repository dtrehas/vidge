package org.vidge;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Activator plugin;
	public static String ID = "org.vidge";

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// VidgeSettings.init("..//vidge.resources");
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static Activator getDefault() {
		return plugin;
	}
}
