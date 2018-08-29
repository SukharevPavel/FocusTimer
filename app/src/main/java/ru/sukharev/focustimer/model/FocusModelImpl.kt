package ru.sukharev.focustimer.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.preference.PreferenceManager
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.*
import java.util.concurrent.TimeUnit

class FocusModelImpl(private val applicationContext: Context) : FocusModel {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    private val preferencesListener = SharedPreferences.OnSharedPreferenceChangeListener {
        _, key ->
        if (key == applicationContext.getString(R.string.focus_time_key)){
            onMaxValueChanged()
        }
    }
    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    private val maxValue = {
        toSeconds(sharedPreferences.getInt(applicationContext.getString(R.string.focus_time_key),
            applicationContext.resources.getInteger(R.integer.focus_time_default_value)))}
    private val listeners = ArrayList<FocusModel.Listener>()
    private var state = CounterState.STOPPED
    set (value) {
        field = value
        onCounterStateChanged()
    }
    private val handler = Handler()
    private val counterChangeRunnable = object: Runnable {
        override fun run() {
            val startMillisVal = startMillis
            counterValue = if (startMillisVal != null)
                ((System.currentTimeMillis() - startMillisVal)/ REFRESH_PERIOD).toInt()
            else counterValue+1
            handler.postDelayed(this, 1000)
            if (counterValue >= maxValue()) {
                dropCounter()
                onFocusFinish()
            }
        }
    }
    private var counterValue = 0
    set(value) {
        field = value
        onCounterValueChanged()

    }
    private var startMillis : Long? = null

    private fun onCounterStateChanged() {
        for (listener in listeners) {
            listener.onStateChanged(state)
        }
    }

    private fun onCounterValueChanged() {
        for (listener in listeners){
            listener.onNewValue(counterValue)
        }
    }

    private fun onMaxValueChanged() {
        for (listener in listeners){
            listener.onMaxValueChanged(maxValue())
        }
    }

    private fun onLevelValueChanged(){
        val exp = sharedPreferences.getInt(FOCUS_EXP,0)
        for (listener in listeners){
            listener.onNewLevel(Level.getLevelEntry(exp))
        }
    }

    private fun onFocusFinish(){
        sendFocusFinishedNotification(applicationContext)
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

    override fun getMaxValue(): Int {
        return maxValue()
    }

    @SuppressLint("ApplySharedPref")
    override fun addCurrentExp(value : Int){
        updateExpPrefs()
        with(sharedPreferences.edit()){
            putInt(FOCUS_EXP, getValidExpValue(value + sharedPreferences.getInt(FOCUS_EXP,0)))
            commit()
        }
        onLevelValueChanged()
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
        if (range != 0L) {
            val newPoints = Level.calculateDecrease(curPoints, TimeUnit.MILLISECONDS.toSeconds(range).toInt())
            with(sharedPreferences.edit()){
                putInt(FOCUS_EXP, getValidExpValue(newPoints))
                putLong(FOCUS_ACCESS_DATE, newDate)
                commit()
            }
        }
    }

    private fun dropCounter(){
        if (counterValue == maxValue()) {
            addCurrentExp(counterValue* SUCCESS_MULTIPLIER)
        } else {
            if (sharedPreferences.getBoolean(applicationContext.getString(R.string.focus_failed_reward_key),
                            applicationContext.resources.getBoolean(R.bool.default_reward_for_unsuccessful))) {
                addCurrentExp(counterValue)
            }
        }
        counterValue = 0
        state = CounterState.STOPPED
        handler.removeCallbacks(counterChangeRunnable)
    }

    private fun startCounter(){
        counterValue = 0
        startMillis = System.currentTimeMillis()
        state = CounterState.STARTED
        handler.postDelayed(counterChangeRunnable, REFRESH_PERIOD)
    }

    override fun attachListener(listener: FocusModel.Listener) {
        listeners.add(listener)
        listener.onMaxValueChanged(maxValue())
        listener.onNewValue(counterValue)
        val exp = sharedPreferences.getInt(FOCUS_EXP,0)
        listener.onNewLevel(Level.getLevelEntry(exp))
        listener.onStateChanged(state)
    }

    override fun detachListener(listener: FocusModel.Listener) {
        listeners.remove(listener)
    }


    companion object {
        private const val REFRESH_PERIOD = 1000L
        private const val FOCUS_ACCESS_DATE = "focus_access_date"
        private const val FOCUS_EXP = "focus_exp"

        @SuppressLint("StaticFieldLeak")
        private var instance : FocusModelImpl? = null

        fun getInstance(context: Context) : FocusModelImpl{
            return instance?:FocusModelImpl(context.applicationContext).apply {
                updateExpPrefs()
                instance = this }
        }


    }

}