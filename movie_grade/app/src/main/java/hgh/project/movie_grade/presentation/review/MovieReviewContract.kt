package hgh.project.movie_grade.presentation.review

import hgh.project.movie_grade.domain.model.Movie
import hgh.project.movie_grade.domain.model.MovieReviews
import hgh.project.movie_grade.domain.model.Review
import hgh.project.movie_grade.presentation.BasePresenter
import hgh.project.movie_grade.presentation.BaseView

interface MovieReviewContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showErrorDescription(message: String)

        fun showMovieInformation(movie: Movie)

        fun showReviews(reviews: MovieReviews)

        fun showErrorToast(message: String)
    }

    interface Presenter : BasePresenter {

        val movie: Movie

        fun requestAddReview(content: String, score: Float)

        fun requestRemoveReview(review: Review)
    }
}