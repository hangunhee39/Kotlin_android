package hgh.project.parcel.data.repository

import hgh.project.parcel.data.api.SweetTrackerApi
import hgh.project.parcel.data.db.ShippingCompanyDao
import hgh.project.parcel.data.entity.ShippingCompany
import hgh.project.parcel.data.preference.PreferenceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ShippingCompanyRepositoryImpl(
    private val trackerApi: SweetTrackerApi,
    private val shippingCompanyDao: ShippingCompanyDao,
    private val preferenceManager: PreferenceManager,
    private val dispatcher: CoroutineDispatcher
) : ShippingCompanyRepository {

    override suspend fun getSippingCompanies(): List<ShippingCompany> = withContext(dispatcher) {
        val currentTimeMillis = System.currentTimeMillis()
        val lastDatabaseUpdatedTimeMillis = preferenceManager.getLong(
            KEY_LAST_DATABASE_UPDATED_TIME_MILLIS
        )

        if (lastDatabaseUpdatedTimeMillis == null ||
            CACHE_MAX_AGE_MILLIS < (currentTimeMillis - lastDatabaseUpdatedTimeMillis)  //일주일 텀으로 호출
        ) {
            val shippingCompanies = trackerApi.getShippingCompanies()
                .body()
                ?.shippingCompanies
                ?: emptyList()
            shippingCompanyDao.insert(shippingCompanies)
            preferenceManager.putLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS, currentTimeMillis)
        }

        shippingCompanyDao.getAll()
    }

    override suspend fun getRecommendShippingCompany(invoice: String): ShippingCompany? =
        withContext(dispatcher) {
            try {
                trackerApi.getRecommendShippingCompanies(invoice)
                    .body()?.shippingCompanies
                    ?.minByOrNull {
                        it.code.toIntOrNull() ?: Int.MAX_VALUE  //오름차순에서 쳣번째만 가져옴
                    }
            } catch (ex: Exception) {
                null
            }
        }


    companion object {
        private const val KEY_LAST_DATABASE_UPDATED_TIME_MILLIS =
            "KEY_LAST_DATABASE_UPDATED_TIME_MILLIS"
        private const val CACHE_MAX_AGE_MILLIS = 1000L * 60 * 60 * 24 * 7       //일주일
    }
}