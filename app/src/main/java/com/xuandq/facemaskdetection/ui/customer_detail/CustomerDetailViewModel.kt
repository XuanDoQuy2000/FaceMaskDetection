package com.xuandq.facemaskdetection.ui.customer_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(private val dataManager: DataManager): ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    val customer by lazy { MutableLiveData<CustomerUI>() }

    fun initData(customerUI: CustomerUI) {
        customer.value = customerUI
    }

    fun getCustomerValue(): CustomerUI? = customer.value
}