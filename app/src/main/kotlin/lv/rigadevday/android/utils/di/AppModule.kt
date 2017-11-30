package lv.rigadevday.android.utils.di

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import lv.rigadevday.android.repository.DataCache
import lv.rigadevday.android.repository.Repository
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.analytics.Analytics
import lv.rigadevday.android.utils.auth.AuthStorage
import javax.inject.Singleton

@Module
class AppModule(private val application: BaseApp) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideRepository(
        context: Context,
        authStorage: AuthStorage,
        data: DataCache
    ): Repository = Repository(context, authStorage, data)

    @Provides
    @Singleton
    fun provideAnalytics() = Analytics(application)

    @Provides
    @Singleton
    fun provideNotificationManager(): NotificationManager
        = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}
