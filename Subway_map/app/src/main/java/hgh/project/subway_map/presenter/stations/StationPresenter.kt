package hgh.project.subway_map.presenter.stations

import hgh.project.subway_map.data.repository.StationRepository
import hgh.project.subway_map.domain.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StationPresenter(
    private val view: StationsContract.View,
    private val stationRepository: StationRepository
): StationsContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    //역을 찾을때 한글자씩 검색 결과 보이게 하기 위해서
    private val queryString: MutableStateFlow<String> = MutableStateFlow("")
    private val stations: MutableStateFlow<List<Station>> = MutableStateFlow(emptyList())

    init {
        observeStations()
    }

    override fun filterStations(query: String) {
        scope.launch {
            queryString.emit(query)
        }
    }

    override fun onViewCreated() {
        scope.launch {
            view.showStations(stations.value)
            stationRepository.refreshStations()
        }
    }

    override fun onDestroyView() {}


    private fun observeStations(){
        stationRepository
            .stations
            .combine(queryString){ stations, query ->   //검색할때
                if (query.isBlank()){
                    stations
                }else{
                    stations.filter {
                        it.name.contains(query)
                    }
                }
            }
            .onStart { view.showLoadingIndicator() }
            .onEach {
                if (it.isNotEmpty()){               //새로운 값이 들아올때마다 업데이트
                    view.hideLoadingIndicator()
                }
                stations.value =it
                view.showStations(it)
            }
            .catch {
                it.printStackTrace()
                view.hideLoadingIndicator()
            }
            .launchIn(scope)
    }

    override fun toggleStationFavorite(station: Station) {
        scope.launch {
            stationRepository.updateStation(station.copy(isFavorited = !station.isFavorited))
        }
    }
}