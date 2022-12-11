package com.xuandq.facemaskdetection.ui.accumulate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.databinding.FragmentAccumulatePointBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import com.xuandq.facemaskdetection.utils.ext.toPointString
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class AccumulatePointFragment : Fragment() {
    lateinit var binding: FragmentAccumulatePointBinding
    private val viewModel: AccumulatePointViewModel by viewModels()

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val decimalFormat = DecimalFormat()
            val money = try {
                decimalFormat.parse(s?.toString() ?: "")
            } catch (e: Exception) {
                null
            }
            Log.d("ppp", "afterTextChanged: ${money?.toInt()}")
            viewModel.setMoney(money?.toInt())
            money?.let {
                val stringFormatted = decimalFormat.format(money)
                binding.edtAmountOfMoney.removeTextChangedListener(this)
                binding.edtAmountOfMoney.setText(stringFormatted)
                binding.edtAmountOfMoney.setSelection(stringFormatted.length)
                binding.edtAmountOfMoney.addTextChangedListener(this)
            }

        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val customer = AccumulatePointFragmentArgs.fromBundle(it).customer
            viewModel.initData(customer)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccumulatePointBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAccumulatePoint.setOnClickListener {
            viewModel.validatePoint {
                NoticeDialog()
                    .message("Tiếp tục tích điểm với số tiền\n ${viewModel.getMoneyValue()}")
                    .singleButton(false)
                    .positiveButton(R.string.action_ok) {
                        viewModel.accumulatePoint { updatedCustomer ->
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

        viewModel.point.observe(viewLifecycleOwner) {
            val pointText = context?.getString(R.string.value_point_convert_format)
                ?.format(viewModel.getPointValue()?.toPointString() ?: "") ?: ""
            binding.tvPointValue.text = pointText
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
        binding.edtAmountOfMoney.addTextChangedListener(textWatcher)
    }

    override fun onPause() {
        super.onPause()
        binding.edtAmountOfMoney.removeTextChangedListener(textWatcher)
    }

}