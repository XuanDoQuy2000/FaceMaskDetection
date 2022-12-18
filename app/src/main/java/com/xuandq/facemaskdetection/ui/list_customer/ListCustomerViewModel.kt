package com.xuandq.facemaskdetection.ui.list_customer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCustomerViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError>() }

    val customers: MutableLiveData<List<CustomerUI>> by lazy { MutableLiveData() }
    var page: Int = 0
    var keyword = ""
    var searchJob: Job? = null

    fun refreshCustomers() {
        page = 0
        getCustomers()
    }

    private fun getCustomers() {
        searchJob = viewModelScope.launch {
            when(val result = dataManager.getCustomersByPoint(keyword, page)) {
                is Result.Success -> {
                    Log.d("ppp", "getCustomers: ${result.data}")
                    customers.value = result.data
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }

    fun searchCustomers(keyword: String) {
        this.keyword = keyword
        page = 0
        getCustomers()
    }
}