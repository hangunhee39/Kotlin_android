package hgh.project.subway_map.presenter

interface BaseView<PresenterT : BasePresenter>  {

    val presenter : PresenterT
}