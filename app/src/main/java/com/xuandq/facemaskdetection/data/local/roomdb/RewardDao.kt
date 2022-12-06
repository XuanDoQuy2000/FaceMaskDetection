package com.xuandq.facemaskdetection.data.local.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xuandq.facemaskdetection.data.model.Reward

@Dao
interface RewardDao {

    @Insert
    fun insert(reward: Reward)

    @Update
    fun update(reward: Reward)

    @Query("SELECT * FROM Reward")
    fun getAll(): List<Reward>
}