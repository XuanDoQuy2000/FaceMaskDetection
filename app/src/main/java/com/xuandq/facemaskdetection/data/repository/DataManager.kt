package com.xuandq.facemaskdetection.data.repository

import com.xuandq.facemaskdetection.data.local.DatabaseDataSource
import com.xuandq.facemaskdetection.data.local.DiskDataSource
import com.xuandq.facemaskdetection.data.model.*
import com.xuandq.facemaskdetection.data.model.common.Result
import com.xuandq.facemaskdetection.data.remote.RemoteDataSource
import com.xuandq.facemaskdetection.utils.DEFAULT_PAGE_SIZE
import javax.inject.Inject

class DataManager @Inject constructor(
    private val local: DatabaseDataSource,
    private val remote: RemoteDataSource,
    private val disk: DiskDataSource
) {
    suspend fun getCustomersByPoint(
        keyword: String? = null,
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Result<List<CustomerUI>> {
        return if (!keyword.isNullOrBlank()) local.searchCustomer(
            keyword,
            page,
            pageSize
        ) else local.getCustomersByPoint(page, pageSize)
    }

    suspend fun addCustomer(customer: Customer, images: List<Image>?): Result<Unit> {
        return when (val result = local.addCustomer(customer)) {
            is Result.Success -> {
                disk.addImagesForCustomer(result.data.toInt(), images)
            }
            is Result.Error -> {
                Result.Error(result.error)
            }
        }
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

    suspend fun redeemPoint(
        customerId: Int,
        rewardId: Int,
        point: Int,
        quantity: Int
    ): Result<Unit> {
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

    suspend fun getTransactionsByCustomer(
        customerId: Int,
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Result<List<PointTransactionUI>> {
        return local.getTransactionByCustomer(customerId, page, pageSize)
    }

    suspend fun getImagesByCustomer(customerId: Int): Result<List<Image>> {
        return disk.getImagesByCustomer(customerId)
    }

    suspend fun updateCustomer(customer: Customer, images: List<Image>): Result<Unit> {
        val result = local.updateCustomer(customer)
        return if (result is Result.Success) {
            disk.updateImagesForCustomer(customer.id, images)
        } else {
            result
        }
    }

    suspend fun deleteCustomer(customer: Customer): Result<Unit> {
        val result = local.deleteCustomer(customer)
        return if (result is Result.Success) {
            disk.deleteAllImageCustomer(customer.id)
        } else {
            result
        }
    }

    suspend fun updateReward(reward: Reward): Result<Unit> {
        return local.updateReward(reward)
    }

    suspend fun getAllImageCustomerEmbedding(): Result<List<Pair<Int, FloatArray>>> {
        return disk.getAllImageCustomerEmbedding()
    }
}