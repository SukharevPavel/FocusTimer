package ru.sukharev.focustimer.focus

import android.content.Context
import ru.sukharev.focustimer.base.BasePresenter
import ru.sukharev.focustimer.base.BaseView
import ru.sukharev.focustimer.utils.Level


interface FocusContract {
    interface View : BaseView<Presenter>{

        fun getViewContext() : Context

        fun changeTimerAndProgressBar(newValue: String, progress: Int)

        fun notifyUserAboutFinish()

        fun setMaxValues(maxProgress: Int)

        fun setLevel(level: Level, exp: Int)
    }

    interface Presenter : BasePresenter{
        fun focusButtonPressed()
    }
}