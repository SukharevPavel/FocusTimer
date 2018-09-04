package ru.sukharev.focustimer.utils.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.sukharev.focustimer.focus.FocusActivity;
import ru.sukharev.focustimer.focus.FocusModule;


@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = FocusModule.class)
    abstract FocusActivity focusActivity();

}
