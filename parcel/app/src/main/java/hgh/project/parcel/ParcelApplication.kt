package hgh.project.parcel

import android.app.Application
import androidx.viewbinding.BuildConfig
import androidx.work.Configuration
import hgh.project.parcel.di.appModule
import hgh.project.parcel.work.AppWorkerFactory
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ParcelApplication : Application(), Configuration.Provider {

    private val workerFactory: AppWorkerFactory by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) {
                    Level.DEBUG
                } else {
                    Level.NONE
                }
            )
            androidContext(this@ParcelApplication)
            modules(appModule)
        }
    }

    //Manifest 추가해야함 (직접적으로 worker 생성 =초기 프로그램 삭제)
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(
                if (BuildConfig.DEBUG) {
                    android.util.Log.DEBUG
                } else {
                    android.util.Log.INFO
                }
            )
            .setWorkerFactory(workerFactory)
            .build()
}