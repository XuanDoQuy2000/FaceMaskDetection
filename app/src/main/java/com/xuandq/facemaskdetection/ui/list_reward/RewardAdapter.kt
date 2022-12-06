package com.xuandq.facemaskdetection.ui.list_reward

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xuandq.facemaskdetection.data.model.RewardUI
import com.xuandq.facemaskdetection.databinding.ItemRewardBinding

class RewardAdapter : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    private val rewards = ArrayList<RewardUI>()

    fun setData(newList: List<RewardUI>) {
        rewards.clear()
        rewards.addAll(newList)
        notifyDataSetChanged()
    }

    class RewardViewHolder(val binding: ItemRewardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(reward: RewardUI) {
            binding.reward = reward
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        return RewardViewHolder(
            ItemRewardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.bind(rewards[position])
    }

    override fun getItemCount(): Int {
        return rewards.size
    }
}