package com.xuandq.facemaskdetection.ui.customer_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(private val dataManager: DataManager) :
    ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    val customer by lazy { MutableLiveData<CustomerUI>() }

    fun initData(customerUI: CustomerUI) {
        customer.value = customerUI
    }

    fun getCustomerValue(): CustomerUI? = customer.value

    fun deleteCustomer(onSuccess: () -> Unit) {
        val customerUI = customer.value
        customerUI ?: return
        viewModelScope.launch {
            when (val result = dataManager.deleteCustomer(
                Customer(
                    id = customerUI.id,
                    name = customerUI.name,
                    phoneNumber = customerUI.phoneNumber,
                    createdTime = customerUI.createdTime
                )
            )) {
                is Result.Success -> {
                    onSuccess.invoke()
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }
}