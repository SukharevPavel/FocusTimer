package ru.sukharev.focustimer.focus

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.model.FocusModelImpl
import ru.sukharev.focustimer.settings.SettingsActivity
import ru.sukharev.focustimer.utils.CounterState
import ru.sukharev.focustimer.utils.LevelEntry
import ru.sukharev.focustimer.utils.bind
import ru.sukharev.focustimer.utils.playSound
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
        presenter.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.focus_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId().equals(R.id.menu_focus_setting)) {
            val intent = Intent(this@FocusActivity, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setMaxValues(newText: String, maxProgress: Int){
        focusTextView.text = newText
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }

    override fun changeFocusButtonState(state: CounterState) {
        when (state) {
            CounterState.STARTED -> {
                focusButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.focus_button_red))
                focusButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.focus_stop))
            }
            CounterState.STOPPED -> {
                focusButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
                focusButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.focus_start))
            }
        }
    }


    override fun notifyUserAboutFinish() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val alarms = preferences.getString(getString(R.string.focus_ringtone_key),
                getString(R.string.focus_ringtone_default_key))
        val uri = Uri.parse(alarms)
        playSound(baseContext, uri)
        if (preferences.getBoolean(getString(R.string.focus_vibrate_key),
                        applicationContext.resources.getBoolean(R.bool.default_vibrate))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrateApi26()
            } else {
                vibrate()
            }
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
