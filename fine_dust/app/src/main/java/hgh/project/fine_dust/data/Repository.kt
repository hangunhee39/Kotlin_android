package hgh.project.fine_dust.data

import hgh.project.fine_dust.BuildConfig
import hgh.project.fine_dust.data.models.airquality.MeasuredValue
import hgh.project.fine_dust.data.models.monitoringstation.MonitoringStation
import hgh.project.fine_dust.data.services.AirKoreaApiService
import hgh.project.fine_dust.data.services.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {


    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double): MonitoringStation?{
        val tmCoordinates = kakaoLocalApiService.getTmCoordinates(longitude, latitude)
            .body()?.documents?.firstOrNull()

        val tmX= tmCoordinates?.x
        val tmY= tmCoordinates?.y

        return airKoreaApiService.getNearbyMonitoringStation(tmX!!,tmY!!)
            .body()?.response?.body?.monitoringStations?.minByOrNull {
                it.tm ?:Double.MAX_VALUE
            }
    }

    suspend fun getLatestAirQualityData(stationName: String): MeasuredValue? = airKoreaApiService.getRealtimeAirQuality(stationName)
            .body()?.response?.body?.measuredValues?.firstOrNull()

    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    //로깅 목적
    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG){
                        HttpLoggingInterceptor.Level.BODY
                    }else{
                        //배포할땐 로깅 안보이게
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}