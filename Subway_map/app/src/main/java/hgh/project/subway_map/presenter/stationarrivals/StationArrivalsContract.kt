package hgh.project.subway_map.presenter.stationarrivals

import hgh.project.subway_map.domain.ArrivalInformation
import hgh.project.subway_map.presenter.BasePresenter
import hgh.project.subway_map.presenter.BaseView

interface StationArrivalsContract {

    interface View : BaseView<Presenter>{

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showErrorDescription(message: String)

        fun showStationArrivals(arrivalInformation: List<ArrivalInformation>)
    }

    interface Presenter: BasePresenter {
        fun fetchStationArrivals()

        fun toggleStationFavorite()
    }
}