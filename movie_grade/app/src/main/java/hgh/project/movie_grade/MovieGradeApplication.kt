package hgh.project.movie_grade

import android.app.Application
import hgh.project.movie_grade.di.appModule
import hgh.project.movie_grade.di.dataModule
import hgh.project.movie_grade.di.domainModule
import hgh.project.movie_grade.di.presenterModule
import hgh.project.movie_grade.utility.MovieDataGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MovieGradeApplication: Application() {

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
            androidContext(this@MovieGradeApplication)
            //모델은 나눠서도 가능
            modules(appModule + dataModule + domainModule + presenterModule)
        }

        //MovieDataGenerator().generate()
    }
}