package hgh.project.subway_map.data.db.entity.mapper

import hgh.project.subway_map.data.repository.RealtimeArrival
import hgh.project.subway_map.domain.ArrivalInformation
import hgh.project.subway_map.domain.Subway
import java.text.SimpleDateFormat
import java.util.*

/*
    stationArrivalsApi.를 다루는 Mapping
 */
private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.'0'", Locale.KOREA)
private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)

private const val INVALID_FIELD = "-"

fun RealtimeArrival.toArrivalInformation(): ArrivalInformation =
    ArrivalInformation(
        subway = Subway.findById(subwayId),
        direction = trainLineNm?.split("-")
            ?.get(1)
            ?.trim()
            ?: INVALID_FIELD,
        destination = bstatnNm ?: INVALID_FIELD,
        message = arvlMsg2
            ?.replace(statnNm.toString(), "당역")
            ?.replace("[\\[\\]]".toRegex(), "")
            ?: INVALID_FIELD,
        updatedAt = recptnDt
            ?.let { apiDateFormat.parse(it) }
            ?.let { dateFormat.format(it) }
            ?: INVALID_FIELD
    )

fun List<RealtimeArrival>.toArrivalInformation(): List<ArrivalInformation> =
    map { it.toArrivalInformation() }