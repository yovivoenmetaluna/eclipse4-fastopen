package com.laboki.eclipse.plugin.fastopen.instance;

public class BaseInstance implements Instance {

	@Override
	public Instance
	start() {
		return this;
	}

	@Override
	public Instance
	stop() {
		return this;
	}
}
