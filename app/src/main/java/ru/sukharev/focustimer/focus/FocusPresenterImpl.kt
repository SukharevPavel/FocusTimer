package ru.sukharev.focustimer.focus

import ru.sukharev.focustimer.model.FocusModel
import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.getReadableTime

class FocusPresenterImpl(private val view :FocusContract.View, private val model: FocusModel): FocusContract.Presenter,
    FocusModel.Listener{

    private var currentValue = 0

    override fun onMaxValueChanged(maxValue: Int) {
        view.setMaxValues(getReadableTime(currentValue, maxValue),maxValue)
    }

    override fun onStateChanged(state: CounterState) {
        view.changeFocusButtonState(state)
    }

    override fun onNewLevel(levelEntry: LevelEntry) {
        view.setLevel(levelEntry)
    }

    override fun onFocusFinish() {
        view.notifyUserAboutFinish()
    }

    override fun stop() {
        model.detachListener(this)
    }

    override fun start() {
        model.attachListener(this)
    }

    override fun onNewValue(value: Int) {
        currentValue = value
        view.changeTimerAndProgressBar(getReadableTime(value, model.getMaxValue()),
                value)
    }


    override fun focusButtonPressed() {
        model.switchCounter()
    }


}