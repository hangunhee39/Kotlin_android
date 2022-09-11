package hgh.project.movie_grade.data.api

import hgh.project.movie_grade.domain.model.User

interface UserApi {

    suspend fun saveUser(user: User): User
}