package com.xuandq.facemaskdetection.utils.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.xuandq.facemaskdetection.utils.ext.toPointString
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(value = ["point", "signed", "defaultPoint"], requireAll = false)
fun setPointFloat(view: TextView, value: Float?, type: String?, defaultValue: Float?) {
    val signed = when (type) {
        "MINUS" -> {
            "-"
        }
        "PLUS" -> {
            "+"
        }
        else -> {
            null
        }
    }
    view.text = "${signed ?: ""}${value?.toPointString() ?: defaultValue ?: ""}"
}

@BindingAdapter("pointInt")
fun setPointInt(view: TextView, value: Int?) {
    view.text = value?.toString() ?: ""
}

@BindingAdapter("dateTime")
fun setDateTimeByLong(view: TextView, value: Long?) {
    if (value == null) view.text = ""
    val dateTime = Date(value!!)
    view.text = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(dateTime)
}

@BindingAdapter("number")
fun setNumberInt(view: TextView, value: Int?) {
    view.text = value?.toString() ?: ""
}