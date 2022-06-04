package hgh.project.shopping.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hgh.project.shopping.presentation.profile.ProfileState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal abstract class BaseViewModel: ViewModel() {

    //상태에 따리 데이터 fetch
    abstract fun fetchData(): Job

}