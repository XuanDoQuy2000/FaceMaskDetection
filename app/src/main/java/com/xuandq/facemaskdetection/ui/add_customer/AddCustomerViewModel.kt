package com.xuandq.facemaskdetection.ui.add_customer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError>() }

    fun addCustomer(name: String?, phoneNumber: String?, onSuccess: () -> Unit) {
        if (name.isNullOrEmpty()) {
            error.value = BaseError.Other("Vui lòng nhập tên")
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            error.value = BaseError.Other("Vui lòng nhập số điện thoại")
            return
        }
        viewModelScope.launch {
            val result = dataManager.addCustomer(
                Customer(
                    id = 0,
                    name = name,
                    phoneNumber = phoneNumber,
                    createdTime = System.currentTimeMillis()
                )
            )
            if (result is Result.Success) {
                onSuccess.invoke()
            }
        }
    }
}