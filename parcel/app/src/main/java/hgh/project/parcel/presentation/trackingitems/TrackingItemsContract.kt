package hgh.project.parcel.presentation.trackingitems

import hgh.project.parcel.data.entity.TrackingInformation
import hgh.project.parcel.data.entity.TrackingItem
import hgh.project.parcel.presentation.BasePresenter
import hgh.project.parcel.presentation.BaseView

class TrackingItemsContract {

    interface View: BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showNoDataDescription()

        fun showTrackingItemInformation(trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>>)

    }

    interface Presenter: BasePresenter {

        var trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>>

        fun refresh()
    }
}