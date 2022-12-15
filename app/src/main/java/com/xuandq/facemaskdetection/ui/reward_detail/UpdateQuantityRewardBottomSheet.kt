package com.xuandq.facemaskdetection.ui.reward_detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xuandq.facemaskdetection.databinding.BtsChangeQuantityRewardBinding

class UpdateQuantityRewardBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BtsChangeQuantityRewardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BtsChangeQuantityRewardBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val currQuantity = it.getInt("curr_quantity")
            binding.edtQuantity.setText(currQuantity.toString())
        }

        binding.btnOk.setOnClickListener {
            val value = binding.edtQuantity.text.toString().toIntOrNull()
            Log.d("ppp", "onViewCreated: $value")
            value?.let {
                parentFragmentManager.setFragmentResult("update_quantity", Bundle().apply {
                    putInt("new_quantity", value)
                })
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            currQuantity: Int,
        ): UpdateQuantityRewardBottomSheet {
            val args = Bundle()
            args.putInt("curr_quantity", currQuantity)
            val fragment = UpdateQuantityRewardBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }
}