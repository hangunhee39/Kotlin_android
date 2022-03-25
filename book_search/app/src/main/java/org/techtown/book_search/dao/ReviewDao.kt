package org.techtown.book_search.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.techtown.book_search.model.Review

@Dao
interface ReviewDao {
    @Query("SELECT * FROM review WHERE id == :id")
    fun getOneReview(id: Int): Review

    //이미 리뷰가있을때 덮어쓰기기
   @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: Review)
}