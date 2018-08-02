package ru.sukharev.focustimer.focus

import ru.sukharev.focustimer.model.FocusModel
import ru.sukharev.focustimer.utils.getReadableTime

class FocusPresenterImpl(val view :FocusContract.View, val model: FocusModel): FocusContract.Presenter,
    FocusModel.Listener{

    override fun stop() {
        model.detachListener(this)
    }

    override fun start() {
        model.attachListener(this)
    }

    override fun onNewValue(value: Int) {
        view.changeTimerAndProgressBar(getReadableTime(value, model.getMaxValue()),
                value,
                model.getMaxValue())
    }

    override fun focusButtonPressed() {
        model.switchCounter()
    }


}