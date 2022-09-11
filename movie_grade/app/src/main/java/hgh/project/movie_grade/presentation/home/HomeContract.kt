package hgh.project.movie_grade.presentation.home

import hgh.project.movie_grade.domain.model.FeaturedMovie
import hgh.project.movie_grade.domain.model.Movie
import hgh.project.movie_grade.presentation.BasePresenter
import hgh.project.movie_grade.presentation.BaseView

interface HomeContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showErrorDescription(message: String)

        fun showMovies(
            featuredMovie: FeaturedMovie?,
            movies: List<Movie>
        )
    }

    interface Presenter : BasePresenter
}