package com.xuandq.facemaskdetection.di

import android.content.Context
import com.xuandq.facemaskdetection.data.local.DatabaseDataSource
import com.xuandq.facemaskdetection.data.local.DiskDataSource
import com.xuandq.facemaskdetection.data.local.roomdb.CustomerDao
import com.xuandq.facemaskdetection.data.local.roomdb.PointTransactionDao
import com.xuandq.facemaskdetection.data.local.roomdb.RewardDao
import com.xuandq.facemaskdetection.data.remote.RemoteDataSource
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLocalDataSource(
        customerDao: CustomerDao,
        transactionDao: PointTransactionDao,
        rewardDao: RewardDao,
    ): DatabaseDataSource {
        return DatabaseDataSource(
            customerDao, rewardDao, transactionDao
        )
    }

    @Singleton
    @Provides
    fun provideDiskDataSource(@ApplicationContext context: Context): DiskDataSource {
        return DiskDataSource(context)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(): RemoteDataSource {
        return RemoteDataSource()
    }

    @Singleton
    @Provides
    fun provideRepository(local: DatabaseDataSource, remote: RemoteDataSource, disk: DiskDataSource): DataManager {
        return DataManager(local, remote, disk)
    }
}