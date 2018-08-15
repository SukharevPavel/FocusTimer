package ru.sukharev.focustimer.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.Level
import ru.sukharev.focustimer.utils.SUCCESS_MULTIPLIER
import java.util.concurrent.TimeUnit

class FocusModelImpl(applicationContext: Context) : FocusModel {


    val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = applicationContext.getSharedPreferences(FOCUS_PREFS, Context.MODE_PRIVATE)
    }

    val listeners = ArrayList<FocusModel.Listener>()
    var state = CounterState.STOPPED
    set (value) {
        field = value
        onCounterStateChanged()
    }

    private fun onCounterStateChanged() {
        for (listener in listeners) {
            listener.onStateChanged(state)
        }
    }

    val handler = Handler()
    val counterChangeRunnable = object: Runnable {
        override fun run() {
            counterValue++
            handler.postDelayed(this, 1000)
            if (counterValue == MAX_VALUE) {
                dropCounter()
                onFocusFinish()
            }
        }
    }
    var counterValue = 0
    set(value) {
        field = value
        onCounterValueChanged()

    }

    private fun onCounterValueChanged() {
        for (listener in listeners){
            listener.onNewValue(counterValue)
        }
    }

    private fun onLevelValueChanged(){
        val exp = sharedPreferences.getInt(FOCUS_EXP,0)
        for (listener in listeners){
            listener.onNewLevel(Level.getLevelEntry(exp))
        }
    }

    private fun onFocusFinish(){
        for (listener in listeners){
            listener.onFocusFinish()
        }
    }

    override fun switchCounter(){
        when (state) {
            CounterState.STARTED -> dropCounter()
            CounterState.STOPPED -> startCounter()
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun addCurrentExp(value : Int){
        with(sharedPreferences.edit()){
            putInt(FOCUS_EXP, getValidExpValue(value + sharedPreferences.getInt(FOCUS_EXP,0)))
            commit()
        }
        updateExpPrefs()
    }

    private fun getValidExpValue(value: Int) : Int{
        val maxLevel = Level.values()[Level.values().size-1]
        return Math.max(Math.min(value,
                maxLevel.getMinPoints() + maxLevel.maxPoints),0)
    }

    @SuppressLint("ApplySharedPref")
    private fun updateExpPrefs(){
        val curPoints = sharedPreferences.getInt(FOCUS_EXP,0)
        val oldDate = sharedPreferences.getLong(FOCUS_ACCESS_DATE, System.currentTimeMillis())
        val newDate = System.currentTimeMillis()
        val range = newDate  - oldDate
        if (!range.equals((0))) {
            val newPoints = Level.calculateDecrease(curPoints, TimeUnit.MILLISECONDS.toSeconds(range).toInt())
            with(sharedPreferences.edit()){
                putInt(FOCUS_EXP, getValidExpValue(newPoints))
                putLong(FOCUS_ACCESS_DATE, newDate)
                commit()
            }
            onLevelValueChanged()
        }
    }

    private fun dropCounter(){
        if (counterValue == MAX_VALUE) {
            addCurrentExp(counterValue* SUCCESS_MULTIPLIER)
        } else {
            addCurrentExp(counterValue)
        }
        counterValue = 0
        state = CounterState.STOPPED
        handler.removeCallbacks(counterChangeRunnable)
    }

    private fun startCounter(){
        counterValue = 0
        state = CounterState.STARTED
        handler.postDelayed(counterChangeRunnable, 1000)
    }

    override fun attachListener(listener: FocusModel.Listener) {
        listeners.add(listener)
        listener.onNewValue(counterValue)
        val exp = sharedPreferences.getInt(FOCUS_EXP,0)
        listener.onNewLevel(Level.getLevelEntry(exp))
        listener.onStateChanged(state)
    }

    override fun detachListener(listener: FocusModel.Listener) {
        listeners.remove(listener)
    }

    override fun getMaxValue(): Int {
        return MAX_VALUE
    }

    companion object {
        private const val MAX_VALUE = 25 * 60
        private const val FOCUS_PREFS = "focus_preferences"
        private const val FOCUS_ACCESS_DATE = "focus_access_date"
        private const val FOCUS_EXP = "focus_exp"

        private var instance : FocusModelImpl? = null

        fun getInstance(context: Context) : FocusModelImpl{
            return instance?:FocusModelImpl(context.applicationContext).apply {
                updateExpPrefs()
                instance = this }
        }


    }

}