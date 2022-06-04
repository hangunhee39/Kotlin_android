package hgh.project.shopping.utillity

import androidx.room.TypeConverter
import java.util.*

object DateConverters {

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong ==null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}