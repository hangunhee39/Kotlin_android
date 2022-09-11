package hgh.project.movie_grade.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Review(
    @DocumentId
    val id: String? = null,

    @ServerTimestamp   //자동으로 타임스탬프 찍고 Date 타입으로 내려받음
    val createdAt: Date? = null,

    val userId: String? = null,
    val movieId: String? = null,
    val content: String? = null,
    val score: Float? = null
)