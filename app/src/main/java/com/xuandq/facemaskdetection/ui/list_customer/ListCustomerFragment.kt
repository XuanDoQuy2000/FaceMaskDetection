package com.xuandq.facemaskdetection.ui.list_customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.databinding.FragmentListCustomerBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCustomerFragment : Fragment() {

    private lateinit var binding: FragmentListCustomerBinding
    private val viewModel: ListCustomerViewModel by viewModels()

    private val customerAdapter = CustomerAdapter()

    private val textWatcher by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchCustomers(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchCustomers(newText ?: "")
                return true
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.rvCustomer.adapter = customerAdapter

        customerAdapter.itemClickListener = {
            findNavController().navigate(
                ListCustomerFragmentDirections.actionSearchCustomerFragmentToCustomerDetailFragment(
                    it
                )
            )
        }

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

        viewModel.error.observe(viewLifecycleOwner) {
            it ?: return@observe
            NoticeDialog()
                .message(it.message)
                .singleButton(true)
                .show(parentFragmentManager,"error")
            viewModel.error.value = null
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCustomers()
        binding.edtSearch.setOnQueryTextListener(textWatcher)
    }

    override fun onPause() {
        super.onPause()
        binding.edtSearch.setOnQueryTextListener(null)
    }

}