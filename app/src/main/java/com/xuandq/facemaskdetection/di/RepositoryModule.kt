package com.xuandq.facemaskdetection.di

import com.xuandq.facemaskdetection.data.local.LocalDataSource
import com.xuandq.facemaskdetection.data.local.roomdb.CustomerDao
import com.xuandq.facemaskdetection.data.local.roomdb.PointTransactionDao
import com.xuandq.facemaskdetection.data.local.roomdb.RewardDao
import com.xuandq.facemaskdetection.data.remote.RemoteDataSource
import com.xuandq.facemaskdetection.data.repository.DataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    ): LocalDataSource {
        return LocalDataSource(
            customerDao, rewardDao, transactionDao
        )
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(): RemoteDataSource {
        return RemoteDataSource()
    }

    @Singleton
    @Provides
    fun provideRepository(local: LocalDataSource, remote: RemoteDataSource): DataManager {
        return DataManager(local, remote)
    }
}