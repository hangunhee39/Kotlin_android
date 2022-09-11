package hgh.project.movie_grade.data.api

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import hgh.project.movie_grade.domain.model.Movie
import kotlinx.coroutines.tasks.await

class MovieFirestoreApi(
    private val firestore: FirebaseFirestore
) : MovieApi {

    override suspend fun getAllMovies(): List<Movie> =
        firestore.collection("movies")
            .get()
            .await()    //코루틴일때 가능 아니면 AddonComplete 사용
            .map { it.toObject<Movie>() } //문서를 객체로 치완

    override suspend fun getMovies(movieIds: List<String>): List<Movie> =
        firestore.collection("movies")
            .whereIn(FieldPath.documentId(), movieIds)  //documentId 로 접근 해야함
            .get()
            .await()
            .map { it.toObject<Movie>() }

}