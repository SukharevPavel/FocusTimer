package ru.sukharev.focustimer.utils

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import ru.sukharev.focustimer.R
import ru.sukharev.focustimer.utils.views.CustomNumberPicker

private const val STEP = 5
private const val MIN_VALUE = 5
private const val MAX_VALUE = 120

class TimePreference(ctx: Context, attrs: AttributeSet) : DialogPreference(ctx, attrs) {
    private var time = 0

    private lateinit var picker: NumberPicker


    override fun onCreateDialogView(): View {
        picker = CustomNumberPicker(context)
        val maxValue = (MAX_VALUE - MIN_VALUE)/STEP
        picker.minValue = 0
        picker.maxValue = maxValue
        val array = Array(maxValue+1) { i -> (MIN_VALUE + i * STEP).toString()}
        picker.displayedValues = array
        positiveButtonText = context.getString(R.string.setting_focus_time_set)
        negativeButtonText = context.getString(R.string.setting_focus_time_cancel)
        return picker
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker.value = picker.displayedValues.indexOf<String>(time.toString())
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            time = picker.displayedValues.get(picker.value).toInt()

            if (callChangeListener(time)) {
                persistInt(time)
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val default = context.resources.getInteger(R.integer.focus_time_default_value)
        time = getPersistedInt(default)

    }


}