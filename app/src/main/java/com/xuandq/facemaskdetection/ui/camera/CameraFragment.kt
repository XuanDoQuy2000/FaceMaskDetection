package com.xuandq.facemaskdetection.ui.camera

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.face.Face
import com.xuandq.facemaskdetection.R
import com.xuandq.facemaskdetection.analyzer.FaceNetModel
import com.xuandq.facemaskdetection.analyzer.FrameAnalyzer
import com.xuandq.facemaskdetection.databinding.FragmentCameraBinding
import com.xuandq.facemaskdetection.ui.list_customer.CustomerAdapter
import com.xuandq.facemaskdetection.utils.FaceGraphic
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.support.label.Category
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject


@AndroidEntryPoint
class CameraFragment : Fragment(), FrameAnalyzer.AnalyzeListener {

    private lateinit var binding: FragmentCameraBinding

    private lateinit var bitmapBuffer: Bitmap

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private var frameAnalyzer: FrameAnalyzer? = null

    private var enableMaskDetector = false
    private var enableFaceRecognition = false

    private val viewModel: CameraViewModel by viewModels()
    private val adapter = CustomerAdapter()

    @Inject
    lateinit var faceNetModel: FaceNetModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(true)
        binding.lifecycleOwner = viewLifecycleOwner

        BottomSheetBehavior.from(binding.standardBottomSheet.root).apply {
            peekHeight = 50
        }

        binding.standardBottomSheet.rvRecentCustomer.adapter = adapter

        cameraExecutor = Executors.newSingleThreadExecutor()

        frameAnalyzer = context?.let { FrameAnalyzer(it, false, faceNetModel, this) }

//        binding.previewView.post {
//            setUpCamera()
//        }

        binding.btnManageCustomer.setOnClickListener {
            findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToSearchCustomerFragment())
        }

        binding.btnManageReward.setOnClickListener {
            findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToListRewardFragment())
        }

        binding.btnDetectMask.setOnClickListener {
            enableMaskDetector = !enableMaskDetector
            frameAnalyzer?.enableDetectMask = enableMaskDetector
            binding.btnDetectMask.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (enableMaskDetector) R.color.yellow else R.color.white
                )
            )
            if (!enableFaceRecognition) {
                if (enableMaskDetector) {
                    it.post {
                        setUpCamera()
                    }
                } else {
                    cameraProvider?.unbindAll()
                }
            }
        }

        binding.btnRecognizeFace.setOnClickListener {
            enableFaceRecognition = !enableFaceRecognition
            frameAnalyzer?.enableRecogFace = enableFaceRecognition
            binding.btnRecognizeFace.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    if (enableFaceRecognition) R.color.yellow else R.color.white
                )
            )
            if (!enableMaskDetector) {
                if (enableFaceRecognition) {
                    it.post {
                        setUpCamera()
                    }
                } else {
                    cameraProvider?.unbindAll()
                }
            }
        }

        viewModel.recentCustomers.observe(viewLifecycleOwner) {
            Log.d("ppp", "onViewCreated: ${it ?: emptyList()}")
            adapter.setData(it ?: emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setStatusBarColor(false)
        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetRotation(binding.previewView.display.rotation)
                .setTargetResolution(Size(720, 1280))
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetResolution(Size(720, 1280))
                .setTargetRotation(binding.previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    frameAnalyzer?.let { analyzer -> it.setAnalyzer(cameraExecutor, analyzer) }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            Log.d("fff", "onViewCreated: ${binding.previewView.measuredWidth}")

            binding.graphicOverlay.setImageSourceInfo(
                720,
                1280,
                false
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.previewView.surfaceProvider)
        } catch (exc: Exception) {
            Log.e("aaa", "Use case binding failed", exc)
        }
    }

    private fun setStatusBarColor(dark: Boolean) {
        activity?.let {
            val window: Window = it.window
            var flags = window.decorView.systemUiVisibility
            flags = if (dark) {
                flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.decorView.systemUiVisibility = flags
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = if (dark) Color.BLACK else (Color.WHITE)
        }
    }

    override fun onError(error: String) {

    }

    override fun onDetectFaceSuccess(faces: List<Face>) {
        if (::binding.isInitialized) {
            binding.graphicOverlay.clear()
            for (face in faces) {
                binding.graphicOverlay.add(FaceGraphic(binding.graphicOverlay, face))
            }
//            binding.facePreview.setImageBitmap(bitmap)
        }
    }

    var maskTime = 0L
    val listMask = ArrayList<Category?>()
    override fun onDetectMaskSuccess(face: Face?, result: Category?) {
        if (::binding.isInitialized) {
            activity?.runOnUiThread {
                binding.txtDetectMaskResult.text =
                    result.toString()
            }
            if (System.currentTimeMillis() - maskTime <= 1000) {
                listMask.add(result)
            } else {
                val maskCount = listMask.count {
                    it?.index == 1
                }
                maskTime = if (maskCount.toDouble() / listMask.size >= 0.75) {
                    Toast.makeText(context, "Nhắc nhở đeo khẩu trang", Toast.LENGTH_SHORT).show()
                    System.currentTimeMillis() + 5000
                } else {
                    System.currentTimeMillis()
                }
                listMask.clear()
            }
        }
    }

    var recognizeTime = 0L
    val listRecogResult = mutableListOf<Pair<Int?, Face>>()
    override fun onRecognizeFaceSuccess(face: Face?, customerId: Int?) {
        if (::binding.isInitialized) {
            activity?.runOnUiThread {
                binding.txtRecognizeFaceResult.text =
                    customerId.toString()
            }
        }
        Log.d("ppp", "onRecognizeFaceSuccess1:")
        face ?: return
        Log.d("ppp", "onRecognizeFaceSuccess2:")
        if (System.currentTimeMillis() - recognizeTime <= 5000) {
            listRecogResult.add(customerId to face)
        } else {
            Log.d("ppp", "onRecognizeFaceSuccess3:")
            val maxFrequency = listRecogResult.groupBy { it.first }.maxByOrNull { it.value.size }
            recognizeTime = if (maxFrequency != null && maxFrequency.value.size.toDouble() / listRecogResult.size >= 0.0) {
                Log.d("ppp", "onRecognizeFaceSuccess: ${maxFrequency.key}")
                if (maxFrequency.key != null) {
                    viewModel.addCustomerToRecent(maxFrequency.key!!)
                } else {
                    // TODO have new Customer
                }
                System.currentTimeMillis() + 5000
            } else {
                System.currentTimeMillis()
            }
            listRecogResult.clear()
        }
    }
}