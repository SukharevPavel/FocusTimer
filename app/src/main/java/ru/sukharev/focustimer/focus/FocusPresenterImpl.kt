package ru.sukharev.focustimer.focus

import ru.sukharev.focustimer.base.BaseView
import ru.sukharev.focustimer.model.IFocusModel
import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.getReadableTime
import javax.inject.Inject

class FocusPresenterImpl @Inject constructor (private val model: IFocusModel): FocusContract.Presenter,
    IFocusModel.Listener{

    private var currentValue = 0

    private var view : FocusContract.View? = null

    override fun setView(view: FocusContract.View) {
        this.view = view
    }

    override fun onMaxValueChanged(maxValue: Int) {
        view?.setMaxValues(getReadableTime(currentValue, maxValue),maxValue)
    }

    override fun onStateChanged(state: CounterState) {
        view?.changeFocusButtonState(state)
    }

    override fun onNewLevel(levelEntry: LevelEntry) {
        view?.setLevel(levelEntry)
    }

    override fun onFocusFinish(successful: Boolean) {
        if (successful) {
            view?.notifyUserAboutFinish()
        }
        onNewValue(0)
    }

    override fun stop() {
        model.detachListener(this)
    }

    override fun start() {
        model.attachListener(this)
    }

    override fun onNewValue(value: Int) {
        currentValue = value
        view?.changeTimerAndProgressBar(getReadableTime(value, model.getMaxValue()),
                value)
    }


    override fun focusButtonPressed() {
        model.switchCounter()
    }


}