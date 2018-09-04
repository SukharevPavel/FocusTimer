package ru.sukharev.focustimer.utils.app

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.sukharev.focustimer.model.FocusModelModule
import ru.sukharev.focustimer.model.FocusServiceModule
import ru.sukharev.focustimer.utils.di.ActivityBindingModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class,
    FocusModelModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class,
    FocusServiceModule::class])
interface AppComponent : AndroidInjector<FocusApplication>{

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}