package hgh.project.subway_map.data.repository

import hgh.project.subway_map.data.api.StationApi
import hgh.project.subway_map.data.api.StationArrivalsApi
import hgh.project.subway_map.data.db.StationDao
import hgh.project.subway_map.data.db.entity.StationSubwayCrossRefEntity
import hgh.project.subway_map.data.db.entity.mapper.toArrivalInformation
import hgh.project.subway_map.data.db.entity.mapper.toStationEntity
import hgh.project.subway_map.data.db.entity.mapper.toStations
import hgh.project.subway_map.data.preference.PreferenceManager
import hgh.project.subway_map.domain.ArrivalInformation
import hgh.project.subway_map.domain.Station
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StationRepositoryImpl(
    private val stationArrivalsApi:StationArrivalsApi,
    private val stationApi: StationApi,
    private val stationDao: StationDao,
    private val preferenceManager: PreferenceManager,
    private val dispatcher: CoroutineDispatcher
) : StationRepository {

    override val stations: Flow<List<Station>> =
        stationDao.getStationWithSubways()
            .distinctUntilChanged()         //과도한 갱신 막기
            .map { stations -> stations.toStations().sortedByDescending { it.isFavorited } }
            .flowOn(dispatcher)

    //firebase 랑 내부 데이터 똑같게 하기
    override suspend fun refreshStations() = withContext(dispatcher) {
        val fileUpdatedTimeMillis = stationApi.getStationDataUpdatedTimeMillis()    //firebase 갱신 사간
        val lastDatabaseUpdatedTimeMillis =
            preferenceManager.getLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS)    //repository 갱신 시간

        if (lastDatabaseUpdatedTimeMillis == null || fileUpdatedTimeMillis > lastDatabaseUpdatedTimeMillis) {
            stationDao.insertStationSubways(stationApi.getStationSubways())
            preferenceManager.putLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS, fileUpdatedTimeMillis)
        }
    }

    override suspend fun getStationArrivals(stationName: String): List<ArrivalInformation> = withContext(dispatcher) {
        stationArrivalsApi.getRealtimeStationArrivals(stationName)
            .body()
            ?.realtimeArrivalList
            ?.toArrivalInformation()
            ?.distinctBy { it.direction }       //중복처리
            ?.sortedBy { it.subway }            //같은 호선 묶기
            ?: throw RuntimeException("도착 정보를 불러오는 데에 실패했습니다.")
    }

    override suspend fun updateStation(station: Station) = withContext(dispatcher) {
        stationDao.updateStation(station.toStationEntity())
    }



    companion object {
        private const val KEY_LAST_DATABASE_UPDATED_TIME_MILLIS =
            "KEY_LAST_DATABASE_UPDATED_TIME_MILLIS"
    }
}