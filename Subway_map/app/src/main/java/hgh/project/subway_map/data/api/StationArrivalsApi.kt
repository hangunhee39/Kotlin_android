package hgh.project.subway_map.data.api

import hgh.project.subway_map.BuildConfig
import hgh.project.subway_map.data.repository.RealtimeStationArrivals
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StationArrivalsApi {

    @GET("api/subway/${BuildConfig.API_ACCESS_KEY}/json/realtimeStationArrival/0/100/{stationName}")
    suspend fun getRealtimeStationArrivals(@Path("stationName") stationName: String): Response<RealtimeStationArrivals>
}