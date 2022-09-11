package hgh.project.movie_grade.domain.usecase

import hgh.project.movie_grade.data.repository.ReviewRepository
import hgh.project.movie_grade.domain.model.Review

class DeleteReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(review: Review) =
        reviewRepository.removeReview(review)
}