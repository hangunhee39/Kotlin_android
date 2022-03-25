package org.techtown.book_search.api


import org.techtown.book_search.model.BestSellerDto
import org.techtown.book_search.model.SearchBookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface BookServiceAPI {

    @GET("/api/search.api?output=json")
    fun getBooksByName(
        @Query("key") apiKey:String,
        @Query("query") keyword: String
    ): Call<SearchBookDto>
    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBastSellerBooks(
        @Query("key") apiKey: String
    ): Call<BestSellerDto>
}