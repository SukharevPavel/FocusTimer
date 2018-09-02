package ru.sukharev.focustimer.model

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import ru.sukharev.focustimer.BuildConfig
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.focus.FocusActivity
import ru.sukharev.focustimer.utils.NOTIFICATION_CHANNEL_STRING
import ru.sukharev.focustimer.utils.NOTIFICATION_ID
import ru.sukharev.focustimer.utils.createNotificationChannelIfNeed

class TimerServiceImpl : Service(), ITimerService {

    private lateinit var serviceLooper: Looper
    private lateinit var serviceHandler: Handler
    private lateinit var model : IFocusModel
    private var defaultFunc  = {
        applicationContext.resources.getInteger(R.integer.focus_time_default_value)
    }
    private var durationValue : Int? = null
    private var startId: Int? = null
    private var durationFunc = {
        durationValue?:defaultFunc()
    }
    private val mainHandler = Handler(Looper.getMainLooper())
    private val counterChangeRunnable = object: Runnable {
        override fun run() {
            val startMillisVal = startMillis
            counterValue = if (startMillisVal != null)
                ((System.currentTimeMillis() - startMillisVal)/ REFRESH_PERIOD).toInt()
            else counterValue+1
            serviceHandler.postDelayed(this, 1000)
            if (counterValue >= durationFunc()) {
                finishFocus(false)
            }
        }
    }

    private fun finishFocus(interrupted: Boolean) {
        mainHandler.post {
            model.notifyFocusFinished(counterValue, interrupted)
        }
        serviceHandler.removeCallbacks(counterChangeRunnable)
        stopForeground(true)
        val startIdVal = startId
        if (startIdVal != null) {
            stopSelf(startIdVal)
        } else {
            stopSelf()
        }
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
        finishFocus(true)
    }


    override fun onCreate() {
        super.onCreate()
        val thread = HandlerThread(NAME)
        thread.start()
        serviceLooper = thread.looper
        serviceHandler = Handler(serviceLooper)
    }


    override fun onStartCommand(@Nullable intent: Intent, flags: Int, startId: Int): Int {
        this.startId = startId
        model = FocusModelImpl.getInstance(this)
        model.setServiceListener(this)
        durationValue = intent.getIntExtra(EXTRA_DURATION, defaultFunc())
        counterValue = 0
        startMillis = System.currentTimeMillis()
        startForeground(100, createServiceNotification())
        serviceHandler.post(counterChangeRunnable)
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceLooper.quit()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        private const val REFRESH_PERIOD = 1000L
        private const val NAME = "TimerHandlerThread"
        const val EXTRA_DURATION = "extra_duration"
        private const val broadcastStopTimer = BuildConfig.APPLICATION_ID + ".stopTimerBroadcast"
    }

    private fun createServiceNotification(): Notification {
        createNotificationChannelIfNeed(this)
        val stopIntent = Intent().apply {
            action = broadcastStopTimer
        }

        val activityIntent = Intent(applicationContext, FocusActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext,
                NOTIFICATION_ID,
                activityIntent,
                PendingIntent.FLAG_ONE_SHOT)
        val stopPendingIntent = PendingIntent.getBroadcast(applicationContext,
                0,
                stopIntent,
                0)
        val builder = NotificationCompat.Builder(applicationContext,
                NOTIFICATION_CHANNEL_STRING)
                .addAction(R.drawable.focus_stop, getString(R.string.stop), stopPendingIntent)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_focus_finished))
                .setOngoing(true)
                .setContentIntent(pendingIntent)

        return builder.build()
    }

}