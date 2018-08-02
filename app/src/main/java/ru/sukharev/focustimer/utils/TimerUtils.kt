package ru.sukharev.focustimer.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
private val timerDateFormat = SimpleDateFormat("mm:ss")


fun getReadableTime(value : Int, maxValue: Int) : String {
    val seconds = maxValue - value;
    return timerDateFormat.format(Date(TimeUnit.SECONDS.toMillis(seconds.toLong())))
}