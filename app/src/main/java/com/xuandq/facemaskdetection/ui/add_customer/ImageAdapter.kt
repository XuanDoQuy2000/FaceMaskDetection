package com.xuandq.facemaskdetection.ui.add_customer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xuandq.facemaskdetection.data.model.Image
import com.xuandq.facemaskdetection.databinding.ItemImageBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val images = ArrayList<Image>()
    var enableSelect = false

    var onLongItemClickListener: ((Image) -> Unit)? = null

    var onItemClickListener: ((Image) -> Unit)? = null

    var onEnableSelectedChange: ((Boolean) -> Unit)? = null

    fun setData(newList: List<Image>, enableSelected: Boolean? = false){
        newList.forEach {
            it.isSelected = false
        }
        images.clear()
        images.addAll(newList)
        enableSelected?.let { enableSelect = it }
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            binding.image = image
            binding.enableSelect = enableSelect

            binding.root.setOnLongClickListener {
                if (!enableSelect) {
                    enableSelect = true
                    image.isSelected = true
                    onEnableSelectedChange?.invoke(true)
                    onLongItemClickListener?.invoke(image)
                    notifyDataSetChanged()
                }
                false
            }
            binding.root.setOnClickListener {
                if (enableSelect) {
                    image.isSelected = !image.isSelected
                    enableSelect = images.any { it.isSelected }
                    onEnableSelectedChange?.invoke(enableSelect)
                    onItemClickListener?.invoke(image)
                    notifyDataSetChanged()
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

}