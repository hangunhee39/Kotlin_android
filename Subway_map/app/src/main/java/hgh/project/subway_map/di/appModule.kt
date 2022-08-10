package hgh.project.subway_map.di

import android.app.Activity
import androidx.viewbinding.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import hgh.project.subway_map.data.api.StationApi
import hgh.project.subway_map.data.api.StationArrivalsApi
import hgh.project.subway_map.data.api.StationStorageApi
import hgh.project.subway_map.data.api.Url
import hgh.project.subway_map.data.db.AppDatabase
import hgh.project.subway_map.data.preference.PreferenceManager
import hgh.project.subway_map.data.preference.SharedPreferenceManager
import hgh.project.subway_map.data.repository.StationRepository
import hgh.project.subway_map.data.repository.StationRepositoryImpl
import hgh.project.subway_map.presenter.stationarrivals.StationArrivalsContract
import hgh.project.subway_map.presenter.stationarrivals.StationArrivalsFragment
import hgh.project.subway_map.presenter.stationarrivals.StationArrivalsPresenter
import hgh.project.subway_map.presenter.stations.StationPresenter
import hgh.project.subway_map.presenter.stations.StationsContract
import hgh.project.subway_map.presenter.stations.StationsFragment
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val appModule = module {

    single { Dispatchers.IO }

    // Database
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().stationDao() }

    // Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Api
    single<StationApi> { StationStorageApi(Firebase.storage) }
    single{
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG){
                        HttpLoggingInterceptor.Level.BODY
                    }else{
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single<StationArrivalsApi>{
        Retrofit.Builder().baseUrl(Url.SEOUL_DATA_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }


    // Repository
    single<StationRepository> { StationRepositoryImpl(get(),get(), get(), get(), get()) }

    // Presentation
    scope<StationsFragment> {
        scoped<StationsContract.Presenter> { StationPresenter(getSource(), get()) } //프래그먼트 종료시 presenter 종료 , scope 내에서는 서로 공유 가능
    }
    scope<StationArrivalsFragment> {
        scoped<StationArrivalsContract.Presenter> { StationArrivalsPresenter(getSource(), get(), get()) }
    }

}