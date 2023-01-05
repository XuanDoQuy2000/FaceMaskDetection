package com.xuandq.facemaskdetection.ui.accumulate

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import com.xuandq.facemaskdetection.utils.PointConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccumulatePointViewModel @Inject constructor(private val dataManager: DataManager) :
    ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    val customer: MutableLiveData<CustomerUI> by lazy { MutableLiveData() }
    val money: MutableLiveData<Int?> by lazy { MutableLiveData() }
    val point = MediatorLiveData<Float?>().apply {
        addSource(money) {
            this.value = PointConverter.fromMoney(it)
        }
    }

    fun initData(customer: CustomerUI) {
        this.customer.value = customer
    }

    fun validatePoint(onValidSuccess: (() -> Unit)? = null): Boolean {
        val customerId = customer.value?.id
        val point = point.value
        return if (customerId != null && point != null && point != 0F) {
            onValidSuccess?.invoke()
            true
        } else {
            error.value = BaseError.Other("Chưa nhập hoặc nhập sai định dạng tiền")
            false
        }
    }

    fun accumulatePoint(onSuccess: (CustomerUI) -> Unit) {
        viewModelScope.launch {
            when (val result =
                dataManager.accumulatePoint(point.value!!, money.value!!, customer.value?.id!!)) {
                is Result.Success -> {
                    customer.value!!.apply {
                        currentPoint = (currentPoint ?: 0f).plus(point.value!!)
                        totalPoint = (totalPoint ?: 0f).plus(point.value!!)
                    }
                    Log.d("ppp", "accumulatePoint: ")
                    onSuccess.invoke(customer.value!!)
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }

    fun getMoneyValue() = money.value

    fun getPointValue() = point.value

    fun setMoney(value: Int?) {
        money.value = value
    }
}