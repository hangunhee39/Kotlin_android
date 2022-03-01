package org.techtown.calcul

import androidx.room.Database
import androidx.room.RoomDatabase
import org.techtown.calcul.dao.HistoryDao
import org.techtown.calcul.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}