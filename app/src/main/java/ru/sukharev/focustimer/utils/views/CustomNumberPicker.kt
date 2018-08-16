package ru.sukharev.focustimer.utils.views

import android.content.Context

import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import ru.sukharev.focustimer.R


class CustomNumberPicker(context: Context) : NumberPicker(context) {

    override fun addView(child: View) {
        super.addView(child)
        updateView(child)
    }

    override fun addView(child: View, index: Int, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        updateView(child)
    }

    override fun addView(child: View, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, params)
        updateView(child)
    }

    private fun updateView(view: View) {
        if (view is EditText) {
            view.textSize = context.resources.getDimension(R.dimen.number_picker_text_size)
        }
    }

}