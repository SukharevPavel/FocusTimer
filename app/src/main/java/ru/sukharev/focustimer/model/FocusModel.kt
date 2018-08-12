package ru.sukharev.focustimer.model

interface FocusModel {

    fun switchCounter()

    fun attachListener(listener : Listener)

    fun detachListener(listener : Listener)

    fun getMaxValue() : Int


    interface Listener {

        fun onNewValue(value : Int)

        fun onFocusFinish()

    }

}