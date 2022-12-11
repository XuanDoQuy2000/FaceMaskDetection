package com.xuandq.facemaskdetection.data.local.roomdb

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.PointTransaction
import com.xuandq.facemaskdetection.data.model.Reward

@Database(
    entities = [Customer::class, Reward::class, PointTransaction::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun rewardDao(): RewardDao
    abstract fun transactionDao(): PointTransactionDao
}