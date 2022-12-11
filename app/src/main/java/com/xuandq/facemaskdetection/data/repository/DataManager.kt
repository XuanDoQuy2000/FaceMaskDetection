package com.xuandq.facemaskdetection.data.repository

import com.xuandq.facemaskdetection.data.local.LocalDataSource
import com.xuandq.facemaskdetection.data.model.*
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.remote.RemoteDataSource
import com.xuandq.facemaskdetection.utils.DEFAULT_PAGE_SIZE
import com.xuandq.facemaskdetection.utils.PointConverter
import javax.inject.Inject
import javax.inject.Singleton

class DataManager @Inject constructor(
    private val local: LocalDataSource,
    private val remote: RemoteDataSource,
) {
    suspend fun getCustomersByPoint(
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Result<List<CustomerUI>> {
        return local.getCustomersByPoint(page, pageSize)
    }

    suspend fun addCustomer(customer: Customer): Result<Unit> {
        return local.addCustomer(customer)
    }

    suspend fun addReward(reward: Reward): Result<Unit> {
        return local.addReward(reward)
    }

    suspend fun getAllReward(): Result<List<RewardUI>> {
        return local.getAllReward()
    }

    suspend fun accumulatePoint(point: Float, money: Int, customerId: Int): Result<Unit> {
        return local.addTransaction(
            PointTransaction(
                id = 0,
                customerId = customerId,
                type = "PLUS",
                timeStamp = System.currentTimeMillis(),
                totalPoint = point,
                money = money
            )
        )
    }

    suspend fun redeemPoint(customerId: Int, rewardId: Int, point: Int, quantity: Int): Result<Unit> {
        return local.addTransaction(
            PointTransaction(
                id = 0,
                customerId = customerId,
                rewardId = rewardId,
                point = point.toFloat(),
                quantity = quantity,
                totalPoint = (point * quantity).toFloat(),
                type = "MINUS",
                timeStamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun getTransactionsByCustomer(customerId: Int, page: Int, pageSize: Int = DEFAULT_PAGE_SIZE): Result<List<PointTransactionUI>> {
        return local.getTransactionByCustomer(customerId, page, pageSize)
    }
}