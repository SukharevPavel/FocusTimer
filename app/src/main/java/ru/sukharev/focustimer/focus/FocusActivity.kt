package ru.sukharev.focustimer.focus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.bind

class FocusActivity : AppCompatActivity(), FocusContract.View {
    override lateinit var presenter: FocusContract.Presenter

    private val focusTextView by bind<TextView>(R.id.focus_text)
    private val focusProgressBar by bind<ProgressBar>(R.id.focus_progress_bar)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus)
    }

    override fun changeTimerAndProgressBar(newValue: String, progress: Int) {
        focusTextView.setText(newValue);
        focusProgressBar.setProgress(progress)
    }
}
