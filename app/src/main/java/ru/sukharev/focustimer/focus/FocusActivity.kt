package ru.sukharev.focustimer.focus

import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.widget.ProgressBar
import android.widget.TextView
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.model.FocusModelImpl
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.bind
import ru.sukharev.focustimer.utils.views.LevelAnimator

class FocusActivity : AppCompatActivity(), FocusContract.View {

    override lateinit var presenter: FocusContract.Presenter

    private val focusTextView by bind<TextView>(R.id.focus_text)
    private val focusProgressBar by bind<ProgressBar>(R.id.focus_progress_bar)
    private val focusButton by bind<FloatingActionButton>(R.id.focus_button)
    private val levelProgressBar by bind<ProgressBar>(R.id.focus_level_progress_bar)
    private val levelText by bind<TextView>(R.id.focus_level_text)
    private lateinit var levelAnimator : LevelAnimator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus)
        levelAnimator = LevelAnimator(levelProgressBar, levelText)
        presenter = FocusPresenterImpl(this, FocusModelImpl.getInstance(this))
        focusButton.setOnClickListener{presenter.focusButtonPressed()}
    }

    override fun setMaxValues(maxProgress: Int){
        focusProgressBar.max = maxProgress
    }

    override fun changeTimerAndProgressBar(newValue: String, progress: Int) {
        focusTextView.text = newValue
        focusProgressBar.progress = progress
    }

    override fun setLevel(levelEntry: LevelEntry) {
        levelAnimator.setValues(levelEntry)
    }

    override fun getViewContext(): Context {
        return this
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }


    override fun notifyUserAboutFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrateApi26()
        } else {
            vibrate()
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrateService : Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrateService.vibrate(VIBRATE_TIME_MILLIS)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun vibrateApi26() {
        val vibrateService : Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(VIBRATE_TIME_MILLIS, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrateService.vibrate(vibrationEffect)
    }

    companion object {
        const val  VIBRATE_TIME_MILLIS = 2000L
    }

}
