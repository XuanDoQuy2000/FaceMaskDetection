package com.xuandq.facemaskdetection.ui.list_reward

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.databinding.FragmentListRewardBinding
import com.xuandq.facemaskdetection.ui.list_customer.ListCustomerFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListRewardFragment : Fragment() {

    private val rewardAdapter = RewardAdapter()
    private val viewModel: ListRewardViewModel by viewModels()
    private lateinit var binding: FragmentListRewardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.rvReward.adapter = rewardAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        binding.toolbar.btnButtonRight.setOnClickListener {

        }

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(ListRewardFragmentDirections.actionListRewardFragmentToAddRewardFragment())
        }

        viewModel.rewards.observe(viewLifecycleOwner) {
            rewardAdapter.setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getRewards()
    }
}