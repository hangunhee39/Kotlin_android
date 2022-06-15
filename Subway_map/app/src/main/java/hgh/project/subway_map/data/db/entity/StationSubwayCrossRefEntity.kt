package hgh.project.subway_map.data.db.entity

import androidx.room.Entity

//station 과 subway 맵핑
@Entity(primaryKeys = ["stationName","subwayId"])
data class StationSubwayCrossRefEntity(
    val stationName: String,
    val subwayId: Int
)
