package hgh.project.shopping.presentation.list

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import hgh.project.shopping.databinding.FragmentProductListBinding
import hgh.project.shopping.presentation.BaseFragment
import hgh.project.shopping.presentation.adapter.ProductListAdapter
import hgh.project.shopping.presentation.datail.ProductDetailActivity
import hgh.project.shopping.presentation.main.MainActivity
import org.koin.android.ext.android.inject

internal class ProductListFragment :
    BaseFragment<ProductListViewModel, FragmentProductListBinding>() {

    companion object {
        const val TAG = "ProductListFragment"
    }

    private val adapter = ProductListAdapter {
        //ProductDetailActivity 인텐트로 id값 주고 결과 가져오기
        startProductDetailForResult.launch(
            ProductDetailActivity.newIntent(requireContext(), it.id)
        )
    }

    private val startProductDetailForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == ProductDetailActivity.PRODUCT_ORDERED_RESULT_CODE) {
                    //MainActivity refresh 상태로 만들기
                (requireActivity() as MainActivity).viewModel.refreshOrderList()
            }
        }

    override val viewModel by inject<ProductListViewModel>()

    override fun getViewBinding(): FragmentProductListBinding =
        FragmentProductListBinding.inflate(layoutInflater)

    override fun observeData() = viewModel.productListStateLiveData.observe(this) {
        when (it) {
            is ProductListState.UnInitialized -> initViews(binding)
            is ProductListState.Loading -> handleLoadingState()
            is ProductListState.Error -> handleErrorState()
            is ProductListState.Success -> handleSuccessState(it)

        }
    }

    private fun initViews(binding: FragmentProductListBinding) = with(binding) {
        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener {
            viewModel.fetchData()
        }
    }

    private fun handleLoadingState() = with(binding) {
        refreshLayout.isRefreshing = true
    }

    private fun handleErrorState() = with(binding) {
        Toast.makeText(context, "에러가 발생했습니다", Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccessState(state: ProductListState.Success) = with(binding) {
        refreshLayout.isRefreshing = false

        if (state.productList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = false
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            adapter.submitList(state.productList)
        }
    }
}