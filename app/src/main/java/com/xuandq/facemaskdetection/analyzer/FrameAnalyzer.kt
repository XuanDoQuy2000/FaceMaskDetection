package com.xuandq.facemaskdetection.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.xuandq.facemaskdetection.data.repository.FaceEmbeddingRepository
import com.xuandq.facemaskdetection.utils.BitmapUtils
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.classifier.Classifications


class FrameAnalyzer(
    private val context: Context,
    private val isFlipped: Boolean,
    private val faceNetModel: FaceNetModel,
    private val listener: AnalyzeListener,
) : ImageAnalysis.Analyzer {

    private lateinit var bitmapBuffer: Bitmap

    private lateinit var realTimeOpts: FaceDetectorOptions

    private lateinit var faceDetector: FaceDetector

    private lateinit var maskDetection: FaceMaskModel

    var enableDetectMask = false
    var enableRecogFace = false

    init {
        setUpProcessors()
    }

    private fun setUpProcessors() {
        maskDetection = FaceMaskModel(context = context, imageClassifierListener = listener)

        realTimeOpts = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.1f)
            .enableTracking()
            .build()

        faceDetector = FaceDetection.getClient(realTimeOpts)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            detectFace(imageProxy)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectFace(imageProxy: ImageProxy) {
        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, rotation)
        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                listener.onDetectFaceSuccess(faces ?: emptyList())
                if (!faces.isNullOrEmpty()) {
                    // convert original image to bitmap
                    val bitmapProxy = imageProxy.toBitmap() ?: Bitmap.createBitmap(
                        imageProxy.width,
                        imageProxy.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val targetFace = faces.first()
                    val croppedFaces = cropFaceToBitmap(listOf(targetFace), bitmapProxy)
                    bitmapProxy.recycle()
                    if (croppedFaces.isNotEmpty()) {
                        bitmapBuffer = croppedFaces.first()
                        var maskCategory: Category? = Category("",0f)
                        if (enableDetectMask) {
                            maskCategory = maskDetection.classify(bitmapBuffer, 1)
                            listener.onDetectMaskSuccess(targetFace, maskCategory)
                        }
                        if (enableRecogFace && (maskCategory?.index == 1 || maskCategory?.index == -1)) {
                            val customerId = faceNetModel.recognizeFace(bitmapBuffer, FaceEmbeddingRepository.facesEmbedding)
                            listener.onRecognizeFaceSuccess(targetFace, customerId)
                        } else {
                            listener.onRecognizeFaceSuccess(null, null)
                        }
                    } else {
                        listener.onDetectMaskSuccess(null, null)
                        listener.onRecognizeFaceSuccess(null, null)
                    }
                } else {
                    listener.onDetectMaskSuccess(null,null)
                    listener.onRecognizeFaceSuccess(null, null)
                }
                imageProxy.close()
            }.addOnFailureListener {
                imageProxy.close()
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun cropFaceToBitmap(faces: List<Face>, originBitmap: Bitmap): List<Bitmap> {
        val croppedFaces = mutableListOf<Bitmap>()
        for (face in faces) {
            val faceBox = face.boundingBox
            val left = (if (isFlipped) originBitmap.width - faceBox.right else faceBox.left).coerceIn(0, originBitmap.width)
            val right = (if (isFlipped) originBitmap.width - faceBox.left else faceBox.right).coerceIn(0, originBitmap.width)
            val top = faceBox.top.coerceIn(0, originBitmap.height)
            val bottom = faceBox.bottom.coerceIn(0, originBitmap.height)
            val width = right - left
            val height = bottom - top
            if (width > face.boundingBox.width() * 0.75 && height > face.boundingBox.height() * 0.75) {
                val faceBitmap = Bitmap.createBitmap(
                    originBitmap,
                    left,
                    top,
                    width,
                    height,
                )
                croppedFaces.add(faceBitmap)
            }
        }
        return croppedFaces
    }

    interface AnalyzeListener {
        fun onError(error: String)
        fun onDetectFaceSuccess(faces: List<Face>)
        fun onDetectMaskSuccess(
            face: Face?,
            result: Category?
        )
        fun onRecognizeFaceSuccess(face: Face?, customerId: Int?)
    }
}

@SuppressLint("UnsafeOptInUsageError")
fun ImageProxy.toBitmap(): Bitmap? {
    return BitmapUtils.getBitmap(this)
}