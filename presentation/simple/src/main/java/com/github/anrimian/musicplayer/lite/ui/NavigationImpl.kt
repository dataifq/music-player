package com.github.anrimian.musicplayer.lite.ui

import androidx.fragment.app.FragmentManager
import com.github.anrimian.musicplayer.ui.common.navigation.SpecialNavigation

class NavigationImpl: SpecialNavigation {

    override fun attachShortSyncStateFragment(
        fm: FragmentManager,
        containerId: Int,
    ) {
        fm.beginTransaction().add(EmptyFragment(), null).commit()
    }
}