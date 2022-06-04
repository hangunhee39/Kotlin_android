package hgh.project.shopping.presentation.datail

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isVisible
import hgh.project.shopping.databinding.ActivityProductDetailBinding
import hgh.project.shopping.extensions.load
import hgh.project.shopping.extensions.loadCenterCrop
import hgh.project.shopping.presentation.BaseActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

internal class ProductDetailActivity :
    BaseActivity<ProductDetailViewModel, ActivityProductDetailBinding>() {

    companion object {
        const val PRODUCT_ID_KEY = "PRODUCT_ID_KEY"

        const val PRODUCT_ORDERED_RESULT_CODE = 99

        fun newIntent(context: Context, productId:Long ) =
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(PRODUCT_ID_KEY, productId)
            }
    }

    //appModule 에 주입시 파라미터가 필요한 경우
    override val viewModel by inject<ProductDetailViewModel>{
        parametersOf(
            intent.getLongExtra(PRODUCT_ID_KEY, 1)
        )
    }

    override fun getViewBinding(): ActivityProductDetailBinding =
        ActivityProductDetailBinding.inflate(layoutInflater)

    override fun observeData() =viewModel.productDetailStateLiveData.observe(this) {
        when (it){
            is ProductDetailState.UnInitialized -> initViews()
            is ProductDetailState.Loading -> handleLoading()
            is ProductDetailState.Order -> handleOrder()
            is ProductDetailState.Error -> handleError()
            is ProductDetailState.Success -> handleSuccess(it)
        }
    }

    private fun initViews() = with(binding) {
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)  //앱바 뒤로가기 설정
        actionBar?.setDisplayShowHomeEnabled(true)
        title=""
        toolbar.setNavigationOnClickListener {
            finish()
        }
        orderButton.setOnClickListener {
            viewModel.orderProduct()
        }
    }

    private fun handleLoading() = with(binding){
        progressBar.isVisible = true
    }

    private fun handleOrder() {
        //intent 결과 보내기
        setResult(PRODUCT_ORDERED_RESULT_CODE)
        Toast.makeText(this, "제품을 주문했습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleError() {
        Toast.makeText(this,"제품 정보를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleSuccess(state: ProductDetailState.Success) = with(binding){
        progressBar.isVisible =false
        val product = state.productEntity
        title = product.productName
        productCategoryTextView.text =product.productType
        productPriceTextView.text = "${product.productPrice}원"
        introductionImageView.load(product.productImage)
        productImageView.loadCenterCrop(product.productImage, 8f)

    }
}