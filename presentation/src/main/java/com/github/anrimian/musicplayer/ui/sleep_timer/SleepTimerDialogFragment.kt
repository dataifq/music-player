package com.github.anrimian.musicplayer.ui.sleep_timer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.github.anrimian.musicplayer.R
import com.github.anrimian.musicplayer.databinding.DialogSleepTimerBinding
import com.github.anrimian.musicplayer.di.Components
import com.github.anrimian.musicplayer.domain.interactors.player.SleepTimerInteractor
import com.github.anrimian.musicplayer.ui.common.format.FormatUtils
import com.github.anrimian.musicplayer.ui.sleep_timer.view.TimePickerWrapper2
import moxy.MvpAppCompatDialogFragment
import moxy.ktx.moxyPresenter

class SleepTimerDialogFragment : MvpAppCompatDialogFragment(), SleepTimerView {

    private val presenter by moxyPresenter { Components.getAppComponent().sleepTimerPresenter() }

    private lateinit var viewBinding: DialogSleepTimerBinding

    private lateinit var timePickerWrapper: TimePickerWrapper2

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogSleepTimerBinding.inflate(LayoutInflater.from(context))

        timePickerWrapper = TimePickerWrapper2(
                viewBinding.secondsPicker,
                viewBinding.minutesPicker,
                viewBinding.hoursPicker,
                presenter::onSleepTimerTimeChanged
        )

        viewBinding.btnClose.setOnClickListener { dismissAllowingStateLoss() }

        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.sleep_timer)
                .setView(viewBinding.root)
                .create()
        dialog.show()

        return dialog
    }

    override fun showSleepTimerTime(sleepTimerTimeMillis: Long) {
        timePickerWrapper.showTime(sleepTimerTimeMillis)
    }

    override fun showSleepTimerState(state: SleepTimerInteractor.SleepTimerState) {
        when(state) {
            SleepTimerInteractor.SleepTimerState.ENABLED -> {
                setPickerEnabled(false)
                setTimerEnabled(true)

                viewBinding.btnAction.setText(R.string.stop)
                viewBinding.btnAction.setOnClickListener { presenter.onStopClicked() }

            }
            SleepTimerInteractor.SleepTimerState.DISABLED -> {
                setPickerEnabled(true)
                setTimerEnabled(false)

                viewBinding.btnAction.setText(R.string.start)
                viewBinding.btnAction.setOnClickListener { presenter.onStartClicked() }
            }
            SleepTimerInteractor.SleepTimerState.PAUSED -> {
                setPickerEnabled(false)
                setTimerEnabled(true)

                viewBinding.btnAction.setText(R.string.resume)
                viewBinding.btnAction.setOnClickListener { presenter.onResumeClicked() }
            }
        }
    }

    override fun showRemainingTimeMillis(millis: Long) {
        viewBinding.tvRemainingTime.text = FormatUtils.formatMilliseconds(millis, false)
    }

    private fun setPickerVisibility(visibility: Int) {
        timePickerWrapper.setVisibility(visibility)
        viewBinding.tvHoursDivider.visibility = visibility
        viewBinding.tvMinutesDivider.visibility = visibility
    }

    private fun setPickerEnabled(enabled: Boolean) {
        timePickerWrapper.setEnabled(enabled)
        viewBinding.tvHoursDivider.isEnabled = enabled
        viewBinding.tvMinutesDivider.isEnabled = enabled
    }

    private fun setTimerEnabled(enabled: Boolean) {
        viewBinding.tvRemainingTimeMessage.isEnabled = enabled
        viewBinding.tvRemainingTime.isEnabled = enabled
    }

}