package hgh.project.movie_grade.data.repository

import hgh.project.movie_grade.domain.model.User


interface UserRepository {

    suspend fun getUser(): User?

    suspend fun saveUser(user: User)
}