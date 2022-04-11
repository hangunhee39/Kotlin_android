package hgh.project.airbnb

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("https://run.mocky.io/v3/2e23c302-1e98-44b3-a8d2-f0ddfb568096")
    fun getHouseList(): Call<HouseDto>
}