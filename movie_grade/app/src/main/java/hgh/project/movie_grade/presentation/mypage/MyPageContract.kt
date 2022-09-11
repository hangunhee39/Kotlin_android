package hgh.project.movie_grade.presentation.mypage

import hgh.project.movie_grade.domain.model.ReviewedMovie
import hgh.project.movie_grade.presentation.BasePresenter
import hgh.project.movie_grade.presentation.BaseView

interface MyPageContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showNoDataDescription(message: String)

        fun showErrorDescription(message: String)

        fun showReviewedMovies(reviewedMovies: List<ReviewedMovie>)
    }

    interface Presenter : BasePresenter
}