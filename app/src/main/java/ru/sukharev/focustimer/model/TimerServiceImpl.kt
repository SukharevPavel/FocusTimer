package ru.sukharev.focustimer.model

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import ru.sukharev.focustimer.BuildConfig
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.focus.FocusActivity
import ru.sukharev.focustimer.utils.FOCUS_PROCESS_NOTIFICATION_ID
import ru.sukharev.focustimer.utils.NOTIFICATION_CHANNEL_STRING
import ru.sukharev.focustimer.utils.createNotificationChannelIfNeed
import ru.sukharev.focustimer.utils.getReadableTime

class TimerServiceImpl : Service(), ITimerService {

    private lateinit var serviceLooper: Looper
    private lateinit var serviceHandler: Handler
    private lateinit var model : IFocusModel
    private var defaultFunc  = {
        applicationContext.resources.getInteger(R.integer.focus_time_default_value)
    }
    private var durationValue : Int? = null
        set(value) {
            field = value
            updateNotification()
        }
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
    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finishFocus(true)
        }
    }
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private fun finishFocus(interrupted: Boolean) {
        executeOnMainThread {
            model.notifyFocusFinished(counterValue, interrupted)
        }
        serviceHandler.removeCallbacks(counterChangeRunnable)
        stopForeground(true)
        stopService()
    }

    private fun stopService() {
        val startIdVal = startId
        if (startIdVal != null) {
            stopSelf(startIdVal)
        } else {
            stopSelf()
        }
        executeOnMainThread {
            model.notifyServerStopped()
        }
        unregisterReceiver(stopReceiver)
    }

    private var startMillis : Long? = null
    private var counterValue = 0
        set(value) {
            field = value
            updateNotification()
            executeOnMainThread {
                model.notifyValueChanged(counterValue)
            }
        }

    override fun durationChanged(duration: Int) {
        this.durationValue = duration
    }

    private fun executeOnMainThread(function: () -> Unit) {
        mainHandler.post {
            function()
        }
    }

    override fun dropCounter() {
        finishFocus(true)
    }

    override fun getValue(): Int {
        return counterValue
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
        startForeground(FOCUS_PROCESS_NOTIFICATION_ID, createServiceNotification())
        durationValue = intent.getIntExtra(EXTRA_DURATION, defaultFunc())
        counterValue = 0
        startMillis = System.currentTimeMillis()
        executeOnMainThread {
            model.setServiceListener(this)
            model.notifyServerStarted()
        }
        serviceHandler.post(counterChangeRunnable)
        registerReceiver(stopReceiver, IntentFilter(intentStopTimer))
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
        private const val intentStopTimer = BuildConfig.APPLICATION_ID + ".stopTimerBroadcast"
    }

    private fun createServiceNotification(): Notification {
        createNotificationChannelIfNeed(this)
        val stopIntent = Intent().apply {
            action = intentStopTimer
        }

        val activityIntent = Intent(applicationContext, FocusActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext,
                FOCUS_PROCESS_NOTIFICATION_ID,
                activityIntent,
                0)
        val stopPendingIntent = PendingIntent.getBroadcast(applicationContext,
                0,
                stopIntent,
                0)
        notificationBuilder = NotificationCompat.Builder(applicationContext,
                NOTIFICATION_CHANNEL_STRING)
                .addAction(R.drawable.focus_stop, getString(R.string.stop), stopPendingIntent)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_focus_processing))
                .setSubText(getReadableTime(counterValue, durationFunc()))
                .setOngoing(true)
                .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }

    private fun updateNotification() {
        notificationBuilder.setSubText(getReadableTime(counterValue, durationFunc()))
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(FOCUS_PROCESS_NOTIFICATION_ID, notificationBuilder.build())
    }

}