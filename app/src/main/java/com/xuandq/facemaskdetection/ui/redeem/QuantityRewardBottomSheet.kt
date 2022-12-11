package com.xuandq.facemaskdetection.ui.redeem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.data.model.RewardUI
import com.xuandq.facemaskdetection.databinding.FragmentQuantityRewardBottomSheetBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuantityRewardBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FragmentQuantityRewardBottomSheetBinding
    private val viewModel : QuantityRewardViewModel by viewModels()

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val customer = it.get("customer") as CustomerUI
            val reward = it.get("reward") as RewardUI
            viewModel.initData(customer, reward)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuantityRewardBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnPlus.setOnClickListener {
            Log.d("ppp", "onViewCreated: +")
            if (viewModel.quantity.value!! < viewModel.getMaxQuantity()) {
                viewModel.quantity.value = viewModel.quantity.value!! + 1
                if (viewModel.quantity.value!! == viewModel.getMaxQuantity()) {
                    binding.btnPlus.isEnabled = false
                }
                binding.btnMinus.isEnabled = true
            }
        }

        binding.btnMinus.setOnClickListener {
            Log.d("ppp", "onViewCreated: -")
            if (viewModel.quantity.value!! > 0) {
                viewModel.quantity.value = viewModel.quantity.value!! - 1
                if (viewModel.quantity.value!! == 0) {
                    binding.btnMinus.isEnabled = false
                }
                binding.btnPlus.isEnabled = true
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnOk.setOnClickListener {
            viewModel.validateRedeem {
                NoticeDialog()
                    .message("Tiếp tục đổi quà ${viewModel.reward.value!!.name} với số lượng ${viewModel.quantity.value}")
                    .singleButton(false)
                    .positiveButton(R.string.action_ok) {
                        viewModel.redeem{ updatedCustomer ->
                            setFragmentResult(
                                "edit_customer",
                                bundleOf("customer_ui" to updatedCustomer)
                            )
                            Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                    .show(parentFragmentManager, "success")
            }
        }


        viewModel.canRedeem.observe(viewLifecycleOwner) {

        }

        viewModel.totalPoint.observe(viewLifecycleOwner) {

        }
    }

    companion object {
        fun newInstance(
            customerUI: CustomerUI,
            reward: RewardUI,
        ): QuantityRewardBottomSheet {
            val args = Bundle()
            args.putParcelable("customer", customerUI)
            args.putParcelable("reward", reward)
            val fragment = QuantityRewardBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

}