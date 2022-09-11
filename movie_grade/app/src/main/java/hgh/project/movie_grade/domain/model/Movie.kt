package hgh.project.movie_grade.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

//firebase 는 디폴트값이 있어야함(null 넣은 이유)
@Parcelize
data class Movie(
    @DocumentId //firebase id 받기위해
    val id: String? = null,

    @field:JvmField //boolean 일때 붙여줌
    val isFeatured: Boolean? = null,

    val title: String? = null,
    val actors: String? = null,
    val country: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val posterUrl: String? = null,
    val rating: String? = null,
    val averageScore: Float? = null,
    val numberOfScore: Int? = null,
    val releaseYear: Int? = null,
    val runtime: Int? = null
): Parcelable