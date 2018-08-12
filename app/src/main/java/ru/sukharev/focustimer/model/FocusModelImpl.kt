package ru.sukharev.focustimer.model

import android.os.Handler

class FocusModelImpl : FocusModel {

    val listeners = ArrayList<FocusModel.Listener>()
    var state = CounterState.STOPPED
    val handler = Handler()
    val counterChangeRunnable = object: Runnable {
        override fun run() {
            counterValue++;
            handler.postDelayed(this, 1000)
            if (counterValue == MAX_VALUE) {
                dropCounter()
            }
        }
    }
    var counterValue = 0
    set(value) {
        field = value
        onCounterValueChanged()
        if (field == MAX_VALUE) {
            onFocusFinish()
        }
    }

    private fun onCounterValueChanged() {
        for (listener in listeners){
            listener.onNewValue(counterValue)
        }
    }

    private fun onFocusFinish(){
        for (listener in listeners){
            listener.onFocusFinish()
        }
    }

    override fun switchCounter() {
        when (state) {
            CounterState.STARTED -> dropCounter()
            CounterState.STOPPED -> startCounter()
        }
    }

    private fun dropCounter(){
        counterValue = 0;
        state = CounterState.STOPPED
        handler.removeCallbacks(counterChangeRunnable)
    }

    private fun startCounter(){
        counterValue = 0;
        state = CounterState.STARTED
        handler.postDelayed(counterChangeRunnable, 1000)
    }

    override fun attachListener(listener: FocusModel.Listener) {
        listeners.add(listener)
        listener.onNewValue(counterValue)
    }

    override fun detachListener(listener: FocusModel.Listener) {
        listeners.remove(listener)
    }

    override fun getMaxValue(): Int {
        return MAX_VALUE
    }

    companion object {
        private const val MAX_VALUE = 60 * 25

        private var instance : FocusModelImpl? = null

        fun getInstance() : FocusModelImpl{
            return instance?:FocusModelImpl().apply { instance = this }
        }


    }

    enum class CounterState {
        STARTED, STOPPED
    }

}