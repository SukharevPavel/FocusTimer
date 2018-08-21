package ru.sukharev.focustimer.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build

fun playSound(context: Context, alert: Uri) {
    if (alert.toString().isEmpty()) {
        return
    }
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(context, alert)
    val audioManager = context
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
    if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
            mediaPlayer.setAudioAttributes(attributes.build())
        } else {
            @Suppress("DEPRECATION")
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
        }
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}