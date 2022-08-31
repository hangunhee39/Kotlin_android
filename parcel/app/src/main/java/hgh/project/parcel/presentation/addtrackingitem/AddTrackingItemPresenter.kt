package hgh.project.parcel.presentation.addtrackingitem

import hgh.project.parcel.data.entity.ShippingCompany
import hgh.project.parcel.data.entity.TrackingItem
import hgh.project.parcel.data.repository.ShippingCompanyRepository
import hgh.project.parcel.data.repository.TrackingItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddTrackingItemPresenter(
    private val view: AddTrackingItemsContract.View,
    private val shippingCompanyRepository: ShippingCompanyRepository,
    private val trackerRepository: TrackingItemRepository
): AddTrackingItemsContract.Presenter {

    override var invoice: String? = null
    override var shippingCompanies: List<ShippingCompany>? =null
    override var selectedShippingCompany: ShippingCompany? =null


    override val scope: CoroutineScope = MainScope()

    override fun fetchSippingCompanies() {
        scope.launch {
            view.showShippingCompaniesLoadingIndicator()
            if (shippingCompanies.isNullOrEmpty()){     //다른화면에 갔다와도 새로 fetch 안하기
                shippingCompanies = shippingCompanyRepository.getSippingCompanies()
            }

            shippingCompanies?.let { view.showCompanies(it) }
            view.hideShippingCompaniesLoadingIndicator()
        }
    }

    override fun fetchRecommendShippingCompany() {
        scope.launch {
            view.showRecommendCompanyLoadingIndicator()
            shippingCompanyRepository.getRecommendShippingCompany(invoice!!)?.let { view.showRecommendCompany(it) }
            view.hideRecommendCompanyLoadingIndicator()
        }
    }

    override fun changeSelectedShippingCompany(companyName: String) {
        selectedShippingCompany =shippingCompanies?.find { it.name==companyName }
        enableSaveButtonIfAvailable()
    }

    override fun changeShippingInvoice(invoice: String) {
        this.invoice =invoice
        enableSaveButtonIfAvailable()
    }

    override fun saveTrackingItem() {
        scope.launch {
            try {
                view.showSaveTrackingItemIndicator()
                trackerRepository.saveTrackingItem(
                    TrackingItem(
                        invoice!!,
                        selectedShippingCompany!!
                    )
                )
                view.finish()
            } catch (exception: Exception) {
                view.showErrorToast(exception.message ?: "서비스에 문제가 생겨서 운송장을 추가하지 못했어요 😢")
            } finally {
                view.hideSaveTrackingItemIndicator()
            }
        }
    }

    private fun enableSaveButtonIfAvailable() {
        if (!invoice.isNullOrBlank() && selectedShippingCompany != null) {
            view.enableSaveButton()
        } else {
            view.disableSaveButton()
        }
    }

    override fun onViewCreated() {
        fetchSippingCompanies()
    }

    override fun onDestroyView() { }
}