package ru.sukharev.focustimer.utils

fun getReadableTime(value : Int, maxValue: Int) : String {
    val totalSeconds = maxValue - value;
    val minutes = totalSeconds / 60
    val seconds = totalSeconds - minutes * 60
    return String.format("%d:%02d", minutes, seconds)
}