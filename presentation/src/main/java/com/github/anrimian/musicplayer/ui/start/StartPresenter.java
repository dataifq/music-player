package com.github.anrimian.musicplayer.ui.start;

import moxy.InjectViewState;
import moxy.MvpPresenter;

/**
 * Created on 19.10.2017.
 */

@InjectViewState
public class StartPresenter extends MvpPresenter<StartView> {

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        requestFilesPermissions();
    }

    void onFilesPermissionResult(boolean granted) {
        if (granted) {
            getViewState().startSystemServices();
            getViewState().goToMainScreen();
        } else {
            getViewState().showDeniedPermissionMessage();
        }
    }

    void onTryAgainButtonClicked() {
        requestFilesPermissions();
    }

    private void requestFilesPermissions() {
        getViewState().showStub();
        getViewState().requestFilesPermissions();
    }
}
