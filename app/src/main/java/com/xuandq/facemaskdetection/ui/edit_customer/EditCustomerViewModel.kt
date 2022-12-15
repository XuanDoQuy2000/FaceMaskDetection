package com.xuandq.facemaskdetection.ui.edit_customer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.Image
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCustomerViewModel @Inject constructor(private val dataManager: DataManager): ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    val customer by lazy { MutableLiveData<CustomerUI>() }
    val images by lazy { MutableLiveData<ArrayList<Image>>(ArrayList()) }
    val showDelete by lazy { MutableLiveData<Boolean>() }

    fun initData(customerUI: CustomerUI) {
        customer.value = customerUI.copy()
        loadImagesForCustomer()
    }

    private fun loadImagesForCustomer() {
        if (customer.value == null) return
        viewModelScope.launch {
            when(val result = dataManager.getImagesByCustomer(customer.value!!.id)) {
                is Result.Success -> {
                    Log.d("ppp", "loadImagesForCustomer: ${result.data}")
                    images.value = result.data as ArrayList
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }

    fun updateCustomer(name: String?, phoneNumber: String?, onSuccess: () -> Unit) {
        if (name.isNullOrEmpty()) {
            error.value = BaseError.Other("Vui lòng nhập tên")
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            error.value = BaseError.Other("Vui lòng nhập số điện thoại")
            return
        }
        val customer = Customer(
            id = customer.value!!.id,
            name = name,
            phoneNumber = phoneNumber,
            createdTime = customer.value!!.createdTime
        )
        viewModelScope.launch {
            val result = dataManager.updateCustomer(
                customer,
                images.value ?: emptyList()
            )
            when (result) {
                is Result.Success -> {
                    onSuccess.invoke()
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }

    fun addImageToPreview(image: Image) {
        val list = images.value
        list?.add(image)
        images.value = list
        showDelete.value = false
    }

    fun removeSelectedImages() {
        images.value = images.value?.filter { !it.isSelected } as ArrayList
        showDelete.value = false
    }

    fun setShowDeleteValue(value: Boolean) {
        showDelete.value = value
    }

    fun getImagesByCustomer() {

    }

    fun getImagesValue() = images.value
}