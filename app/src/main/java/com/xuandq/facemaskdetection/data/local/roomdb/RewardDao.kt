package com.xuandq.facemaskdetection.data.local.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xuandq.facemaskdetection.data.model.Reward
import com.xuandq.facemaskdetection.data.model.RewardUI

@Dao
interface RewardDao {

    @Insert
    fun insert(reward: Reward)

    @Update
    fun update(reward: Reward)

    @Query("SELECT Reward.id, Reward.name, Reward.description, Reward.point, Reward.totalQuantity, Reward.createdTime, " +
            "SUM((CASE WHEN PointTransaction.type = 'MINUS' then 1 else 0 END) * PointTransaction.quantity) AS usedQuantity " +
            "FROM Reward " +
            "LEFT JOIN PointTransaction " +
            "ON Reward.id = PointTransaction.rewardId " +
            "GROUP BY Reward.id")
    fun getAll(): List<RewardUI>
}