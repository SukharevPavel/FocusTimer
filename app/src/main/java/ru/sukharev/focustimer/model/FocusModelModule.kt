package ru.sukharev.focustimer.model

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
class FocusModelModule {

    @Singleton
    @Provides
    fun provideFocusModel(application : Application) : IFocusModel {
        return FocusModelImpl.getInstance(application.applicationContext)
    }

}