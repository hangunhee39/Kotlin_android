package hgh.project.github_repository.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hgh.project.github_repository.data.dao.SearchHistoryDao
import hgh.project.github_repository.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

}