package hgh.project.movie_grade.data.api

import hgh.project.movie_grade.domain.model.Movie


interface MovieApi {

    suspend fun getAllMovies(): List<Movie>

    suspend fun getMovies(movieIds: List<String>): List<Movie>

}