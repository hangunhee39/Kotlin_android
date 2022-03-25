package org.techtown.book_search

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.techtown.book_search.dao.HistoryDao
import org.techtown.book_search.dao.ReviewDao
import org.techtown.book_search.model.History
import org.techtown.book_search.model.Review

@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

fun getAppDatabase(context: Context):AppDatabase {

    //데이터베이스 추가할때
    val migration_1_2 =object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'REVIEW' ('id' INTERGER, 'review' TEXT," + "PRIMARY KEY('id'))")
        }
    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchEB"
    )
        .addMigrations(migration_1_2)
        .build()
}