package com.xuandq.facemaskdetection.ui.list_reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xuandq.facemaskdetection.databinding.BtsChangeQuantityRewardBinding

class UpdateQuantityRewardBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BtsChangeQuantityRewardBinding

    var onUpdated: ((Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BtsChangeQuantityRewardBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun newInstance(
        currQuantity: Int,
        onUpdated: (Int) -> Unit,
    ): UpdateQuantityRewardBottomSheet {
        val fragment = UpdateQuantityRewardBottomSheet()
        fragment.onUpdated = onUpdated
        return fragment
    }
}