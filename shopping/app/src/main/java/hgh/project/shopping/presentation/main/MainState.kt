package hgh.project.shopping.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hgh.project.shopping.presentation.list.ProductListState

sealed class MainState {

    object RefreshOrderList: MainState()
}