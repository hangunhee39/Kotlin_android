package hgh.project.parcel.data.repository

import hgh.project.parcel.data.api.SweetTrackerApi
import hgh.project.parcel.data.db.TrackingItemDao
import hgh.project.parcel.data.entity.TrackingInformation
import hgh.project.parcel.data.entity.TrackingItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.lang.RuntimeException

class TrackingItemRepositoryImpl(
    private val trackerApi: SweetTrackerApi,
    private val trackingItemDao: TrackingItemDao,
    private val dispatcher: CoroutineDispatcher
) : TrackingItemRepository {

    //DB를 옵저빙해서 변경이 있으면 알아챔
    override val trackingItems: Flow<List<TrackingItem>> =
        trackingItemDao.allTrackingItems()
            .distinctUntilChanged() //계속호출방지
            .flowOn(dispatcher)


    override suspend fun getTrackingItemInformation(): List<Pair<TrackingItem, TrackingInformation>> = withContext(dispatcher) {
        trackingItemDao.getAll()
            .mapNotNull { trackingItem ->
                val relatedTrackingInfo = trackerApi.getTrackingInformation(
                    trackingItem.company.code,
                    trackingItem.invoice
                ).body()

                if (relatedTrackingInfo!!.errorMassage.isNullOrBlank()) {
                    null
                } else {    //pair 반환(to)
                    trackingItem to relatedTrackingInfo
                }
            }
            .sortedWith(        //순서 배송단계 낮은순, 시간은 최근순으로 배열
                compareBy(
                    { it.second.level },
                    { -(it.second.lastDetail?.time ?: Long.MAX_VALUE) }
                )
            )
    }

    override suspend fun getTrackingInformation(
        companyCode: String,
        invoice: String
    ): TrackingInformation? = withContext(dispatcher) {
        trackerApi.getTrackingInformation(companyCode, invoice)
            .body()?.sortTrackingDetailsByTimeDescending()
    }

    override suspend fun saveTrackingItem(trackingItem: TrackingItem) = withContext(dispatcher) {
        val trackingInformation = trackerApi.getTrackingInformation(
            trackingItem.company.code,
            trackingItem.invoice
        ).body()

        if (!trackingInformation!!.errorMassage.isNullOrBlank()){
            throw RuntimeException(trackingInformation.errorMassage)
        }

        trackingItemDao.insert(trackingItem)
    }

    override suspend fun deleteTrackingItem(trackingItem: TrackingItem)= withContext(dispatcher) {
        trackingItemDao.delete(trackingItem)
    }

    //깊은 복사(value)
    private fun TrackingInformation.sortTrackingDetailsByTimeDescending() =
        copy(trackingDetails =  trackingDetails?.sortedByDescending { it.time ?:0L })
}