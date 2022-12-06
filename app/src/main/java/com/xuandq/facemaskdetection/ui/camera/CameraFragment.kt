package com.xuandq.facemaskdetection.ui.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.xuandq.facemaskdetection.analyzer.FaceMaskAnalyzer
import com.xuandq.facemaskdetection.analyzer.ImageClassifierHelper
import com.xuandq.facemaskdetection.databinding.FragmentCameraBinding
import com.xuandq.facemaskdetection.utils.FaceGraphic
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    private lateinit var binding: FragmentCameraBinding

    private lateinit var bitmapBuffer: Bitmap

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private var faceMaskAnalyzer: FaceMaskAnalyzer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        faceMaskAnalyzer = context?.let { FaceMaskAnalyzer(it, true, this) }

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
                    faceMaskAnalyzer?.let { analyzer -> it.setAnalyzer(cameraExecutor, analyzer) }
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

    override fun onResults(face: Face?, results: List<Classifications>?, inferenceTime: Long) {
        if (::binding.isInitialized) {
            val firstClassfication = results?.firstOrNull()
            val categories = firstClassfication?.categories?.sortedBy { it.score }
            val firstCategory = categories?.firstOrNull()
            Log.d("aaa", "onResults: result = ${firstCategory.toString()}")
            activity?.runOnUiThread {
                binding.txtResult.text =
                    "${firstCategory.toString()}"
            }
        }
    }

    override fun onFaceDetected(faces: List<Face>, bitmap: Bitmap) {
        if (::binding.isInitialized) {
            binding.graphicOverlay.clear()
            for (face in faces) {
                binding.graphicOverlay.add(FaceGraphic(binding.graphicOverlay, face))
            }
            binding.facePreview.setImageBitmap(bitmap)
        }
    }
}