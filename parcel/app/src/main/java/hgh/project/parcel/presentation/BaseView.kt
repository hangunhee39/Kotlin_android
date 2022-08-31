package hgh.project.parcel.presentation

interface BaseView<PresenterT : BasePresenter> {

    val presenter: PresenterT
}