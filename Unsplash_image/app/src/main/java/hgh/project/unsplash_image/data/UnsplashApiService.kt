package hgh.project.unsplash_image.data

import hgh.project.unsplash_image.BuildConfig
import hgh.project.unsplash_image.data.model.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {

    @GET(
        "/photos/random?"+
                "client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}"+
                "&count=30"
    )
    suspend fun getRandomPhotos(
        @Query("query") query: String?
    ): Response<List<PhotoResponse>>
}