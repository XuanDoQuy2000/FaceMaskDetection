package com.xuandq.facemaskdetection.ui.redeem

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.RewardUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import com.xuandq.facemaskdetection.utils.ext.isFalse
import com.xuandq.facemaskdetection.utils.ext.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuantityRewardViewModel @Inject constructor(private val dataManager: DataManager) :
    ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError>() }

    val customer: MutableLiveData<CustomerUI> by lazy { MutableLiveData() }
    val reward: MutableLiveData<RewardUI> by lazy { MutableLiveData() }

    val quantity: MutableLiveData<Int> by lazy { MutableLiveData() }

    val canRedeem = MediatorLiveData<Boolean>().apply {
        addSource(quantity) {
            value = (it ?: 0) > 0
        }
    }

    val totalPoint = MediatorLiveData<Float>().apply {
        addSource(quantity) {
            value = (it ?: 0) * (reward.value?.point?.toFloat() ?: 0F)
        }
    }

    fun initData(
        customerUI: CustomerUI,
        rewardUI: RewardUI,
    ) {
        customer.value = customerUI
        reward.value = rewardUI
        quantity.value = if (getMaxQuantity() == 0) 0 else 1
    }

    fun validateRedeem(onValid: () -> Unit) {
        if (!canRedeem.value.isTrue() || customer.value == null || reward.value == null) {
            error.value = BaseError.Other("Không thể đổi quà")
            return
        }
        onValid.invoke()
    }

    fun redeem(onSuccess: (CustomerUI) -> Unit) {
        viewModelScope.launch {
            when (val result = dataManager.redeemPoint(
                customer.value!!.id,
                reward.value!!.id,
                reward.value!!.point!!,
                quantity.value!!
            )) {
                is Result.Success -> {
                    customer.value = customer.value?.apply {
                        currentPoint = currentPoint!! - this@QuantityRewardViewModel.totalPoint.value!!
                    }
                    onSuccess.invoke(customer.value!!)
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }

    fun getMaxQuantity() =
        (customer.value?.currentPoint?.toInt() ?: 0) / (reward.value?.point ?: -1)
}