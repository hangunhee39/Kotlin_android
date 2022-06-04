package hgh.project.shopping.data.repository

import hgh.project.shopping.data.db.dao.ProductDao
import hgh.project.shopping.data.entity.product.ProductEntity
import hgh.project.shopping.data.network.ProductApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultProductRepository(
    private val productApi: ProductApiService,
    private val IoDispatcher: CoroutineDispatcher,
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getProductList(): List<ProductEntity> = withContext(IoDispatcher){
        val response = productApi.getProducts()
        return@withContext if (response.isSuccessful){
            response.body()?.item?.map { it.toEntity() } ?: listOf()
        }else{
            listOf()
        }
    }

    override suspend fun getLocalProductList(): List<ProductEntity> = withContext(IoDispatcher) {
        productDao.getAll()
    }

    override suspend fun insertProductItem(productItem: ProductEntity): Long = withContext(IoDispatcher){
        productDao.insert(productItem)
    }

    override suspend fun insertProductList(ProductList: List<ProductEntity>) = withContext(IoDispatcher){
        TODO("Not yet implemented")
    }

    override suspend fun updateProductItem(ProductItem: ProductEntity) = withContext(IoDispatcher){
        TODO("Not yet implemented")
    }

    override suspend fun getProductItem(itemId: Long): ProductEntity? = withContext(IoDispatcher){
        val response = productApi.getProduct(itemId)
        return@withContext if (response.isSuccessful){
            response.body()?.toEntity()
        }else{
            null
        }
    }

    override suspend fun deleteAll() = withContext(IoDispatcher){
        productDao.deleteAll()
    }

    override suspend fun deleteProductItem(id: Long) = withContext(IoDispatcher){
        TODO("Not yet implemented")
    }
}