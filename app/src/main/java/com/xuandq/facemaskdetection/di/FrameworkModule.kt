package com.xuandq.facemaskdetection.di

import android.content.Context
import androidx.room.Room
import com.xuandq.facemaskdetection.data.local.roomdb.AppDatabase
import com.xuandq.facemaskdetection.data.local.roomdb.CustomerDao
import com.xuandq.facemaskdetection.data.local.roomdb.PointTransactionDao
import com.xuandq.facemaskdetection.data.local.roomdb.RewardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FrameworkModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "db"
        ).build()
    }

    @Provides
    fun provideCustomerDao(db: AppDatabase): CustomerDao {
        return db.customerDao()
    }

    @Provides
    fun provideRewardDao(db: AppDatabase): RewardDao {
        return db.rewardDao()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): PointTransactionDao {
        return db.transactionDao()
    }
}