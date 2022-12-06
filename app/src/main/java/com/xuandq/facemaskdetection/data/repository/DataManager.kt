package com.xuandq.facemaskdetection.data.repository

import com.xuandq.facemaskdetection.data.local.LocalDataSource
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.remote.RemoteDataSource
import com.xuandq.facemaskdetection.utils.DEFAULT_PAGE_SIZE
import javax.inject.Inject
import javax.inject.Singleton

class DataManager @Inject constructor(
    private val local: LocalDataSource,
    private val remote: RemoteDataSource,
) {
    suspend fun getCustomersByPoint(page: Int, pageSize: Int = DEFAULT_PAGE_SIZE) : Result<List<CustomerUI>?> {
        return local.getCustomersByPoint(page, pageSize)
    }

    suspend fun addCustomer(customer: Customer) : Result<Unit> {
        return local.addCustomer(customer)
    }
}