package hgh.project.parcel.data.api

import hgh.project.parcel.BuildConfig
import hgh.project.parcel.data.entity.ShippingCompanies
import hgh.project.parcel.data.entity.ShippingCompany
import hgh.project.parcel.data.entity.TrackingInformation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SweetTrackerApi {

    @GET("api/v1/trackingInfo?t_key=${BuildConfig.TRACKING_API_KEY}")
    suspend fun getTrackingInformation(
        @Query("t_code")companyCode: String,
        @Query("t_invoice")invoice:String
    ): Response<TrackingInformation>

    @GET("api/v1/companylist?t_key=${BuildConfig.TRACKING_API_KEY}")
    suspend fun getShippingCompanies(): Response<ShippingCompanies>

    @GET("api/v1/recommend?t_key=${BuildConfig.TRACKING_API_KEY}")
    suspend fun getRecommendShippingCompanies(
        @Query("t_invoice")invoice: String
    ) : Response<ShippingCompanies>

}