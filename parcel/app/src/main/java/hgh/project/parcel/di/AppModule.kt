package hgh.project.parcel.di

import android.app.Activity
import hgh.project.parcel.BuildConfig
import hgh.project.parcel.data.api.SweetTrackerApi
import hgh.project.parcel.data.api.Url
import hgh.project.parcel.data.db.AppDatabase
import hgh.project.parcel.data.entity.TrackingInformation
import hgh.project.parcel.data.entity.TrackingItem
import hgh.project.parcel.data.preference.PreferenceManager
import hgh.project.parcel.data.preference.SharedPreferenceManager
import hgh.project.parcel.data.repository.*
import hgh.project.parcel.presentation.addtrackingitem.AddTrackingItemFragment
import hgh.project.parcel.presentation.addtrackingitem.AddTrackingItemPresenter
import hgh.project.parcel.presentation.addtrackingitem.AddTrackingItemsContract
import hgh.project.parcel.presentation.trackinghistory.TrackingHistoryContract
import hgh.project.parcel.presentation.trackinghistory.TrackingHistoryFragment
import hgh.project.parcel.presentation.trackinghistory.TrackingHistoryPresenter
import hgh.project.parcel.presentation.trackingitems.TrackingItemsContract
import hgh.project.parcel.presentation.trackingitems.TrackingItemsFragment
import hgh.project.parcel.presentation.trackingitems.TrackingItemsPresenter
import hgh.project.parcel.work.AppWorkerFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
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

    //db
    single { AppDatabase.build(androidApplication()) }
    single { get<AppDatabase>().trackingItemDao() }
    single { get<AppDatabase>().shippingCompanyDao() }

    //api
    single {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single<SweetTrackerApi> {
        Retrofit.Builder().baseUrl(Url.SWEET_TRACKER_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }

    // Work
    single { AppWorkerFactory(get(), get()) }

    //Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Repository
    single<TrackingItemRepository> { TrackingItemRepositoryImpl(get(), get(), get()) }
    single<ShippingCompanyRepository> { ShippingCompanyRepositoryImpl(get(), get(), get(), get()) }

    //Presentation
    scope<TrackingItemsFragment> {
        scoped<TrackingItemsContract.Presenter> { TrackingItemsPresenter(getSource(), get()) }
    }
    scope<AddTrackingItemFragment> {
        scoped<AddTrackingItemsContract.Presenter> {
            AddTrackingItemPresenter(getSource(), get(), get())
        }
    }
    scope<TrackingHistoryFragment> {
        scoped<TrackingHistoryContract.Presenter> { (item: TrackingItem, information: TrackingInformation) ->
            TrackingHistoryPresenter(getSource(), get(), item, information)
        }
    }

}