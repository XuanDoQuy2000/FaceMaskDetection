package com.xuandq.facemaskdetection.ui.add_customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.databinding.FragmentAddCustomerBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCustomerFragment : Fragment() {

    private lateinit var binding: FragmentAddCustomerBinding
    private val viewModel: AddCustomerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.btnButtonRight.setOnClickListener {
            viewModel.addCustomer(
                binding.edtName.text?.toString(),
                binding.edtPhone.text?.toString()
            ) {
                NoticeDialog()
                    .message("Thêm khách hàng thành công")
                    .singleButton(true)
                    .positiveButton(R.string.action_ok) {
                        findNavController().popBackStack()
                    }
                    .show(parentFragmentManager,"success")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it ?: return@observe
            NoticeDialog()
                .message(it.message)
                .singleButton(true)
                .show(parentFragmentManager,"error")
            viewModel.error.value = null
        }
    }

}