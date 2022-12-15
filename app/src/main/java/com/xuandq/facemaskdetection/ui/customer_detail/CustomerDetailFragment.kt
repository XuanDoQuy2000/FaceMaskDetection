package com.xuandq.facemaskdetection.ui.customer_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.databinding.FragmentCustomerDetailBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerDetailFragment : Fragment() {

    lateinit var binding: FragmentCustomerDetailBinding
    private val viewModel: CustomerDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val customer = CustomerDetailFragmentArgs.fromBundle(it).customer
            viewModel.initData(customer)
        }

        setFragmentResultListener("edit_customer") { requestKey, bundle ->
            val customerUI = bundle.get("customer_ui") as CustomerUI
            viewModel.initData(customerUI)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.btnButtonRight.setOnClickListener {
            findNavController().navigate(
                CustomerDetailFragmentDirections.actionCustomerDetailFragmentToEditCustomerFragment(
                    viewModel.getCustomerValue()!!
                )
            )
        }

        binding.toolbar.btnButtonRight2.setOnClickListener {
            NoticeDialog()
                .message("Bạn muốn xóa khách hàng này?")
                .singleButton(false)
                .positiveButton(R.string.action_ok) {
                    viewModel.deleteCustomer {
                        Toast.makeText(context, "Xóa Thành công", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
                .show(parentFragmentManager, "success")
        }

        binding.btnAccumulate.setOnClickListener {
            if (viewModel.getCustomerValue() != null) {
                findNavController().navigate(
                    CustomerDetailFragmentDirections.actionCustomerDetailFragmentToAccumulatePointFragment(
                        viewModel.getCustomerValue()!!
                    )
                )
            }
        }

        binding.btnHistory.setOnClickListener {
            if (viewModel.getCustomerValue() != null) {
                findNavController().navigate(
                    CustomerDetailFragmentDirections.actionCustomerDetailFragmentToHistoryFragment(
                        viewModel.getCustomerValue()!!
                    )
                )
            }
        }

        binding.btnRedeem.setOnClickListener {
            if (viewModel.getCustomerValue() != null) {
                findNavController().navigate(
                    CustomerDetailFragmentDirections.actionCustomerDetailFragmentToChooseRewardFragment(
                        viewModel.getCustomerValue()!!
                    )
                )
            }
        }
    }
}