package ru.sukharev.focustimer.focus

import android.content.Context
import ru.sukharev.focustimer.base.BasePresenter
import ru.sukharev.focustimer.base.BaseView
import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry


interface FocusContract {
    interface View : BaseView<Presenter>{

        fun getViewContext() : Context

        fun changeTimerAndProgressBar(newValue: String, progress: Int)

        fun notifyUserAboutFinish()

        fun setMaxValues(newText:String, maxProgress: Int)

        fun setLevel(levelEntry: LevelEntry)

        fun changeFocusButtonState(state: CounterState)
    }

    interface Presenter : BasePresenter<View>{
        fun focusButtonPressed()

    }
}