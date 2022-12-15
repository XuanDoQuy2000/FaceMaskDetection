package com.xuandq.facemaskdetection.ui.add_customer

import android.app.Activity.RESULT_OK
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.data.model.Image
import com.xuandq.facemaskdetection.databinding.FragmentAddCustomerBinding
import com.xuandq.facemaskdetection.ui.dialog.NoticeDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddCustomerFragment : Fragment() {

    private lateinit var binding: FragmentAddCustomerBinding
    private val viewModel: AddCustomerViewModel by viewModels()
    private val imageAdapter = ImageAdapter()

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
        binding.lifecycleOwner = viewLifecycleOwner

        binding.rvImage.layoutManager = GridLayoutManager(context, 3)
        binding.rvImage.adapter = imageAdapter

        imageAdapter.onEnableSelectedChange = {
            viewModel.setShowDeleteValue(it)
        }

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
                    .show(parentFragmentManager, "success")
            }
        }

        binding.btnAddImage.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .cropSquare()
                .compress(512)
                .createIntent {
                    startForProfileImageResult.launch(it)
                }
        }

        binding.btnDelete.setOnClickListener {
            viewModel.removeSelectedImages()
        }

        viewModel.images.observe(viewLifecycleOwner) {
            Log.d("ppp", "onViewCreated: $it")
            imageAdapter.setData(it ?: ArrayList(), false)
        }

        viewModel.showDelete.observe(viewLifecycleOwner){

        }

        viewModel.error.observe(viewLifecycleOwner) {
            it ?: return@observe
            NoticeDialog()
                .message(it.message)
                .singleButton(true)
                .show(parentFragmentManager, "error")
            viewModel.error.value = null
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    Log.d("ppp", "file uri: $fileUri")
                    onPickImageSuccess(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun onPickImageSuccess(uri: Uri) {
        context?.let { context ->
            try {
                val path = FileUriUtils.getRealPath(context, uri) ?: ""
                val file = File(path)
                viewModel.addImageToPreview(
                    Image(
                        path = file.absolutePath,
                        name = file.name,
                        extension = file.extension
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi chọn ảnh. Vui lòng thử lại", Toast.LENGTH_SHORT).show()
            }
        }
    }

}