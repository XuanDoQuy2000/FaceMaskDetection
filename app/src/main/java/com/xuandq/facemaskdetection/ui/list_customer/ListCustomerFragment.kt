package com.xuandq.facemaskdetection.ui.list_customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.databinding.FragmentListCustomerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCustomerFragment : Fragment() {

    private lateinit var binding: FragmentListCustomerBinding
    private val viewModel: ListCustomerViewModel by viewModels()

    private val customerAdapter = CustomerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListCustomerBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.rvCustomer.adapter = customerAdapter

        binding.toolbar.btnButtonRight.setOnClickListener {

        }

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(ListCustomerFragmentDirections.actionSearchCustomerFragmentToAddCustomerFragment())
        }

        viewModel.customers.observe(viewLifecycleOwner) {
            customerAdapter.setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCustomers()
    }

}