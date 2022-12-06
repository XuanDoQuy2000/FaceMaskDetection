package com.xuandq.facemaskdetection.ui.list_customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.databinding.ItemCustomerBinding

class CustomerAdapter() : RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {
    private val customers = ArrayList<CustomerUI>()

    fun setData(customers: List<CustomerUI>) {
        this.customers.clear()
        this.customers.addAll(customers)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(customer: CustomerUI) {
            binding.customer = customer
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_customer,
            parent,
            false
        ) as ItemCustomerBinding
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = customers[position]
        holder.bind(customer)
    }

    override fun getItemCount(): Int {
        return customers.size
    }
}

