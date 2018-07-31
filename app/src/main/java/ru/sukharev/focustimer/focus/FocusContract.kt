package ru.sukharev.focustimer.focus

import ru.sukharev.focustimer.base.BasePresenter
import ru.sukharev.focustimer.base.BaseView


interface FocusContract {
    interface View : BaseView<Presenter>{
        fun changeTimerAndProgressBar(newValue: String, progress: Int)
    }

    interface Presenter : BasePresenter{
        fun focusButtonPressed()
    }
}