package hgh.project.unsplash_image.data

import hgh.project.unsplash_image.BuildConfig
import hgh.project.unsplash_image.data.model.PhotoResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {

    private val unsplashApiService: UnsplashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
            .create()
    }

    suspend fun getRandomPhotos(query: String?):List<PhotoResponse>? =
        unsplashApiService.getRandomPhotos(query).body()

    private fun buildOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =if (BuildConfig.DEBUG){
                        HttpLoggingInterceptor.Level.BODY
                    } else{
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}