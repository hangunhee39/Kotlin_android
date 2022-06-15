package hgh.project.subway_map.data.db.entity.mapper

import hgh.project.subway_map.data.db.entity.StationWithSubwaysEntity
import hgh.project.subway_map.data.db.entity.SubwayEntity
import hgh.project.subway_map.domain.Station
import hgh.project.subway_map.domain.Subway

fun StationWithSubwaysEntity.toStation() = Station(
    name = station.stationName,
    isFavorited = station.isFavorite,
    connectedSubways = subways.toSubways()
)

fun List<StationWithSubwaysEntity>.toStations() = map { it.toStation() }

fun List<SubwayEntity>.toSubways(): List<Subway> = map { Subway.findById(it.subwayId) }
