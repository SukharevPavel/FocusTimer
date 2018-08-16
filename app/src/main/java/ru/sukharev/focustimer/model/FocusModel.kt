package ru.sukharev.focustimer.model

import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry

interface FocusModel {

    fun switchCounter()

    fun attachListener(listener : Listener)

    fun detachListener(listener : Listener)

    fun addCurrentExp(value : Int)

    fun getMaxValue() : Int

    interface Listener {

        fun onNewValue(value : Int)

        fun onNewLevel(levelEntry: LevelEntry)

        fun onFocusFinish()

        fun onStateChanged(state: CounterState)

        fun onMaxValueChanged(maxValue: Int)

    }

}