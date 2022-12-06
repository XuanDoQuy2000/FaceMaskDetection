package com.xuandq.facemaskdetection.ui.list_reward

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListRewardViewModel @Inject constructor(private val dataManager: DataManager): ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError>() }
}