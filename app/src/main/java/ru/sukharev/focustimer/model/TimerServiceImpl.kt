package ru.sukharev.focustimer.model

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import ru.sukharev.focustimer.R

class TimerServiceImpl : IntentService(NAME) ,ITimerService{

    private lateinit var model : IFocusModel
    private var defaultFunc  = {
        applicationContext.resources.getInteger(R.integer.focus_time_default_value)
    }
    private var durationValue : Int? = null
    private var durationFunc = {
        durationValue?:defaultFunc()
    }
    private val handler = Handler()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val counterChangeRunnable = object: Runnable {
        override fun run() {
            val startMillisVal = startMillis
            counterValue = if (startMillisVal != null)
                ((System.currentTimeMillis() - startMillisVal)/ REFRESH_PERIOD).toInt()
            else counterValue+1
            handler.postDelayed(this, 1000)
            if (counterValue >= durationFunc()) {
                finishFocus()
            }
        }
    }

    private fun finishFocus() {
        mainHandler.post {
            model.notifyFocusFinished(counterValue, false) }
        handler.removeCallbacks(counterChangeRunnable)
    }

    private var startMillis : Long? = null
    private var counterValue = 0
    set(value) {
        field = value
        mainHandler.post {
            model.notifyValueChanged(counterValue)
        }
    }

    override fun durationChanged(duration: Int) {
        this.durationValue = duration
    }

    override fun dropCounter() {
        mainHandler.post{
            model.notifyFocusFinished(counterValue, true)
            handler.removeCallbacks(counterChangeRunnable)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        model = FocusModelImpl.getInstance(this)
        model.setServiceListener(this)
        durationValue = intent?.getIntExtra(EXTRA_DURATION, defaultFunc())
        counterValue = 0
        startMillis = System.currentTimeMillis()
      //  startForeground()
        handler.post(counterChangeRunnable)
    }

    companion object {

        private const val REFRESH_PERIOD = 1000L
        private val NAME = "timer"
        val EXTRA_DURATION = "extra_duration"
    }

}