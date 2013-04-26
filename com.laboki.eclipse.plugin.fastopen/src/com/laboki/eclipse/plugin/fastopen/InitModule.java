package com.laboki.eclipse.plugin.fastopen;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.laboki.eclipse.plugin.fastopen.opener.files.AccessedFiles;
import com.laboki.eclipse.plugin.fastopen.opener.files.AccessedFilesSerializer;
import com.laboki.eclipse.plugin.fastopen.opener.files.RecentFiles;
import com.laboki.eclipse.plugin.fastopen.opener.resources.FileResources;
import com.laboki.eclipse.plugin.fastopen.opener.resources.RecentResources;
import com.laboki.eclipse.plugin.fastopen.opener.resources.RecentResourcesFilter;
import com.laboki.eclipse.plugin.fastopen.opener.resources.WorkspaceResources;

final class InitModule extends AbstractModule {

	@Override
	protected void configure() {
		this.registerEventBusListeners();
		this.bind(RecentResourcesFilter.class).asEagerSingleton();
		this.bind(RecentResources.class).asEagerSingleton();
		this.bind(AccessedFiles.class).asEagerSingleton();
		this.bind(AccessedFilesSerializer.class).asEagerSingleton();
		this.bind(RecentFiles.class).asEagerSingleton();
		this.bind(FileResources.class).asEagerSingleton();
		this.bind(WorkspaceResources.class).asEagerSingleton();
	}

	private void registerEventBusListeners() {
		this.bindListener(Matchers.any(), new TypeListener() {

			@Override
			public <I> void hear(final TypeLiteral<I> typeLiteral, final TypeEncounter<I> typeEncounter) {
				typeEncounter.register(new InjectionListener<I>() {

					@Override
					public void afterInjection(final I i) {
						EventBus.register(i);
					}
				});
			}
		});
	}
}
