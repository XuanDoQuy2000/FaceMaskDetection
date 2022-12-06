package com.xuandq.facemaskdetection.data.local.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xuandq.facemaskdetection.data.model.PointTransaction

@Dao
interface PointTransactionDao {

    @Insert
    fun insert(pointTransaction: PointTransaction)

    @Query("SELECT * FROM PointTransaction WHERE customerId = :customerId ORDER BY timeStamp DESC LIMIT :pageSize OFFSET :offset")
    fun getByCustomer(customerId: Int, offset: Int, pageSize: Int): List<PointTransaction>
}