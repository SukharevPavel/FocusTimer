package ru.sukharev.focustimer.model

interface FocusModel {

    fun switchCounter()

    fun attachListener(listener : Listener)

    fun detachListener(listener : Listener)

    fun getMaxValue() : Int

    fun addCurrentExp(value : Int)

    fun getMaxLevel(): Int

    interface Listener {

        fun onNewValue(value : Int)

        fun onNewLevel(value: Int)

        fun onFocusFinish()

    }

}