package hgh.project.movie_grade.presentation

interface BaseView<PresenterT: BasePresenter> {

    val presenter: PresenterT
}