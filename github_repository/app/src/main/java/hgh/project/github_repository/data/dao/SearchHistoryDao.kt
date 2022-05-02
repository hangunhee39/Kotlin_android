package hgh.project.github_repository.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hgh.project.github_repository.data.entity.GithubRepoEntity

@Dao
interface SearchHistoryDao {

    @Insert
    suspend fun insert(repo: GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepoEntity>)

    @Query("SELECT * FROM GithubRepository")
    suspend fun getHistory(): List<GithubRepoEntity>

    @Query("SELECT * FROM GithubRepository WHERE fullName = :fullName")
    suspend fun getRepository(fullName: String): GithubRepoEntity?

    @Query("DELETE FROM GithubRepository WHERE fullName = :fullName")
    suspend fun remove(fullName: String)

    @Query("DELETE FROM GithubRepository")
    suspend fun clearAll()

}