package com.xuandq.facemaskdetection.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xuandq.facemaskdetection.data.model.PointTransactionUI
import com.xuandq.facemaskdetection.databinding.ItemTransactionBinding

class HistoryAdapter(): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val transactions = ArrayList<PointTransactionUI>()

    fun setData(newList: List<PointTransactionUI>){
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: PointTransactionUI){
            binding.transaction = transaction
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}