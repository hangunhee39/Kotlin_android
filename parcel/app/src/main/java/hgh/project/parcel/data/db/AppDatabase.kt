package hgh.project.parcel.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hgh.project.parcel.data.entity.ShippingCompany
import hgh.project.parcel.data.entity.TrackingItem

@Database(
    entities = [TrackingItem::class, ShippingCompany::class],   //entity 추가해서 migration 오류 생길수있음
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackingItemDao(): TrackingItemDao
    abstract fun shippingCompanyDao(): ShippingCompanyDao

    companion object{

        private const val DATABASE_NAME = "tracking.db"

        fun build(context: Context):AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }
}