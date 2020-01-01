package com.github.anrimian.musicplayer.data.database.dao.play_queue;

import android.util.Log;

import com.github.anrimian.musicplayer.data.database.AppDatabase;
import com.github.anrimian.musicplayer.data.database.entities.play_queue.PlayQueueCompositionDto;
import com.github.anrimian.musicplayer.data.database.entities.play_queue.PlayQueueEntity;
import com.github.anrimian.musicplayer.data.database.entities.play_queue.PlayQueueItemDto;
import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.composition.PlayQueueItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;

import static com.github.anrimian.musicplayer.domain.Constants.NO_POSITION;
import static com.github.anrimian.musicplayer.domain.utils.ListUtils.mapList;

/**
 * Created on 02.07.2018.
 */
public class PlayQueueDaoWrapper {

    private final AppDatabase appDatabase;
    private final PlayQueueDao playQueueDao;

    public PlayQueueDaoWrapper(AppDatabase appDatabase, PlayQueueDao playQueueDao) {
        this.appDatabase = appDatabase;
        this.playQueueDao = playQueueDao;
    }

    public Observable<List<PlayQueueCompositionDto>> getPlayQueueObservable() {
        return playQueueDao.getPlayQueueObservable();
    }

    public List<PlayQueueCompositionDto> getFullPlayQueue() {
        return playQueueDao.getFullPlayQueue();
    }

    public List<PlayQueueItem> getPlayQueue(boolean isRandom) {
        return isRandom? getPlayQueueInShuffledOrder(): getPlayQueueInNormalOrder();
    }

    public List<PlayQueueItem> getPlayQueueInNormalOrder() {
        return mapList(playQueueDao.getPlayQueueInNormalOrder(), this::toQueueItem);
    }

    public List<PlayQueueItem> getPlayQueueInShuffledOrder() {
        return mapList(playQueueDao.getPlayQueueInShuffledOrder(), this::toQueueItem);
    }

    public Observable<List<PlayQueueItem>> getPlayQueueObservable(boolean isRandom) {
        return isRandom? getPlayQueueInShuffledOrderObservable(): getPlayQueueInNormalOrderObservable();
    }

    public Observable<List<PlayQueueItem>> getPlayQueueInNormalOrderObservable() {
        return playQueueDao.getPlayQueueInNormalOrderObservable()
                .doOnNext(o -> Log.d("KEK2", "new normal order queue: "))
                .map(list -> mapList(list, this::toQueueItem));
    }

    public Observable<List<PlayQueueItem>> getPlayQueueInShuffledOrderObservable() {
        return playQueueDao.getPlayQueueInShuffledOrderObservable()
                .doOnNext(o -> Log.d("KEK2", "new shuffled order queue: "))
                .map(list -> mapList(list, this::toQueueItem));
    }

    public PlayQueueCompositionDto getPlayQueueItem(long id) {
        return playQueueDao.getPlayQueueEntity(id);
    }

    public void reshuffleQueue(PlayQueueItem currentItem) {
        appDatabase.runInTransaction(() -> {
            List<PlayQueueEntity> list = playQueueDao.getPlayQueue();

            Collections.shuffle(list);

            long firstItemId = list.get(0).getId();
            long currentItemId = currentItem.getId();
            int currentItemPosition = -1;
            for (int i = 0; i < list.size(); i++) {
                PlayQueueEntity entity = list.get(i);

                if (entity.getId() == currentItemId) {
                    currentItemPosition = i;
                }
                entity.setShuffledPosition(i);
            }
            if (currentItemPosition != -1 && firstItemId != currentItemId) {
                list.get(currentItemPosition).setShuffledPosition(0);
                list.get(0).setShuffledPosition(currentItemPosition);
            }

            playQueueDao.deletePlayQueue();
            playQueueDao.insertItems(list);
        });
    }

    public PlayQueueItem insertNewPlayQueue(List<Composition> compositions,
                                            boolean randomPlayingEnabled,
                                            int startPosition) {
        return appDatabase.runInTransaction(() -> {
            List<Composition> shuffledList = new ArrayList<>(compositions);
            long randomSeed = System.nanoTime();
            Collections.shuffle(shuffledList, new Random(randomSeed));

            List<Integer> shuffledPositionList = new ArrayList<>(compositions.size());
            for (int i = 0; i < compositions.size(); i++) {
                shuffledPositionList.add(i);
            }
            Collections.shuffle(shuffledPositionList, new Random(randomSeed));

            List<PlayQueueEntity> entities = new ArrayList<>(compositions.size());
            int shuffledStartPosition = 0;
            for (int i = 0; i < compositions.size(); i++) {
                Composition composition = compositions.get(i);
                PlayQueueEntity playQueueEntity = new PlayQueueEntity();
                playQueueEntity.setAudioId(composition.getId());
                playQueueEntity.setPosition(i);
                int shuffledPosition =  shuffledPositionList.get(i);
                playQueueEntity.setShuffledPosition(shuffledPosition);

                if (startPosition != NO_POSITION && i == startPosition) {
                    shuffledStartPosition = shuffledPosition;
                }

                entities.add(playQueueEntity);
            }

            playQueueDao.deletePlayQueue();
            playQueueDao.insertItems(entities);

            PlayQueueItemDto item;
            if (randomPlayingEnabled) {
                item = playQueueDao.getItemAtShuffledPosition(shuffledStartPosition);
            } else {
                item = playQueueDao.getItemAtPosition(startPosition == NO_POSITION? 0: startPosition);
            }
            return toQueueItem(item);
        });
    }

    public Observable<PlayQueueItem> getItemObservable(long id) {
        return playQueueDao.getItemObservable(id)
                .map(this::toQueueItem);
    }

    public void deleteItem(long itemId) {
        playQueueDao.deleteItem(itemId);
    }

    public void swapItems(PlayQueueItem firstItem,
                          int firstPosition,
                          PlayQueueItem secondItem,
                          int secondPosition,
                          boolean shuffleMode) {
        appDatabase.runInTransaction(() -> {
            if (shuffleMode) {
                playQueueDao.updateShuffledPosition(firstItem.getId(), secondPosition);
                playQueueDao.updateShuffledPosition(secondItem.getId(), firstPosition);
            } else {
                playQueueDao.updateItemPosition(firstItem.getId(), secondPosition);
                playQueueDao.updateItemPosition(secondItem.getId(), firstPosition);
            }
        });
    }

    public List<PlayQueueItem> addCompositionsToEndQueue(List<Composition> compositions) {
        return appDatabase.runInTransaction(() -> {
            int positionToInsert = playQueueDao.getLastPosition();
            List<PlayQueueEntity> entities = toEntityList(compositions, positionToInsert, positionToInsert);
            List<Long> ids = playQueueDao.insertItems(entities);
            return toPlayQueueItems(compositions, ids);
        });
    }

    public List<PlayQueueItem> addCompositionsToQueue(List<Composition> compositions,
                                                      PlayQueueItem currentItem) {
        return appDatabase.runInTransaction(() -> {
            int position = 0;
            int shuffledPosition = 0;
            if (currentItem != null) {
                position = playQueueDao.getPosition(currentItem.getId());
                shuffledPosition = playQueueDao.getShuffledPosition(currentItem.getId());

                int increaseBy = compositions.size();
                playQueueDao.increasePositions(increaseBy, position);
                playQueueDao.increaseShuffledPositions(increaseBy, shuffledPosition);
            }

            List<PlayQueueEntity> entities = toEntityList(compositions, position, shuffledPosition);
            List<Long> ids = playQueueDao.insertItems(entities);
            return toPlayQueueItems(compositions, ids);
        });
    }

    public int getPosition(long id, boolean isShuffle) {
        if (isShuffle) {
            return playQueueDao.getShuffledPosition(id);
        } else {
            return playQueueDao.getPosition(id);
        }
    }

    private List<PlayQueueEntity> toEntityList(List<Composition> compositions,
                                               long randomSeed) {
        List<PlayQueueEntity> entityList = new ArrayList<>(compositions.size());
        List<Integer> shuffledPositionList = new ArrayList<>(compositions.size());
        for (int i = 0; i < compositions.size(); i++) {
            shuffledPositionList.add(i);
        }
        Collections.shuffle(shuffledPositionList, new Random(randomSeed));

        for (int i = 0; i < compositions.size(); i++) {
            Composition composition = compositions.get(i);
            PlayQueueEntity playQueueEntity = new PlayQueueEntity();
            playQueueEntity.setAudioId(composition.getId());
            playQueueEntity.setPosition(i);
            playQueueEntity.setShuffledPosition(shuffledPositionList.get(i));

            entityList.add(playQueueEntity);
        }
        return entityList;
    }

    private List<PlayQueueEntity> toEntityList(List<Composition> compositions,
                                               int position,
                                               int shuffledPosition) {
        List<PlayQueueEntity> entityList = new ArrayList<>(compositions.size());

        for (Composition composition: compositions) {
            PlayQueueEntity playQueueEntity = new PlayQueueEntity();
            playQueueEntity.setAudioId(composition.getId());
            playQueueEntity.setPosition(++position);
            playQueueEntity.setShuffledPosition(++shuffledPosition);

            entityList.add(playQueueEntity);
        }
        return entityList;
    }

    private List<PlayQueueItem> toPlayQueueItems(List<Composition> compositions, List<Long> ids) {
        List<PlayQueueItem> items = new ArrayList<>();

        for (int i = 0; i < compositions.size(); i++) {
            Composition composition = compositions.get(i);
            PlayQueueItem playQueueItem = new PlayQueueItem(ids.get(i), composition);
            items.add(playQueueItem);
        }
        return items;
    }

    private PlayQueueItem toQueueItem(PlayQueueItemDto dto) {
        return new PlayQueueItem(dto.getItemId(), dto.getComposition());
    }
}
