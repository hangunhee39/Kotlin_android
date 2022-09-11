package hgh.project.movie_grade.data.api

import com.google.firebase.firestore.FirebaseFirestore
import hgh.project.movie_grade.domain.model.User
import kotlinx.coroutines.tasks.await

class UserFireStoreApi(
    private val firestore: FirebaseFirestore
) : UserApi {

    override suspend fun saveUser(user: User): User =
        firestore.collection("users")
            .add(user)
            .await()
            .let { User(it.id) }
}