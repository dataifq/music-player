package com.github.anrimian.musicplayer.ui.library.folders;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.anrimian.musicplayer.R;
import com.github.anrimian.musicplayer.di.Components;
import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.composition.Order;
import com.github.anrimian.musicplayer.domain.models.composition.folders.FileSource;
import com.github.anrimian.musicplayer.domain.models.playlist.PlayList;
import com.github.anrimian.musicplayer.ui.common.DialogUtils;
import com.github.anrimian.musicplayer.ui.common.error.ErrorCommand;
import com.github.anrimian.musicplayer.ui.common.order.SelectOrderDialogFragment;
import com.github.anrimian.musicplayer.ui.library.folders.adapter.MusicFileSourceAdapter;
import com.github.anrimian.musicplayer.ui.library.folders.wrappers.HeaderViewWrapper;
import com.github.anrimian.musicplayer.ui.playlist_screens.choose.ChoosePlayListDialogFragment;
import com.github.anrimian.musicplayer.ui.utils.fragments.BackButtonListener;
import com.github.anrimian.musicplayer.ui.utils.wrappers.ProgressViewWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.anrimian.musicplayer.Constants.Arguments.PATH_ARG;
import static com.github.anrimian.musicplayer.Constants.Tags.ORDER_TAG;
import static com.github.anrimian.musicplayer.Constants.Tags.SELECT_PLAYLIST_FOR_FOLDER_TAG;
import static com.github.anrimian.musicplayer.Constants.Tags.SELECT_PLAYLIST_TAG;
import static com.github.anrimian.musicplayer.ui.common.format.MessagesUtils.getAddToPlayListCompleteMessage;
import static com.github.anrimian.musicplayer.ui.common.format.MessagesUtils.getDeleteCompleteMessage;

/**
 * Created on 23.10.2017.
 */

public class LibraryFoldersFragment extends MvpAppCompatFragment implements LibraryFoldersView, BackButtonListener {

    @InjectPresenter
    LibraryFoldersPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.header_container)
    View headerContainer;

    @BindView(R.id.list_container)
    CoordinatorLayout clListContainer;

    private ProgressViewWrapper progressViewWrapper;
    private MusicFileSourceAdapter adapter;
    private HeaderViewWrapper headerViewWrapper;

    public static LibraryFoldersFragment newInstance(@Nullable String path) {
        Bundle args = new Bundle();
        args.putString(PATH_ARG, path);
        LibraryFoldersFragment fragment = new LibraryFoldersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    private String getPath() {
        return getArguments().getString(PATH_ARG);
    }

    @ProvidePresenter
    LibraryFoldersPresenter providePresenter() {
        return Components.getLibraryFilesComponent(getPath()).storageLibraryPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        return inflater.inflate(R.layout.fragment_library_folders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        progressViewWrapper = new ProgressViewWrapper(view);
        progressViewWrapper.setTryAgainButtonOnClickListener(v -> presenter.onTryAgainButtonClicked());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        headerViewWrapper = new HeaderViewWrapper(headerContainer);
        headerViewWrapper.setOnClickListener(v -> presenter.onBackPathButtonClicked());

        fab.setOnClickListener(v -> presenter.onPlayAllButtonClicked());

        SelectOrderDialogFragment fragment = (SelectOrderDialogFragment) getChildFragmentManager()
                .findFragmentByTag(ORDER_TAG);
        if (fragment != null) {
            fragment.setOnCompleteListener(presenter::onOrderSelected);
        }

        ChoosePlayListDialogFragment playListDialog = (ChoosePlayListDialogFragment) getChildFragmentManager()
                .findFragmentByTag(SELECT_PLAYLIST_TAG);
        if (playListDialog != null) {
            playListDialog.setOnCompleteListener(presenter::onPlayListToAddingSelected);
        }

        ChoosePlayListDialogFragment folderPlayListDialog = (ChoosePlayListDialogFragment) getChildFragmentManager()
                .findFragmentByTag(SELECT_PLAYLIST_FOR_FOLDER_TAG);
        if (folderPlayListDialog != null) {
            folderPlayListDialog.setOnCompleteListener(presenter::onPlayListForFolderSelected);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.library_files_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
//        String searchQuery = presenter.getSearchQuery();
//        if (!TextUtils.isEmpty(searchQuery)) {
//            searchItem.expandActionView();
//            searchView.setQuery(searchQuery, false);
//            searchView.clearFocus();
//        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                presenter.onSearchTextChanged(text);
//                AndroidUtils.hideKeyboard(getActivity());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                presenter.onSearchTextChanged(text);
                //presenter.startSearchWithDelay(newText);it notify when search view closed, first fix this
                return true;
            }
        });
//        searchView.setOnSearchClickListener(v -> searchView.setQuery(presenter.getSearchQuery(), false));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_order: {
                presenter.onOrderMenuItemClicked();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void bindList(List<FileSource> musicList) {
        adapter = new MusicFileSourceAdapter(musicList);
        adapter.setOnCompositionClickListener(presenter::onCompositionClicked);
        adapter.setOnFolderClickListener(this::goToMusicStorageScreen);
        adapter.setOnDeleteCompositionClickListener(presenter::onDeleteCompositionButtonClicked);
        adapter.setOnAddToPlaylistClickListener(presenter::onAddToPlayListButtonClicked);
        adapter.setOnAddFolderToPlaylistClickListener(presenter::onAddFolderToPlayListButtonClicked);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showBackPathButton(@NonNull String path) {
        headerViewWrapper.setVisible(true);
        headerViewWrapper.bind(path);
    }

    @Override
    public void hideBackPathButton() {
        headerViewWrapper.setVisible(false);
    }

    @Override
    public void showEmptyList() {
        fab.setVisibility(View.GONE);
        progressViewWrapper.showMessage(R.string.compositions_on_device_not_found, false);
    }

    @Override
    public void showEmptySearchResult() {
        fab.setVisibility(View.GONE);
        progressViewWrapper.showMessage(R.string.compositions_and_folders_for_search_not_found, false);
    }

    @Override
    public void showList() {
        fab.setVisibility(View.VISIBLE);
        progressViewWrapper.hideAll();
    }

    @Override
    public void showLoading() {
        progressViewWrapper.showProgress();
    }

    @Override
    public void showError(ErrorCommand errorCommand) {
        progressViewWrapper.hideAll();
        progressViewWrapper.showMessage(errorCommand.getMessage(), true);
    }

    @Override
    public void goBackToMusicStorageScreen(String path) {
        FragmentManager fragmentManager = requireFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.anim_alpha_appear, R.anim.anim_alpha_disappear)
                    .replace(R.id.library_folders_container, LibraryFoldersFragment.newInstance(path))
                    .commit();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (getPath() != null) {
            presenter.onBackPathButtonClicked();
            return true;
        }
        return false;
    }

    @Override
    public void updateList(List<FileSource> oldList, List<FileSource> newList) {
        Parcelable recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        adapter.updateList(oldList, newList);
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    @Override
    public void showAddingToPlayListError(ErrorCommand errorCommand) {
        Snackbar.make(clListContainer,
                getString(R.string.add_to_playlist_error_template, errorCommand.getMessage()),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showAddingToPlayListComplete(PlayList playList, List<Composition> compositions) {
        String text = getAddToPlayListCompleteMessage(requireActivity(), playList, compositions);
        Snackbar.make(clListContainer, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSelectPlayListForFolderDialog() {
        ChoosePlayListDialogFragment dialog = new ChoosePlayListDialogFragment();
        dialog.setOnCompleteListener(presenter::onPlayListForFolderSelected);
        dialog.show(getChildFragmentManager(), SELECT_PLAYLIST_FOR_FOLDER_TAG);
    }

    @Override
    public void showSelectOrderScreen(Order folderOrder) {
        SelectOrderDialogFragment fragment = SelectOrderDialogFragment.newInstance(folderOrder);
        fragment.setOnCompleteListener(presenter::onOrderSelected);
        fragment.show(getChildFragmentManager(), ORDER_TAG);
    }

    @Override
    public void showSelectPlayListDialog() {
        ChoosePlayListDialogFragment dialog = new ChoosePlayListDialogFragment();
        dialog.setOnCompleteListener(presenter::onPlayListToAddingSelected);
        dialog.show(getChildFragmentManager(), SELECT_PLAYLIST_TAG);
    }

    @Override
    public void showConfirmDeleteDialog(List<Composition> compositionsToDelete) {
        DialogUtils.showConfirmDeleteDialog(requireContext(),
                compositionsToDelete,
                presenter::onDeleteCompositionsDialogConfirmed);
    }

    @Override
    public void showDeleteCompositionError(ErrorCommand errorCommand) {
        Snackbar.make(clListContainer,
                getString(R.string.add_to_playlist_error_template, errorCommand.getMessage()),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showDeleteCompositionMessage(List<Composition> compositionsToDelete) {
        String text = getDeleteCompleteMessage(requireActivity(), compositionsToDelete);
        Snackbar.make(clListContainer, text, Snackbar.LENGTH_SHORT).show();
    }

    private void goToMusicStorageScreen(String path) {
        LibraryFoldersFragment fragment = LibraryFoldersFragment.newInstance(path);
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.library_folders_container, fragment, path)
                .addToBackStack(path)
                .commit();
    }
}