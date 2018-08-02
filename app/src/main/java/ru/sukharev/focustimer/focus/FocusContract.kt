package ru.sukharev.focustimer.focus

import android.content.Context
import ru.sukharev.focustimer.base.BasePresenter
import ru.sukharev.focustimer.base.BaseView


interface FocusContract {
    interface View : BaseView<Presenter>{

        fun getViewContext() : Context

        fun changeTimerAndProgressBar(newValue: String, progress: Int, maxProgress: Int)
    }

    interface Presenter : BasePresenter{
        fun focusButtonPressed()
    }
}