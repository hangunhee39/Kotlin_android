package hgh.project.subway_map.data.repository

import hgh.project.subway_map.domain.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {

    val stations: Flow<List<Station>>

    suspend fun refreshStations()
}