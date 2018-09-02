package ru.sukharev.focustimer.model

import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry

interface IFocusModel {

    fun switchCounter()

    fun attachListener(listener : Listener)

    fun detachListener(listener : Listener)

    fun addCurrentExp(value : Int)

    fun getMaxValue() : Int

    fun notifyFocusFinished(value : Int, interrupted: Boolean)

    fun notifyValueChanged(value : Int)

    fun notifyServerStarted()

    fun notifyServerStopped()

    fun setServiceListener(serviceListener : ITimerService)


    interface Listener {

        fun onNewValue(value : Int)

        fun onNewLevel(levelEntry: LevelEntry)

        fun onFocusFinish(successful: Boolean)

        fun onStateChanged(state: CounterState)

        fun onMaxValueChanged(maxValue: Int)

    }

}