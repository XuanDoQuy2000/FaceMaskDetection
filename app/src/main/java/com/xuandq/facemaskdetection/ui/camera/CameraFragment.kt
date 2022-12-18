package com.xuandq.facemaskdetection.ui.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.xuandq.facemaskdetection.analyzer.FrameAnalyzer
import com.xuandq.facemaskdetection.analyzer.FaceNetModel
import com.xuandq.facemaskdetection.databinding.FragmentCameraBinding
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
        binding.lifecycleOwner = viewLifecycleOwner

        cameraExecutor = Executors.newSingleThreadExecutor()

        frameAnalyzer = context?.let { FrameAnalyzer(it, true, faceNetModel, this) }

        binding.previewView.post {
            setUpCamera()
        }

        binding.btnManageCustomer.setOnClickListener {
            findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToSearchCustomerFragment())
        }

        binding.btnManageReward.setOnClickListener {
            findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToListRewardFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

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
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

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
                true
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.previewView.surfaceProvider)
        } catch (exc: Exception) {
            Log.e("aaa", "Use case binding failed", exc)
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
                maskTime = if (maskCount.toDouble() / listMask.size >= 0.75){
                    Toast.makeText(context,"deo khau trang vao ku", Toast.LENGTH_SHORT).show()
                    System.currentTimeMillis() + 5000
                } else {
                    System.currentTimeMillis()
                }
                listMask.clear()
            }
        }
    }

    override fun onRecognizeFaceSuccess(face: Face?, customerId: Int?) {
        if (::binding.isInitialized) {
            activity?.runOnUiThread {
                binding.txtRecognizeFaceResult.text =
                    customerId.toString()
            }
        }
    }
}