package hgh.project.shopping.domain

import hgh.project.shopping.data.entity.product.ProductEntity
import hgh.project.shopping.data.repository.ProductRepository

internal class GetProductItemUseCase(
    private val productRepository: ProductRepository
):UseCase {

    suspend operator fun invoke(productId: Long): ProductEntity? {
        return productRepository.getProductItem(productId)
    }
}