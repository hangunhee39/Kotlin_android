package hgh.project.shopping.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hgh.project.shopping.data.db.dao.ProductDao
import hgh.project.shopping.data.entity.product.ProductEntity
import hgh.project.shopping.utillity.DateConverters

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class ProductDatabase: RoomDatabase() {

    companion object{
        const val DB_NAME = "ProductDataBase.db"
    }

    abstract fun productDao(): ProductDao
}