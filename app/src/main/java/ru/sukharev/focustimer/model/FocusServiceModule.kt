package ru.sukharev.focustimer.model

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FocusServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeFocusService() : TimerServiceImpl

}