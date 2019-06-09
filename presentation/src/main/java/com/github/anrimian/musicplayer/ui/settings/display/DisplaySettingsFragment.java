package com.github.anrimian.musicplayer.ui.settings.display;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.anrimian.musicplayer.R;
import com.github.anrimian.musicplayer.di.Components;
import com.github.anrimian.musicplayer.ui.common.toolbar.AdvancedToolbar;
import com.github.anrimian.musicplayer.ui.utils.fragments.navigation.FragmentNavigation;
import com.github.anrimian.musicplayer.ui.utils.moxy.ui.MvpAppCompatFragment;
import com.github.anrimian.musicplayer.ui.utils.slidr.SlidrPanel;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.anrimian.musicplayer.ui.utils.ViewUtils.onCheckChanged;
import static com.github.anrimian.musicplayer.ui.utils.ViewUtils.setChecked;

public class DisplaySettingsFragment extends MvpAppCompatFragment implements DisplaySettingsView {

    @InjectPresenter
    DisplaySettingsPresenter presenter;

    @BindView(R.id.nsv_container)
    NestedScrollView nsvContainer;

    @BindView(R.id.cb_covers)
    CheckBox cbCovers;

    @BindView(R.id.cb_covers_in_notification)
    CheckBox cbCoversInNotification;

    @BindView(R.id.cb_colored_notification)
    CheckBox cbColoredNotification;

    @BindView(R.id.cb_notification_on_lock_screen)
    CheckBox cbNotificationOnLockScreen;

    @ProvidePresenter
    DisplaySettingsPresenter providePresenter() {
        return Components.getSettingsComponent().displaySettingsPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        AdvancedToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings);
        toolbar.setSubtitle(R.string.display);
        toolbar.setTitleClickListener(null);

        SlidrConfig slidrConfig = new SlidrConfig.Builder().position(SlidrPosition.LEFT).build();
        SlidrPanel.replace(nsvContainer,
                slidrConfig,
                () -> FragmentNavigation.from(requireFragmentManager()).goBack(0),
                toolbar::onStackFragmentSlided);

        onCheckChanged(cbCovers, presenter::onCoversChecked);
        onCheckChanged(cbCoversInNotification, presenter::onCoversInNotificationChecked);
        onCheckChanged(cbColoredNotification, presenter::onColoredNotificationChecked);
        onCheckChanged(cbNotificationOnLockScreen, presenter::onCoversOnLockScreenChecked);
    }

    @Override
    public void showCoversChecked(boolean checked) {
        setChecked(cbCovers, checked);
    }

    @Override
    public void showCoversInNotificationChecked(boolean checked) {
        setChecked(cbCoversInNotification, checked);
    }

    @Override
    public void showColoredNotificationChecked(boolean checked) {
        setChecked(cbColoredNotification, checked);
    }

    @Override
    public void showCoversOnLockScreenChecked(boolean checked) {
        setChecked(cbNotificationOnLockScreen, checked);
    }

    @Override
    public void showCoversInNotificationEnabled(boolean enabled) {
        cbCoversInNotification.setEnabled(enabled);
    }

    @Override
    public void showColoredNotificationEnabled(boolean enabled) {
        cbColoredNotification.setEnabled(enabled);
    }

    @Override
    public void showShowCoversOnLockScreenEnabled(boolean enabled) {
        cbNotificationOnLockScreen.setEnabled(enabled);
    }
}