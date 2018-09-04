package ru.sukharev.focustimer.focus

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.sukharev.focustimer.model.IFocusModel
import ru.sukharev.focustimer.utils.di.ActivityScoped

@Module
class FocusModule {

    @ActivityScoped
    @Provides
    fun focusPresenter(model: IFocusModel): FocusContract.Presenter {
        return FocusPresenterImpl(model)
    }
}