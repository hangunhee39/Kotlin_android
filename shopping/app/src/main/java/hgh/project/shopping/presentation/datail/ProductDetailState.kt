package hgh.project.shopping.presentation.datail

import hgh.project.shopping.data.entity.product.ProductEntity

sealed class ProductDetailState {

    object UnInitialized : ProductDetailState()

    object Loading : ProductDetailState()

    object Error : ProductDetailState()

    object Order : ProductDetailState()

    data class Success(
        val productEntity: ProductEntity
    ) : ProductDetailState()
}