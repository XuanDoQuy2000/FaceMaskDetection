package com.xuandq.facemaskdetection.data.local

import com.xuandq.facemaskdetection.data.local.roomdb.CustomerDao
import com.xuandq.facemaskdetection.data.local.roomdb.PointTransactionDao
import com.xuandq.facemaskdetection.data.local.roomdb.RewardDao
import com.xuandq.facemaskdetection.data.model.*
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class LocalDataSource @Inject constructor(
    private val customerDao: CustomerDao,
    private val rewardDao: RewardDao,
    private val transactionDao: PointTransactionDao,
) {

    suspend fun addCustomer(customer: Customer): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(customerDao.insert(customer))
        } catch (e: Exception) {
            Result.Error(BaseError.DBError(e.message ?: ""))
        }
    }

    suspend fun getCustomersByPoint(page: Int, pageSize: Int): Result<List<CustomerUI>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(customerDao.getCustomersByPoint(page * pageSize, pageSize))
            } catch (e: Exception) {
                Result.Error(BaseError.DBError(e.message ?: ""))
            }
        }

    suspend fun addReward(reward: Reward): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(rewardDao.insert(reward))
        } catch (e: Exception) {
            Result.Error(BaseError.DBError(e.message ?: ""))
        }
    }

    suspend fun getAllReward(): Result<List<RewardUI>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(rewardDao.getAll())
        } catch (e: Exception) {
            Result.Error(BaseError.DBError(e.message ?: ""))
        }
    }

    suspend fun addTransaction(pointTransaction: PointTransaction): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(transactionDao.insert(pointTransaction))
            } catch (e: Exception) {
                Result.Error(BaseError.DBError(e.message ?: ""))
            }
        }

    suspend fun getTransactionByCustomer(
        customerId: Int,
        page: Int,
        pageSize: Int
    ): Result<List<PointTransactionUI>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(transactionDao.getByCustomer(customerId, page * pageSize, pageSize))
        } catch (e: Exception) {
            Result.Error(BaseError.DBError(e.message ?: ""))
        }
    }
}