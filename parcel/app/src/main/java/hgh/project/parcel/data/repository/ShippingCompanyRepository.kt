package hgh.project.parcel.data.repository

import hgh.project.parcel.data.entity.ShippingCompany

interface ShippingCompanyRepository {

    suspend fun getSippingCompanies(): List<ShippingCompany>

    suspend fun getRecommendShippingCompany(invoice: String): ShippingCompany?
}