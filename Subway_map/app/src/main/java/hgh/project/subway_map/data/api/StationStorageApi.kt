package hgh.project.subway_map.data.api

import com.google.firebase.storage.FirebaseStorage
import hgh.project.subway_map.data.db.entity.StationEntity
import hgh.project.subway_map.data.db.entity.SubwayEntity
import kotlinx.coroutines.tasks.await

class StationStorageApi(
    firebaseStorage: FirebaseStorage
) : StationApi {

    companion object {
        private const val STATION_DATA_FILE_NAME = "station_data.csv"
    }

    private val sheetReference = firebaseStorage.reference.child(STATION_DATA_FILE_NAME)

    //업데이트된 시간 가져오기
    override suspend fun getStationDataUpdatedTimeMillis(): Long =
        sheetReference.metadata.await().updatedTimeMillis

    //firebase 에서 파일 가져오기
    override suspend fun getStationSubways(): List<Pair<StationEntity, SubwayEntity>> {
        val downloadSizeBytes = sheetReference.metadata.await().sizeBytes
        val byteArray = sheetReference.getBytes(downloadSizeBytes).await()

        return byteArray.decodeToString()
            .lines()
            .drop(1)
            .map { it.split(",") }
            .map {StationEntity(it[1]) to SubwayEntity(it[0].toInt()) }
    }

}