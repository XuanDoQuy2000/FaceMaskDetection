package com.xuandq.facemaskdetection.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.Image
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.xuandq.facemaskdetection.utils.BitmapUtils
import com.xuandq.facemaskdetection.utils.FrameMetadata
import java.nio.ByteBuffer
import kotlin.math.abs


class FaceMaskAnalyzer(
    private val context: Context,
    private val listener: ImageClassifierHelper.ClassifierListener,
) : ImageAnalysis.Analyzer {

    private lateinit var bitmapBuffer: Bitmap

    private lateinit var realTimeOpts: FaceDetectorOptions

    private lateinit var faceDetector: FaceDetector

    private lateinit var maskDetection: ImageClassifierHelper

    init {
        setUpProcessors()
    }

    private fun setUpProcessors() {
        maskDetection = ImageClassifierHelper(context = context, imageClassifierListener = listener)

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
                if (!faces.isNullOrEmpty()) {
                    Log.d("aaa", "detectFace: ")
                    val bitmapProxy = imageProxy.toBitmap() ?: Bitmap.createBitmap(
                        imageProxy.width,
                        imageProxy.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val croppedFaces = cropFaceToBitmap(faces, bitmapProxy)
                    bitmapProxy.recycle()
                    if (croppedFaces.isNotEmpty()) {
                        bitmapBuffer = croppedFaces.first()
                        detectMask(rotation)
                    }
                } else {
                    listener.onResults(null,0)
                }
                imageProxy.close()
            }.addOnFailureListener {
                imageProxy.close()
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun detectMask(rotation: Int) {
        maskDetection.classify(bitmapBuffer, rotation)
    }

    private fun cropFaceToBitmap(faces: List<Face>, originBitmap: Bitmap): List<Bitmap> {
        val croppedFaces = mutableListOf<Bitmap>()
        for (face in faces) {
//            Log.d("bbb", "detectFace:${originBitmap.width} - ${originBitmap.height}")
//            Log.d(
//                "bbb",
//                "detectFace: ${face.boundingBox.left} - ${face.boundingBox.right} - ${face.boundingBox.top} - ${face.boundingBox.bottom}"
//            )
            val faceBox = face.boundingBox
            val left = faceBox.left.coerceIn(0, originBitmap.width)
            val right = faceBox.right.coerceIn(0, originBitmap.width)
            val top = faceBox.top.coerceIn(0, originBitmap.height)
            val bottom = faceBox.bottom.coerceIn(0, originBitmap.height)
            val width = right - left
            val height = bottom - top
            Log.d("bbb", "cropFaceToBitmap: $width - $height")
            if (width > 10 && height > 10) {
                croppedFaces.add(
                    Bitmap.createBitmap(
                        originBitmap,
                        left,
                        top,
                        width,
                        height,
                    )
                )
            }
        }
        return croppedFaces
    }

}

@SuppressLint("UnsafeOptInUsageError")
fun ImageProxy.toBitmap(): Bitmap? {
    return BitmapUtils.getBitmap(this)
}