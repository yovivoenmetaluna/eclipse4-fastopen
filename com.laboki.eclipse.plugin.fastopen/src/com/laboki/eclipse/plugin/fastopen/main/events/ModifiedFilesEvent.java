package com.laboki.eclipse.plugin.fastopen.main.events;

import com.google.common.collect.ImmutableList;

public final class ModifiedFilesEvent extends FilesEvent {

	public ModifiedFilesEvent(final ImmutableList<String> files) {
		super(files);
	}
}