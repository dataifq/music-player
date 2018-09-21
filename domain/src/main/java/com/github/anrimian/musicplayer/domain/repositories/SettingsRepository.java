package com.github.anrimian.musicplayer.domain.repositories;

import com.github.anrimian.musicplayer.domain.models.composition.Order;

/**
 * Created on 14.11.2017.
 */

public interface SettingsRepository {

    void setRandomPlayingEnabled(boolean enabled);

    boolean isRandomPlayingEnabled();

    void setInfinitePlayingEnabled(boolean enabled);

    boolean isInfinitePlayingEnabled();

    void setFolderOrder(Order order);

    void setCompositionsOrder(Order order);

    Order getFolderOrder();

    Order getCompositionsOrder();
}