package hgh.project.shopping.domain

import hgh.project.shopping.data.entity.product.ProductEntity
import hgh.project.shopping.data.repository.ProductRepository

internal class OrderProductItemUseCase(
    private val productRepository: ProductRepository
):UseCase {

    suspend operator fun invoke(productEntity: ProductEntity): Long {
        return productRepository.insertProductItem(productEntity)
    }
}