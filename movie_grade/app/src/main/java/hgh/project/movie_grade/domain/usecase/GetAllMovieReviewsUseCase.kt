package hgh.project.movie_grade.domain.usecase

import hgh.project.movie_grade.data.repository.ReviewRepository
import hgh.project.movie_grade.data.repository.UserRepository
import hgh.project.movie_grade.domain.model.MovieReviews
import hgh.project.movie_grade.domain.model.Review
import hgh.project.movie_grade.domain.model.User

class GetAllMovieReviewsUseCase(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(movieId: String): MovieReviews {
        val reviews = reviewRepository.getAllMovieReviews(movieId)
        val user = userRepository.getUser()

        if (user == null) {
            userRepository.saveUser(User())

            return MovieReviews(null, reviews)
        }

        return MovieReviews(
            reviews.find { it.userId == user.id },
            reviews.filter { it.userId != user.id }
        )
    }

}