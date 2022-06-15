package hgh.project.subway_map.data.db

import androidx.room.*
import hgh.project.subway_map.data.db.entity.StationEntity
import hgh.project.subway_map.data.db.entity.StationSubwayCrossRefEntity
import hgh.project.subway_map.data.db.entity.StationWithSubwaysEntity
import hgh.project.subway_map.data.db.entity.SubwayEntity
import kotlinx.coroutines.flow.Flow
import java.lang.ref.PhantomReference

@Dao
interface StationDao {

    @Transaction
    @Query("SELECT * FROM StationEntity")
    fun getStationWithSubways(): Flow<List<StationWithSubwaysEntity>>
    //Flow : observable 하게 데이터가 변경할때마다 반영

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertStations(station: List<StationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertSubways(subways: List<SubwayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertCrossReferences(reference: List<StationSubwayCrossRefEntity>)
}