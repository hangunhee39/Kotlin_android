package hgh.project.music_play.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {
    @GET("/v3/c6034588-0256-4feb-a7a6-a75e0d2d26c7")
    fun listMusics() : Call<MusicDto>
}