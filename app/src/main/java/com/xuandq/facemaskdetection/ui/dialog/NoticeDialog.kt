package com.xuandq.facemaskdetection.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.databinding.DialogTwoButtoBinding

class NoticeDialog : DialogFragment() {

    lateinit var binding: DialogTwoButtoBinding

    private var title: CharSequence? = null
    private var titleResId: Int? = null

    private var message: CharSequence? = null
    private var messageResId: Int? = null

    private var negativeButtonResId: Int? = null
    private var positiveButtonResId: Int? = null

    private var negativeAction: (() -> Unit)? = null
    private var positiveAction: (() -> Unit)? = null

    private var singleButton = false

    fun title(title: CharSequence): NoticeDialog {
        this.title = title
        return this
    }

    fun title(resId: Int): NoticeDialog {
        this.titleResId = resId
        return this
    }

    fun message(message: CharSequence): NoticeDialog {
        this.message = message
        return this
    }

    fun message(resId: Int): NoticeDialog {
        this.messageResId = resId
        return this
    }

    fun negativeButton(resId: Int, callback: (() -> Unit)? = null): NoticeDialog {
        this.negativeButtonResId = resId
        this.negativeAction = callback
        return this
    }

    fun positiveButton(resId: Int, callback: (() -> Unit)? = null): NoticeDialog {
        this.positiveButtonResId = resId
        this.positiveAction = callback
        return this
    }

    fun singleButton(single: Boolean): NoticeDialog {
        this.singleButton = single
        return this
    }

    override fun getTheme(): Int {
        return R.style.NoticeDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogTwoButtoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (title != null) {
            binding.tvTitle.text = title
        } else {
            binding.tvTitle.setText(titleResId ?: R.string.notice)
        }

        if (message != null) {
            binding.tvMessage.text = message
        } else {
            binding.tvMessage.setText(messageResId ?: R.string.notice)
        }

        if (singleButton) {
            binding.btnNegative.visibility = View.GONE
            binding.vVerLine.visibility = View.GONE
        } else {
            binding.btnNegative.setText(negativeButtonResId ?: R.string.action_cancel)
            binding.btnNegative.setOnClickListener {
                negativeAction?.invoke()
                dismissAllowingStateLoss()
            }
        }

        binding.btnPositive.setText(positiveButtonResId ?: R.string.action_ok)
        binding.btnPositive.setOnClickListener {
            positiveAction?.invoke()
            dismissAllowingStateLoss()
        }
    }

}