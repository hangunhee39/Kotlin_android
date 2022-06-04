package hgh.project.shopping.di

import hgh.project.shopping.data.db.provideDB
import hgh.project.shopping.data.db.provideToDoDao
import hgh.project.shopping.data.network.buildOkHttpClient
import hgh.project.shopping.data.network.provideGsonConverterFactory
import hgh.project.shopping.data.network.provideProductApiService
import hgh.project.shopping.data.network.provideProductRetrofit
import hgh.project.shopping.data.preference.PreferenceManager
import hgh.project.shopping.data.repository.DefaultProductRepository
import hgh.project.shopping.data.repository.ProductRepository
import hgh.project.shopping.domain.DeleteOrderedProductListUseCase
import hgh.project.shopping.domain.GetOrderedProductListUseCase
import hgh.project.shopping.domain.GetProductItemUseCase
import hgh.project.shopping.domain.GetProductListUseCase
import hgh.project.shopping.domain.OrderProductItemUseCase
import hgh.project.shopping.presentation.datail.ProductDetailViewModel
import hgh.project.shopping.presentation.list.ProductListViewModel
import hgh.project.shopping.presentation.main.MainViewModel
import hgh.project.shopping.presentation.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    //ViewModels
    viewModel { MainViewModel() }
    viewModel { ProductListViewModel(get()) }
    viewModel { ProfileViewModel(get(),get(), get()) }
    viewModel { (productId: Long) -> ProductDetailViewModel(productId, get(), get()) }

    //Coroutines Dispatcher
    single { Dispatchers.Main }
    single { Dispatchers.IO }

    //UseCase
    factory { GetProductItemUseCase(get()) }
    factory { GetProductListUseCase(get()) }
    factory { OrderProductItemUseCase(get()) }
    factory { GetOrderedProductListUseCase(get()) }
    factory { DeleteOrderedProductListUseCase(get()) }

    //Repository
    single<ProductRepository> { DefaultProductRepository(get(), get(), get()) }

    single { provideProductApiService(get()) }

    single { provideProductRetrofit(get(), get()) }

    single { provideGsonConverterFactory() }

    single { buildOkHttpClient() }

    single { PreferenceManager(androidContext()) }

    //DataBase
    single { provideDB(androidApplication()) }
    single { provideToDoDao(get()) }
}