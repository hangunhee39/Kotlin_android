package hgh.project.shopping.domain

import hgh.project.shopping.data.entity.product.ProductEntity
import hgh.project.shopping.data.repository.ProductRepository

internal class GetOrderedProductListUseCase(
    private val productRepository: ProductRepository
):UseCase {

    suspend operator fun invoke(): List<ProductEntity> {
        return productRepository.getLocalProductList()
    }
}