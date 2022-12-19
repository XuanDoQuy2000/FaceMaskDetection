package com.xuandq.facemaskdetection.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuandq.facemaskdetection.analyzer.FaceNetModel
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.repository.DataManager
import com.xuandq.facemaskdetection.data.repository.FaceEmbeddingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dataManager: DataManager): ViewModel() {
    var isReady = false

    init {
        viewModelScope.launch {
            when(val result = dataManager.getAllImageCustomerEmbedding()) {
                is Result.Success -> {
                    FaceEmbeddingRepository.facesEmbedding.clear()
                    FaceEmbeddingRepository.facesEmbedding.addAll(result.data)
                    Log.d("ppp", "embeding size: ${result.data.size}")
                    isReady = true
                }
                is Result.Error -> {
                    Log.d("ppp", "error: ${result.error}")
                }
            }
        }
    }
}