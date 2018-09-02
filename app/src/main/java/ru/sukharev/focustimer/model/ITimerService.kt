package ru.sukharev.focustimer.model

interface ITimerService {
    fun dropCounter()

    fun durationChanged(duration: Int)

    fun getValue(): Int

}