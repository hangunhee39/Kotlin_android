package hgh.project.subway_map.presenter.stations

import hgh.project.subway_map.domain.Station
import hgh.project.subway_map.presenter.BasePresenter
import hgh.project.subway_map.presenter.BaseView

interface StationsContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showStations(Stations: List<Station>)

    }

    interface Presenter : BasePresenter {
        fun filterStations(query: String)

        fun toggleStationFavorite(station: Station)
    }
}