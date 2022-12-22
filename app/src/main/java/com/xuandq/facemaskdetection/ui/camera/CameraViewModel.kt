package com.xuandq.facemaskdetection.ui.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val dataManager: DataManager): ViewModel() {
    val error by lazy { MutableLiveData<BaseError?>() }

    val recentCustomers by lazy { MutableLiveData<List<CustomerUI>>() }

    fun addCustomerToRecent(customerId: Int) {
        viewModelScope.launch {
            when(val result = dataManager.getCustomerById(customerId)) {
                is Result.Success -> {
                    val mutableList = recentCustomers.value?.toMutableList() ?: mutableListOf()
                    val customer = result.data
                    mutableList.remove(customer)
                    mutableList.add(0, customer)
                    recentCustomers.value = mutableList
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }
}