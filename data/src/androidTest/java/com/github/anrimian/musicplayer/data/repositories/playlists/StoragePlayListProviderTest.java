package com.github.anrimian.musicplayer.data.repositories.playlists;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import androidx.collection.LongSparseArray;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.github.anrimian.musicplayer.data.storage.providers.playlists.StoragePlayList;
import com.github.anrimian.musicplayer.data.storage.providers.playlists.StoragePlayListItem;
import com.github.anrimian.musicplayer.data.storage.providers.playlists.StoragePlayListsProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.reactivex.observers.TestObserver;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class StoragePlayListProviderTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);

    private StoragePlayListsProvider storagePlayListsProvider;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        storagePlayListsProvider = new StoragePlayListsProvider(appContext);
    }

    @Test
    public void getPlayLists() {
        LongSparseArray<StoragePlayList> playLists = storagePlayListsProvider.getPlayLists();
        assertNotNull(playLists);
    }

    @Test
    public void createAndDeletePlayListTest() {
        TestObserver<LongSparseArray<StoragePlayList>> playListsObserver = storagePlayListsProvider.getPlayListsObservable()
                .test();

        StoragePlayList createdPlayList = storagePlayListsProvider.createPlayList("test playlist10");
        assertEquals("test playlist10", createdPlayList.getName());

        LongSparseArray<StoragePlayList> map = storagePlayListsProvider.getPlayLists();
        for(int i = 0, size = map.size(); i < size; i++) {
            StoragePlayList playList = map.valueAt(i);
            if (playList.getName().equals("test playlist10")) {
                storagePlayListsProvider.deletePlayList(playList.getId());
            }
        }

        playListsObserver.assertValueCount(1);
    }

    @Test
    public void addCompositionToPlayListTest() {
        StoragePlayListItem item = findComposition(0);
        Log.d("KEK", "item: " + item);

        storagePlayListsProvider.createPlayList("test playlist6");

        StoragePlayList playList = getPlayList("test playlist6");
        try {
            storagePlayListsProvider.addCompositionToPlayList(item.getCompositionId(),
                    playList.getId(),
                    0);

            List<StoragePlayListItem> items = storagePlayListsProvider.getPlayListItems(playList.getId());
            assertEquals(1, items.size());
            assertEquals(item.getCompositionId(), items.get(0).getCompositionId());

            storagePlayListsProvider.deleteItemFromPlayList(items.get(0).getItemId(), playList.getId());

            List<StoragePlayListItem> deletedItems = storagePlayListsProvider.getPlayListItems(playList.getId());
            assertEquals(0, deletedItems.size());
        } finally {
            storagePlayListsProvider.deletePlayList(playList.getId());
        }
    }

    @Test
    public void moveItemInPlayListTest() {
        StoragePlayListItem compositionOne = findComposition(0);
        StoragePlayListItem compositionTwo = findComposition(1);
        StoragePlayListItem compositionThree = findComposition(2);

        storagePlayListsProvider.createPlayList("test playlist7");

        StoragePlayList playList = getPlayList("test playlist7");

        try {
            storagePlayListsProvider.addCompositionToPlayList(compositionOne.getCompositionId(),
                    playList.getId(),
                    0);

            storagePlayListsProvider.addCompositionToPlayList(compositionTwo.getCompositionId(),
                    playList.getId(),
                    1);

            storagePlayListsProvider.addCompositionToPlayList(compositionThree.getCompositionId(),
                    playList.getId(),
                    2);

            List<StoragePlayListItem> items = storagePlayListsProvider.getPlayListItems(playList.getId());
            assertEquals(compositionOne.getCompositionId(), items.get(0).getCompositionId());
            assertEquals(compositionTwo.getCompositionId(), items.get(1).getCompositionId());
            assertEquals(compositionThree.getCompositionId(), items.get(2).getCompositionId());

            storagePlayListsProvider.moveItemInPlayList(playList.getId(), 2, 0);

            List<StoragePlayListItem> movedItems = storagePlayListsProvider.getPlayListItems(playList.getId());
            assertEquals(compositionThree.getCompositionId(), movedItems.get(0).getCompositionId());
            assertEquals(compositionOne.getCompositionId(), movedItems.get(1).getCompositionId());
            assertEquals(compositionTwo.getCompositionId(), movedItems.get(2).getCompositionId());
        } finally {
            storagePlayListsProvider.deletePlayList(playList.getId());
        }
    }

    @Test
    public void updatePlayListNameTest() {
        String oldName = "test playlist name";
        String newName = "test playlist name(new name)";
        storagePlayListsProvider.createPlayList(oldName);

        StoragePlayList playList = getPlayList(oldName);
        try {
            storagePlayListsProvider.updatePlayListName(playList.getId(), newName);
            assertEquals(newName, getPlayList(newName).getName());
        } finally {
            storagePlayListsProvider.deletePlayList(playList.getId());
        }
    }

    private StoragePlayList getPlayList(String name) {
        LongSparseArray<StoragePlayList> map = storagePlayListsProvider.getPlayLists();
        for(int i = 0, size = map.size(); i < size; i++) {
            StoragePlayList playList = map.valueAt(i);
            if (playList.getName().equals(name)) {
                return playList;
            }
        }
        throw new IllegalStateException("play list not found, name: " + name);
    }

    private StoragePlayListItem findComposition(int index) {
        LongSparseArray<StoragePlayList> map = storagePlayListsProvider.getPlayLists();
        for(int i = 0, size = map.size(); i < size; i++) {
            StoragePlayList playList = map.valueAt(i);
            List<StoragePlayListItem> items = storagePlayListsProvider.getPlayListItems(playList.getId());

            if (index < items.size()) {
                return items.get(index);
            }
        }
        throw new IllegalStateException("composition not found");
    }
}