package ru.sukharev.focustimer.focus

import ru.sukharev.focustimer.model.FocusModel
import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.getReadableTime

class FocusPresenterImpl(private val view :FocusContract.View, private val model: FocusModel): FocusContract.Presenter,
    FocusModel.Listener{

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
        view.setMaxValues(model.getMaxValue())
    }

    override fun onNewValue(value: Int) {
        view.changeTimerAndProgressBar(getReadableTime(value, model.getMaxValue()),
                value)
    }


    override fun focusButtonPressed() {
        model.switchCounter()
    }


}