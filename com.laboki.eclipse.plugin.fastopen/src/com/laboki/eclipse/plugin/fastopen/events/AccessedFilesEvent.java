package com.laboki.eclipse.plugin.fastopen.events;

import org.eclipse.core.resources.IFile;

import com.google.common.collect.ImmutableList;

public final class AccessedFilesEvent {

	private final ImmutableList<IFile> files;

	public AccessedFilesEvent(final ImmutableList<IFile> files) {
		this.files = files;
	}

	public ImmutableList<IFile>
	getFiles() {
		return this.files;
	}
}
