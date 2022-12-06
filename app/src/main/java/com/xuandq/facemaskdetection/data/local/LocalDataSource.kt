package com.xuandq.facemaskdetection.data.local

import com.xuandq.facemaskdetection.data.local.roomdb.CustomerDao
import com.xuandq.facemaskdetection.data.local.roomdb.PointTransactionDao
import com.xuandq.facemaskdetection.data.local.roomdb.RewardDao
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
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

    suspend fun addCustomer(customer: Customer) : Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Result.Success(customerDao.insert(customer))
        } catch (e: Exception) {
            Result.Error(BaseError.DBError(e.message ?: ""))
        }
    }

    suspend fun getCustomersByPoint(page: Int, pageSize: Int): Result<List<CustomerUI>?> = withContext(Dispatchers.IO){
        return@withContext try {
            Result.Success(customerDao.getCustomersByPoint(page * pageSize, pageSize))
        } catch (e : Exception) {
            Result.Error(BaseError.DBError(e.message ?: ""))
        }
//        delay(1000)
//        return Result.Success(
//            listOf(
//                Customer(
//                    id = 0,
//                    name = "Do Quy Xuan",
//                    phoneNumber = "3045802348"
//                ),
//                Customer(
//                    id = 1,
//                    name = "Nguyen Thuc Thuy Tien",
//                    phoneNumber = "3045802348"
//                ),
//                Customer(
//                    id = 2,
//                    name = "Tran Hoai Nam",
//                    phoneNumber = "3045802348"
//                ),
//                Customer(
//                    id = 3,
//                    name = "Tran Nguyen Thionh",
//                    phoneNumber = "3045802348"
//                ),
//                Customer(
//                    id = 4,
//                    name = "Nguyen Minh Cuong",
//                    phoneNumber = "3045802348"
//                ),
//            )
//        )
    }
}