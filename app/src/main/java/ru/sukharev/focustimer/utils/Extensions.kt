package ru.sukharev.focustimer.utils

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View
import java.util.concurrent.TimeUnit

fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy { findViewById<T>(res) }
}

fun toSeconds(minutes : Int) : Int {
    return TimeUnit.MINUTES.toSeconds(minutes.toLong()).toInt()
}