package hgh.project.subway_map.data.api

import hgh.project.subway_map.data.db.entity.StationEntity
import hgh.project.subway_map.data.db.entity.SubwayEntity

interface StationApi {

    suspend fun getStationDataUpdatedTimeMillis(): Long

    suspend fun getStationSubways(): List<Pair<StationEntity, SubwayEntity>>
}