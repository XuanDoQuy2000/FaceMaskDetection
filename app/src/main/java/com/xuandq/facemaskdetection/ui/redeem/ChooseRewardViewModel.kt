package com.xuandq.facemaskdetection.ui.redeem

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.RewardUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseRewardViewModel @Inject constructor (private val dataManager: DataManager): ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError>() }

    val rewards by lazy { MutableLiveData<List<RewardUI>>(emptyList()) }
    val customer by lazy { MutableLiveData<CustomerUI>() }

    fun getRewards() {
        viewModelScope.launch {
            when (val result = dataManager.getAllReward()) {
                is Result.Success -> {
                    Log.d("data", "getCustomers: ${result.data}")
                    rewards.value = result.data
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }
}