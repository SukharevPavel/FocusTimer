package ru.sukharev.focustimer.utils.app

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class FocusApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}