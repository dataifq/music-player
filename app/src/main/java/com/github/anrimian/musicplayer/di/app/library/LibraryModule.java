package com.github.anrimian.musicplayer.di.app.library;

import static com.github.anrimian.musicplayer.di.app.SchedulerModule.UI_SCHEDULER;

import androidx.annotation.NonNull;

import com.github.anrimian.filesync.SyncInteractor;
import com.github.anrimian.musicplayer.domain.controllers.SystemMusicController;
import com.github.anrimian.musicplayer.domain.interactors.library.LibraryFoldersInteractor;
import com.github.anrimian.musicplayer.domain.interactors.player.LibraryPlayerInteractor;
import com.github.anrimian.musicplayer.domain.interactors.player.PlayerScreenInteractor;
import com.github.anrimian.musicplayer.domain.interactors.playlists.PlayListsInteractor;
import com.github.anrimian.musicplayer.domain.interactors.sleep_timer.SleepTimerInteractor;
import com.github.anrimian.musicplayer.domain.models.sync.FileKey;
import com.github.anrimian.musicplayer.domain.repositories.MediaScannerRepository;
import com.github.anrimian.musicplayer.domain.repositories.PlayQueueRepository;
import com.github.anrimian.musicplayer.domain.repositories.SettingsRepository;
import com.github.anrimian.musicplayer.domain.repositories.UiStateRepository;
import com.github.anrimian.musicplayer.ui.common.error.parser.ErrorParser;
import com.github.anrimian.musicplayer.ui.player_screen.PlayerPresenter;
import com.github.anrimian.musicplayer.ui.player_screen.lyrics.LyricsPresenter;
import com.github.anrimian.musicplayer.ui.player_screen.queue.PlayQueuePresenter;
import com.github.anrimian.musicplayer.ui.settings.folders.ExcludedFoldersPresenter;

import javax.annotation.Nonnull;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Scheduler;

/**
 * Created on 29.10.2017.
 */
@Module
public class LibraryModule {

    @Provides
    @Nonnull
    PlayerPresenter playerPresenter(LibraryPlayerInteractor musicPlayerInteractor,
                                    PlayerScreenInteractor playerScreenInteractor,
                                    PlayListsInteractor playListsInteractor,
                                    ErrorParser errorParser,
                                    @Named(UI_SCHEDULER) Scheduler uiScheduler) {
        return new PlayerPresenter(musicPlayerInteractor,
                playerScreenInteractor,
                playListsInteractor,
                errorParser,
                uiScheduler);
    }

    @Provides
    @Nonnull
    PlayQueuePresenter playQueuePresenter(LibraryPlayerInteractor musicPlayerInteractor,
                                          PlayerScreenInteractor playerScreenInteractor,
                                          SyncInteractor<FileKey, ?, Long> syncInteractor,
                                          PlayListsInteractor playListsInteractor,
                                          ErrorParser errorParser,
                                          @Named(UI_SCHEDULER) Scheduler uiScheduler) {
        return new PlayQueuePresenter(musicPlayerInteractor,
                playerScreenInteractor,
                syncInteractor,
                playListsInteractor,
                errorParser,
                uiScheduler);
    }

    @Provides
    @Nonnull
    LyricsPresenter lyricsPresenter(LibraryPlayerInteractor libraryPlayerInteractor,
                                    ErrorParser errorParser,
                                    @Named(UI_SCHEDULER) Scheduler uiScheduler) {
        return new LyricsPresenter(
                libraryPlayerInteractor,
                errorParser,
                uiScheduler);
    }

    @Provides
    @NonNull
    @LibraryScope
    PlayerScreenInteractor playerScreenInteractor(SleepTimerInteractor sleepTimerInteractor,
                                                  LibraryPlayerInteractor libraryPlayerInteractor,
                                                  SyncInteractor<FileKey, ?, Long> syncInteractor,
                                                  PlayQueueRepository playQueueRepository,
                                                  UiStateRepository uiStateRepository,
                                                  SettingsRepository settingsRepository,
                                                  MediaScannerRepository mediaScannerRepository,
                                                  SystemMusicController systemMusicController) {
        return new PlayerScreenInteractor(sleepTimerInteractor,
                libraryPlayerInteractor,
                syncInteractor,
                playQueueRepository,
                uiStateRepository,
                settingsRepository,
                mediaScannerRepository,
                systemMusicController);
    }

    @Provides
    @Nonnull
    ExcludedFoldersPresenter excludedFoldersPresenter(LibraryFoldersInteractor interactor,
                                                      @Named(UI_SCHEDULER) Scheduler uiScheduler,
                                                      ErrorParser errorParser) {
        return new ExcludedFoldersPresenter(interactor, uiScheduler, errorParser);
    }
}
