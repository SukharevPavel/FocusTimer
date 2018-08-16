package ru.sukharev.focustimer.utils

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.views.CustomNumberPicker


class TimePreference(ctx: Context, attrs: AttributeSet) : DialogPreference(ctx, attrs) {
    private var time = 0
    private val picker: NumberPicker

    init {
        picker = CustomNumberPicker(context)
        picker.minValue = 1
        picker.maxValue = 120
        positiveButtonText = context.getString(R.string.setting_focus_time_set)
        negativeButtonText = context.getString(R.string.setting_focus_time_cancel)
    }

    override fun onCreateDialogView(): View {
        return picker
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker.value = time
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            time = picker.value

            if (callChangeListener(time)) {
                persistInt(time)
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val default = context.resources.getInteger(R.integer.default_focus_time)
        time = getPersistedInt(default)

    }

}