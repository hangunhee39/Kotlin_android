package hgh.project.parcel.presentation.trackingitems

import hgh.project.parcel.data.entity.TrackingInformation
import hgh.project.parcel.data.entity.TrackingItem
import hgh.project.parcel.data.repository.TrackingItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception

class TrackingItemsPresenter(
    private val view: TrackingItemsContract.View,
    private val trackingItemRepository: TrackingItemRepository
) : TrackingItemsContract.Presenter{


    override var trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>> = emptyList()

    override fun refresh() {
        fetchTrackingInformation(true)
    }

    override val scope: CoroutineScope = MainScope()

    init {  //DB 변경이 있을때마다 refresh

        scope.launch {
            trackingItemRepository
                .trackingItems
                .collect {refresh()} //observe 같은 느낌
        }
    }

    override fun onViewCreated() {
        fetchTrackingInformation()
    }

    override fun onDestroyView() {}

    private fun fetchTrackingInformation(forceFetch: Boolean =false) =scope.launch {
        try {

            view.showLoadingIndicator()
            if (trackingItemInformation.isEmpty() || forceFetch) {      //너무 자주 api 쓰지 않게 하기
                trackingItemInformation = trackingItemRepository.getTrackingItemInformation()
            }
            if (trackingItemInformation.isEmpty()) {
                view.showNoDataDescription()
            } else {
                view.showTrackingItemInformation(trackingItemInformation)
            }
        }catch (exception: Exception){
            exception.printStackTrace()
        }finally {
            view.hideLoadingIndicator()
        }
    }
}