package com.xuandq.facemaskdetection.ui.add_reward

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.databinding.FragmentAddRewardBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddRewardFragment : Fragment() {

    lateinit var binding: FragmentAddRewardBinding
    private val viewModel: AddRewardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.btnButtonRight.setOnClickListener {
            viewModel.addReward(
                binding.edtName.text?.toString(),
                binding.edtDes.text?.toString(),
                binding.edtQuantity.text?.toString(),
                binding.edtPoint.text?.toString()
            ) {
                NoticeDialog()
                    .message("Thêm quà thành công")
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