package hgh.project.youtube_p.service

import hgh.project.youtube_p.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/055b90c6-502f-4ba6-b961-fae22e856bb8")
    fun listVideos(): Call<VideoDto>
}