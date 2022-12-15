package com.xuandq.facemaskdetection.ui.add_customer

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.Image
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    val images by lazy { MutableLiveData<ArrayList<Image>>(ArrayList()) }
    val showDelete by lazy { MutableLiveData<Boolean>() }

    fun addCustomer(name: String?, phoneNumber: String?, onSuccess: () -> Unit) {
        if (name.isNullOrEmpty()) {
            error.value = BaseError.Other("Vui lòng nhập tên")
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            error.value = BaseError.Other("Vui lòng nhập số điện thoại")
            return
        }
        val customer = Customer(
            id = 0,
            name = name,
            phoneNumber = phoneNumber,
            createdTime = System.currentTimeMillis()
        )
        viewModelScope.launch {
            val result = dataManager.addCustomer(
                customer,
                images.value
            )
            when (result) {
                is Result.Success -> {
                    onSuccess.invoke()
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
            Log.d("ppp", "addCustomer: ${dataManager.getImagesByCustomer(1)}")
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