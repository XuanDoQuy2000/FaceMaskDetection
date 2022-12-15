package com.xuandq.facemaskdetection.ui.reward_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.Reward
import com.xuandq.facemaskdetection.data.model.RewardUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardDetailViewModel @Inject constructor(private val dataManager: DataManager) :
    ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError?>() }

    val reward by lazy { MutableLiveData<RewardUI>() }

    fun initViewModel(rewardUI: RewardUI){
        reward.value = rewardUI
    }

    fun getRewardTotalQuantity() = reward.value?.totalQuantity ?: 0

    fun updateRewardQuantity(quantity: Int, onSuccess: () -> Unit) {
        val rewardValue = reward.value
        rewardValue ?: return
        val newQuantity = quantity.coerceAtLeast(rewardValue.usedQuantity ?: 0)
        val updatedReward = Reward(
            id = rewardValue.id,
            name = rewardValue.name,
            description = rewardValue.description,
            point = rewardValue.point,
            totalQuantity = newQuantity,
            createdTime = rewardValue.createdTime
        )
        viewModelScope.launch {
            when(val result = dataManager.updateReward(updatedReward)){
                is Result.Success -> {
                    reward.value = reward.value?.copy(totalQuantity = newQuantity)
                    onSuccess.invoke()
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }
}