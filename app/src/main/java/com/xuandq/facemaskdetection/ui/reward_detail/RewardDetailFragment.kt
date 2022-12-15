package com.xuandq.facemaskdetection.ui.reward_detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.databinding.FragmentRewardDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RewardDetailFragment : Fragment() {

    lateinit var binding: FragmentRewardDetailBinding
    private val viewModel : RewardDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val reward = RewardDetailFragmentArgs.fromBundle(it).reward
            viewModel.initViewModel(reward)
        }
        childFragmentManager.setFragmentResultListener("update_quantity", this) { _, bundle ->
            Log.d("ppp", "onCreate: ")
            val newQuantity = bundle.get("new_quantity") as Int
            viewModel.updateRewardQuantity(newQuantity) {
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardDetailBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.btnButtonRight.setOnClickListener {
            UpdateQuantityRewardBottomSheet.newInstance(
                viewModel.getRewardTotalQuantity()
            ).show(childFragmentManager, "update_quantity")
        }
    }
}