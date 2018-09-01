package ru.sukharev.focustimer.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.*
import java.util.concurrent.TimeUnit

class FocusModelImpl private constructor(private val applicationContext: Context) : IFocusModel {


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
    private var serviceListener : ITimerService? = null
    private val listeners = ArrayList<IFocusModel.Listener>()
    private var state = CounterState.STOPPED
    set (value) {
        field = value
        onCounterStateChanged()
    }

    override fun setServiceListener(serviceListener: ITimerService) {
        this.serviceListener = serviceListener
    }

    private fun onCounterStateChanged() {
        for (listener in listeners) {
            listener.onStateChanged(state)
        }
    }

    override fun notifyValueChanged(value: Int) {
        for (listener in listeners){
            listener.onNewValue(value)
        }
    }


    private fun onMaxValueChanged() {
        for (listener in listeners){
            listener.onMaxValueChanged(maxValue())
        }
        serviceListener?.durationChanged(maxValue())
    }

    private fun onLevelValueChanged(){
        val exp = sharedPreferences.getInt(FOCUS_EXP,0)
        for (listener in listeners){
            listener.onNewLevel(Level.getLevelEntry(exp))
        }
    }

    private fun onFocusFinish(successful: Boolean){
        if (successful) {
            sendFocusFinishedNotification(applicationContext)
        }
        for (listener in listeners){
            listener.onFocusFinish(successful)
        }
        serviceListener = null
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

    override fun notifyFocusFinished(value: Int, interrupted: Boolean) {
        if (!interrupted) {
            addCurrentExp(value* SUCCESS_MULTIPLIER)
        } else {
            if (sharedPreferences.getBoolean(applicationContext.getString(R.string.focus_failed_reward_key),
                            applicationContext.resources.getBoolean(R.bool.default_reward_for_unsuccessful))) {
                addCurrentExp(value)
            }
        }
        onFocusFinish(!interrupted)
        state = CounterState.STOPPED
    }

    private fun dropCounter(){
        serviceListener?.dropCounter()
    }

    private fun startCounter(){
        val intent = Intent(applicationContext, TimerServiceImpl::class.java)
        intent.putExtra(TimerServiceImpl.EXTRA_DURATION, maxValue())
        applicationContext.startService(intent)
        state = CounterState.STARTED
    }

    override fun attachListener(listener: IFocusModel.Listener) {
        listeners.add(listener)
        listener.onMaxValueChanged(maxValue())
        val exp = sharedPreferences.getInt(FOCUS_EXP,0)
        listener.onNewLevel(Level.getLevelEntry(exp))
        listener.onStateChanged(state)
    }

    override fun detachListener(listener: IFocusModel.Listener) {
        listeners.remove(listener)
    }


    companion object {
        private const val FOCUS_ACCESS_DATE = "focus_access_date"
        private const val FOCUS_EXP = "focus_exp"

        @SuppressLint("StaticFieldLeak")
        private var instance : FocusModelImpl? = null

        fun getInstance(context: Context) : FocusModelImpl{
            return instance?:FocusModelImpl(context.applicationContext).apply {
                updateExpPrefs()
                onLevelValueChanged()
                instance = this }
        }


    }

}