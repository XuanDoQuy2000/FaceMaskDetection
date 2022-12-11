package com.xuandq.facemaskdetection.ui.redeem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.data.model.CustomerUI
import com.xuandq.facemaskdetection.databinding.FragmentChooseRewardBinding
import com.xuandq.facemaskdetection.ui.list_reward.ListRewardViewModel
import com.xuandq.facemaskdetection.ui.list_reward.RewardAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseRewardFragment : Fragment() {

    private val rewardAdapter = RewardAdapter()
    private val viewModel: ChooseRewardViewModel by viewModels()
    lateinit var binding: FragmentChooseRewardBinding

    private var customer: CustomerUI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customer = ChooseRewardFragmentArgs.fromBundle(it).customer
        }
        viewModel.getRewards()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.rvReward.adapter = rewardAdapter

        rewardAdapter.itemClickListener = { rewardUI ->
            customer?.let { customer
                QuantityRewardBottomSheet.newInstance(
                    it,
                    rewardUI
                ).show(childFragmentManager,"choose_quantity")
            }
        }

        binding.toolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.rewards.observe(viewLifecycleOwner) {
            rewardAdapter.setData(it)
        }
    }

}