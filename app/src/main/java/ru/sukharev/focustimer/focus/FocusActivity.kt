package ru.sukharev.focustimer.focus

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.ProgressBar
import android.widget.TextView
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.model.FocusModelImpl
import ru.sukharev.focustimer.utils.bind

class FocusActivity : AppCompatActivity(), FocusContract.View {


    override lateinit var presenter: FocusContract.Presenter

    private val focusTextView by bind<TextView>(R.id.focus_text)
    private val focusProgressBar by bind<ProgressBar>(R.id.focus_progress_bar)
    private val focusButton by bind<FloatingActionButton>(R.id.focus_button)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus)
        presenter = FocusPresenterImpl(this, FocusModelImpl.getInstance())
        focusButton.setOnClickListener{presenter.focusButtonPressed()}
    }

    override fun changeTimerAndProgressBar(newValue: String, progress: Int, maxProgress: Int) {
        focusTextView.text = newValue;
        focusProgressBar.progress = progress
        focusProgressBar.max = maxProgress
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


}
