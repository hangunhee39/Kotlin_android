package hgh.project.shopping.presentation.list

import hgh.project.shopping.data.entity.product.ProductEntity

sealed class ProductListState {

    object UnInitialized: ProductListState()

    object Loading: ProductListState()

    object  Error: ProductListState()

    data class Success(
        val productList: List<ProductEntity>
    ): ProductListState()
}