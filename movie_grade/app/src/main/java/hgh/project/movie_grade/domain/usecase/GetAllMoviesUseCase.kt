package hgh.project.movie_grade.domain.usecase

import hgh.project.movie_grade.data.repository.MovieRepository
import hgh.project.movie_grade.domain.model.Movie

class GetAllMoviesUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke(): List<Movie> = movieRepository.getAllMovies()
}