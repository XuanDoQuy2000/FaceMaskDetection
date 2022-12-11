package com.xuandq.facemaskdetection.ui.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.PointTransaction
import com.xuandq.facemaskdetection.data.model.PointTransactionUI
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {
    val loading by lazy { MutableLiveData<Boolean>() }
    val error by lazy { MutableLiveData<BaseError>() }

    val customer: MutableLiveData<CustomerUI> by lazy { MutableLiveData() }
    val transactions: MutableLiveData<List<PointTransactionUI>> by lazy { MutableLiveData() }

    var page = 0

    fun initData(customerUI: CustomerUI) {
        customer.value = customerUI
        page = 0
        getTransactionsByCustomer()
    }

    private fun getTransactionsByCustomer() {
        val customerId = customer.value?.id
        customerId ?: return
        viewModelScope.launch {
            when (val result = dataManager.getTransactionsByCustomer(customerId,page)) {
                is Result.Success -> {
                    Log.d("ppp", "getTransactionsByCustomer: $result")
                    transactions.value = result.data
                }
                is Result.Error -> {
                    error.value = result.error
                }
            }
        }
    }
}