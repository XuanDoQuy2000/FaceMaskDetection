package com.xuandq.facemaskdetection.data.local.roomdb

import androidx.paging.PagingSource
import androidx.room.*
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI

@Dao
interface CustomerDao {

    @Insert
    fun insert(customer: Customer): Long

    @Delete
    fun delete(customer: Customer)

    @Update
    fun update(customer: Customer)

    @Query("SELECT Customer.id, Customer.name, Customer.phoneNumber, Customer.createdTime, " +
            "SUM((CASE WHEN PointTransaction.type = 'PLUS' then 1 else -1 END) * PointTransaction.totalPoint) AS currentPoint, " +
            "SUM((CASE WHEN PointTransaction.type = 'PLUS' then 1 else 0 END) * PointTransaction.totalPoint) AS totalPoint " +
            "FROM Customer " +
            "LEFT JOIN PointTransaction " +
            "ON Customer.id = PointTransaction.customerId " +
            "GROUP BY Customer.id " +
            "ORDER BY totalPoint ASC LIMIT :pageSize OFFSET :offset")
    fun getCustomersByPoint(offset: Int, pageSize: Int): List<CustomerUI>

    @Query("SELECT Customer.id, Customer.name, Customer.phoneNumber, Customer.createdTime, " +
            "SUM((CASE WHEN PointTransaction.type = 'PLUS' then 1 else -1 END) * PointTransaction.totalPoint) AS currentPoint, " +
            "SUM((CASE WHEN PointTransaction.type = 'PLUS' then 1 else 0 END) * PointTransaction.totalPoint) AS totalPoint " +
            "FROM Customer " +
            "LEFT JOIN PointTransaction " +
            "ON Customer.id = PointTransaction.customerId " +
            "WHERE (name LIKE '%' || :keyword || '%') OR (phoneNumber LIKE '%' || :keyword || '%')" +
            "GROUP BY Customer.id " +
            "ORDER BY totalPoint ASC LIMIT :pageSize OFFSET :offset")
    fun searchCustomers(keyword: String, offset: Int, pageSize: Int): List<CustomerUI>
}