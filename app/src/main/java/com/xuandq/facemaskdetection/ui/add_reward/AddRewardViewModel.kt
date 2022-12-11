package com.xuandq.facemaskdetection.ui.add_reward

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Reward
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import javax.inject.Inject

@HiltViewModel
class AddRewardViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    fun addReward(name: String?, des: String?, quantity: String?, point: String? , onSuccess: () -> Unit) {
        if (name.isNullOrEmpty() || des.isNullOrEmpty() || quantity.isNullOrEmpty() || point.isNullOrEmpty()) {
            error.value = BaseError.Other("Không được để trống thông tin.")
            return
        }
        try {
            val quantityValue = quantity.toInt()
            val pointValue = point.toInt()
            viewModelScope.launch {
                val result = dataManager.addReward(
                    Reward(
                        id = 0,
                        name = name,
                        description = des,
                        totalQuantity = quantityValue,
                        point = pointValue,
                        createdTime = System.currentTimeMillis()
                    )
                )
                if (result is Result.Success) {
                    onSuccess.invoke()
                }
            }
        } catch (e : NumberFormatException) {
            error.value = BaseError.Other("Nhập lỗi sai định dạng.")
        }
    }

}