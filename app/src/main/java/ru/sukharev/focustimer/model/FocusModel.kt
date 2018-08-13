package ru.sukharev.focustimer.model

import ru.sukharev.focustimer.utils.LevelEntry

interface FocusModel {

    fun switchCounter()

    fun attachListener(listener : Listener)

    fun detachListener(listener : Listener)

    fun getMaxValue() : Int

    fun addCurrentExp(value : Int)

    interface Listener {

        fun onNewValue(value : Int)

        fun onNewLevel(levelEntry: LevelEntry)

        fun onFocusFinish()

    }

}