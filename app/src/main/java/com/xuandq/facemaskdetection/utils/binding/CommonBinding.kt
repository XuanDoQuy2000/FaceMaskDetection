package com.xuandq.facemaskdetection.utils.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.xuandq.facemaskdetection.utils.ext.isTrue

@BindingAdapter("isVisible")
internal fun setVisibility(view: View, visible: Boolean?) {
    view.visibility = if (visible.isTrue()) View.VISIBLE else View.GONE
}

@BindingAdapter("isGone")
fun setGone(view: View, gone: Boolean?) {
    view.visibility = if (gone.isTrue()) View.GONE else View.VISIBLE
}

@BindingAdapter("isInvisible")
fun setInvisibility(view: View, invisible: Boolean?) {
    view.visibility = if (invisible.isTrue()) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("selected")
fun setSelected(view: View, selected: Boolean?) {
    view.isSelected = selected ?: false
}