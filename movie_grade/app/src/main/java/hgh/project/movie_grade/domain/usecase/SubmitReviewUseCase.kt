package hgh.project.movie_grade.domain.usecase

import hgh.project.movie_grade.data.repository.ReviewRepository
import hgh.project.movie_grade.data.repository.UserRepository
import hgh.project.movie_grade.domain.model.Movie
import hgh.project.movie_grade.domain.model.Review
import hgh.project.movie_grade.domain.model.User

class SubmitReviewUseCase(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) {

    suspend operator fun invoke(
        movie: Movie,
        content: String,
        score: Float
    ): Review {
        var user = userRepository.getUser()

        if (user == null) {
            userRepository.saveUser(User())
            user = userRepository.getUser()
        }

        return reviewRepository.addReview(
            Review(
                userId = user!!.id,
                movieId = movie.id,
                content = content,
                score = score
            )
        )
    }
}