package ru.sukharev.focustimer.base

interface BasePresenter<T> {

    fun setView(view : T)

    fun start()

    fun stop()

}