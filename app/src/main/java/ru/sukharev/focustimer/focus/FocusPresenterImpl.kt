package ru.sukharev.focustimer.focus

import ru.sukharev.focustimer.model.FocusModel
import ru.sukharev.focustimer.utils.getReadableTime

class FocusPresenterImpl(val view :FocusContract.View, val model: FocusModel): FocusContract.Presenter,
    FocusModel.Listener{

    override fun onFocusFinish() {
        view.notifyUserAboutFinish()
    }

    override fun stop() {
        model.detachListener(this)
    }

    override fun start() {
        model.attachListener(this)
        view.setMaxValues(model.getMaxValue(), model.getMaxLevel())
    }

    override fun onNewValue(value: Int) {
        view.changeTimerAndProgressBar(getReadableTime(value, model.getMaxValue()),
                value)
    }

    override fun onNewLevel(value: Int){
        view.setLevel(value)
    }

    override fun focusButtonPressed() {
        model.switchCounter()
    }


}