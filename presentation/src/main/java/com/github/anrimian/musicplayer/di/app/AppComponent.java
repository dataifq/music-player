package com.github.anrimian.musicplayer.di.app;


import com.github.anrimian.musicplayer.di.app.library.LibraryComponent;
import com.github.anrimian.musicplayer.di.app.library.LibraryModule;
import com.github.anrimian.musicplayer.di.app.play_list.PlayListComponent;
import com.github.anrimian.musicplayer.di.app.play_list.PlayListModule;
import com.github.anrimian.musicplayer.infrastructure.service.MusicServiceManager;
import com.github.anrimian.musicplayer.infrastructure.service.music.MusicService;
import com.github.anrimian.musicplayer.ui.playlist_screens.choose.ChoosePlayListPresenter;
import com.github.anrimian.musicplayer.ui.playlist_screens.create.CreatePlayListPresenter;
import com.github.anrimian.musicplayer.ui.playlist_screens.playlist.PlayListPresenter;
import com.github.anrimian.musicplayer.ui.playlist_screens.playlists.PlayListsPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on 11.02.2017.
 */

@Singleton
@Component(modules = {
        AppModule.class,
        SchedulerModule.class,
        ErrorModule.class,
        MusicModule.class,
        DbModule.class,
        StorageModule.class,
        SettingsModule.class,
        PlayListsModule.class
})
public interface AppComponent {

    LibraryComponent libraryComponent(LibraryModule libraryModule);
    PlayListComponent playListComponent(PlayListModule module);

    MusicServiceManager serviceManager();
    PlayListsPresenter playListsPresenter();
    CreatePlayListPresenter createPlayListsPresenter();
    ChoosePlayListPresenter choosePlayListPresenter();

    void inject(MusicService musicService);
}