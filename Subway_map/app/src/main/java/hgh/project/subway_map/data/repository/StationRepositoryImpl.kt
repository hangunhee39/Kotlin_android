package hgh.project.subway_map.data.repository

import hgh.project.subway_map.data.api.StationApi
import hgh.project.subway_map.data.db.StationDao
import hgh.project.subway_map.data.db.entity.StationSubwayCrossRefEntity
import hgh.project.subway_map.data.db.entity.mapper.toStations
import hgh.project.subway_map.data.preference.PreferenceManager
import hgh.project.subway_map.domain.Station
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StationRepositoryImpl(
    private val stationApi: StationApi,
    private val stationDao: StationDao,
    private val preferenceManager: PreferenceManager,
    private val dispatcher: CoroutineDispatcher
) : StationRepository {

    override val stations: Flow<List<Station>> =
        stationDao.getStationWithSubways()
            .distinctUntilChanged()         //과도한 갱신 막기
            .map { it.toStations() }
            .flowOn(dispatcher)

    override suspend fun refreshStations() = withContext(dispatcher) {
        val fileUpdatedTimeMillis = stationApi.getStationDataUpdatedTimeMillis()
        val lastDatabaseUpdatedTimeMillis = preferenceManager.getLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS)

        if (lastDatabaseUpdatedTimeMillis == null || fileUpdatedTimeMillis > lastDatabaseUpdatedTimeMillis) {
            val stationSubways = stationApi.getStationSubways()
            stationDao.insertStations(stationSubways.map { it.first })
            stationDao.insertSubways(stationSubways.map { it.second })
            stationDao.insertCrossReferences(
                stationSubways.map { (station, subway) ->
                    StationSubwayCrossRefEntity(
                        station.stationName,
                        subway.subwayId
                    )
                }
            )
            preferenceManager.putLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS, fileUpdatedTimeMillis)
        }
    }

    companion object {
        private const val KEY_LAST_DATABASE_UPDATED_TIME_MILLIS = "KEY_LAST_DATABASE_UPDATED_TIME_MILLIS"
    }
}