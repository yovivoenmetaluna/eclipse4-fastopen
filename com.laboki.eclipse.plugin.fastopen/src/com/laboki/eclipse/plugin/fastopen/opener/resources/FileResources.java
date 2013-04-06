package com.laboki.eclipse.plugin.fastopen.opener.resources;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.fastopen.DelayedTask;
import com.laboki.eclipse.plugin.fastopen.EventBus;
import com.laboki.eclipse.plugin.fastopen.events.FileResourcesMapEvent;
import com.laboki.eclipse.plugin.fastopen.events.ModifiedFilesEvent;
import com.laboki.eclipse.plugin.fastopen.events.WorkspaceResourcesEvent;
import com.laboki.eclipse.plugin.fastopen.opener.EditorContext;
import com.laboki.eclipse.plugin.fastopen.opener.listeners.OpenerResourceChangeListener;

public final class FileResources implements IResourceDeltaVisitor {

	private final Map<String, IFile> fileResourcesMap = Maps.newHashMap();
	private final List<String> modifiedFiles = Lists.newArrayList();
	private final OpenerResourceChangeListener listener = new OpenerResourceChangeListener(this);

	public FileResources() {
		this.listener.start();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void worskpaceResources(final WorkspaceResourcesEvent event) {
		EditorContext.asyncExec(new DelayedTask("", 50) {

			ImmutableList<IFile> resources = event.getResources();

			@Override
			public void execute() {
				this.buildFileResourcesMap();
				this.updateModifiedFiles();
				FileResources.this.postEvents();
			}

			private void buildFileResourcesMap() {
				FileResources.this.fileResourcesMap.putAll(this.buildMapFromResources());
			}

			private Map<String, IFile> buildMapFromResources() {
				return Maps.uniqueIndex(this.resources, new Function<IFile, String>() {

					@Override
					public String apply(final IFile file) {
						return EditorContext.getURIPath(file);
					}
				});
			}

			private void updateModifiedFiles() {
				FileResources.this.modifiedFiles.clear();
				FileResources.this.modifiedFiles.addAll(this.getPathsFromResources());
			}

			private List<String> getPathsFromResources() {
				return Lists.transform(this.resources, new Function<IFile, String>() {

					@Override
					public String apply(final IFile file) {
						return EditorContext.getURIPath(file);
					}
				});
			}
		});
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		if (EditorContext.isResourceFile(resource)) this.updateModifiedFiles(delta, (IFile) resource);
		return true;
	}

	private void updateModifiedFiles(final IResourceDelta delta, final IFile file) {
		switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				this.addResource(file);
				break;
			case IResourceDelta.REMOVED:
				this.removeResource(file);
				break;
			default:
				break;
		}
	}

	private void addResource(final IFile file) {
		if (EditorContext.isNotValidResourceFile(file)) return;
		final String filepath = EditorContext.getURIPath(file);
		this.fileResourcesMap.put(filepath, file);
		this.modifiedFiles.remove(filepath);
		this.modifiedFiles.add(0, filepath);
		this.postEvents();
		System.out.println(file.getName());
		System.out.println("added");
	}

	private void removeResource(final IFile file) {
		final String filePath = EditorContext.getURIPath(file);
		this.fileResourcesMap.remove(filePath);
		this.modifiedFiles.remove(filePath);
		this.postEvents();
		System.out.println(file.getName());
		System.out.println("removed");
	}

	private void postEvents() {
		EventBus.post(new FileResourcesMapEvent(ImmutableMap.copyOf(this.fileResourcesMap)));
		EventBus.post(new ModifiedFilesEvent(ImmutableList.copyOf(this.modifiedFiles)));
	}
}
