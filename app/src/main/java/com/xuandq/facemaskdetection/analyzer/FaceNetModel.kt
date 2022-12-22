package com.xuandq.facemaskdetection.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class FaceNetModel @Inject constructor(
    context: Context,
    private val l2Threshold: Float,
    private val outputDims: Int,
    inputDims: Int,
) {
    private var interpreter: Interpreter
    private val imageTensorProcessor = ImageProcessor.Builder()
        .add(ResizeOp(inputDims, inputDims, ResizeOp.ResizeMethod.BILINEAR))
        .add(StandardizeOp())
        .build()

    init {
        val compatList = CompatibilityList()
        val interpreterOptions = Interpreter.Options().apply {
            if(compatList.isDelegateSupportedOnThisDevice){
                val delegateOptions = compatList.bestOptionsForThisDevice
                this.addDelegate(GpuDelegate(delegateOptions))
            } else {
                this.numThreads = 4
            }
        }
        interpreter =
            Interpreter(FileUtil.loadMappedFile(context, MODEL_NAME), interpreterOptions)
    }

    fun getFaceEmbedding(image: Bitmap): FloatArray {
        return runFaceNet(convertBitmapToBuffer(image))[0]
    }

    fun recognizeFace(
        imageFace: Bitmap,
        embeddingCustomers: ArrayList<Pair<Int, FloatArray>>
    ): Int? {
        val inputFaceEmbedding = getFaceEmbedding(imageFace)
        val scoreHashMap = HashMap<Int, ArrayList<Float>>()
        for (customer in embeddingCustomers) {
            val l2NormValue = calculateL2Norm(inputFaceEmbedding, customer.second)
            if (scoreHashMap[customer.first] == null) {
                val p = ArrayList<Float>()
                p.add(l2NormValue)
                scoreHashMap[customer.first] = p
            } else {
                scoreHashMap[customer.first]!!.add(l2NormValue)
            }
        }
        val minScore = scoreHashMap.mapValues { it.value.average() }.minByOrNull {
            it.value
        }
        minScore ?: return null
        return if (minScore.value > l2Threshold) {
            null
        } else {
            minScore.key
        }
    }

    private fun runFaceNet(inputs: Any): Array<FloatArray> {
        val t1 = System.currentTimeMillis()
        val faceNetModelOutputs = Array(1) { FloatArray(outputDims) }
        interpreter.run(inputs, faceNetModelOutputs)
        Log.i("Performance", "Inference Speed in ms : ${System.currentTimeMillis() - t1}")
        return faceNetModelOutputs
    }

    // Resize the given bitmap and convert it to a ByteBuffer
    private fun convertBitmapToBuffer(image: Bitmap): ByteBuffer {
        return imageTensorProcessor.process(TensorImage.fromBitmap(image)).buffer
    }

    private fun calculateL2Norm(x1: FloatArray, x2: FloatArray): Float {
        return sqrt(x1.mapIndexed { i, xi -> (xi - x2[i]).pow(2) }.sum())
    }

    companion object {
        const val MODEL_NAME = "facenet.tflite"
    }

    inner class StandardizeOp : TensorOperator {
        override fun apply(buffer: TensorBuffer?): TensorBuffer {
            val pixels = buffer!!.floatArray
            val mean = pixels.average().toFloat()
            var std = sqrt(pixels.map { pi -> (pi - mean).pow(2) }.sum() / pixels.size.toFloat())
            std = max(std, 1f / sqrt(pixels.size.toFloat()))
            for (i in pixels.indices) {
                pixels[i] = (pixels[i] - mean) / std
            }
            val output = TensorBufferFloat.createFixedSize(buffer.shape, DataType.FLOAT32)
            output.loadArray(pixels)
            return output
        }
    }
}