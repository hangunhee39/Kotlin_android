package hgh.project.movie_grade.data.repository

import hgh.project.movie_grade.domain.model.Movie

interface MovieRepository {

    suspend fun getAllMovies(): List<Movie>

    suspend fun getMovies(movieIds: List<String>): List<Movie>

}