package com.xuandq.facemaskdetection.data.local.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xuandq.facemaskdetection.data.model.PointTransaction
import com.xuandq.facemaskdetection.data.model.PointTransactionUI

@Dao
interface PointTransactionDao {

    @Insert
    fun insert(pointTransaction: PointTransaction)

    @Query(
        "SELECT PointTransaction.id, " +
                "PointTransaction.customerId, Customer.name AS customerName, " +
                "PointTransaction.rewardId, Reward.name AS rewardName, " +
                "PointTransaction.point, PointTransaction.money, PointTransaction.timeStamp, " +
                "PointTransaction.type, PointTransaction.quantity, PointTransaction.totalPoint " +
                "FROM PointTransaction " +
                "JOIN Customer ON PointTransaction.customerId = Customer.id " +
                "JOIN Reward ON PointTransaction.rewardId = Reward.id OR PointTransaction.rewardId IS NULL " +
                "WHERE customerId = :customerId " +
                "ORDER BY timeStamp " +
                "DESC LIMIT :pageSize " +
                "OFFSET :offset"
    )
    fun getByCustomer(customerId: Int, offset: Int, pageSize: Int): List<PointTransactionUI>
}