package ru.sukharev.focustimer.focus

class FocusPresenterImpl(view :FocusContract.View): FocusContract.Presenter {

    init {
        view.presenter = this
    }
    override fun start() {

    }

    override fun focusButtonPressed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}