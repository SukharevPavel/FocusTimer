package ru.sukharev.focustimer

interface FocusView {
    fun changeTimerAndProgressBar(newValue: String, progress: Int)
}

interface FocusPresenter{
    fun focusButtonPressed()
}