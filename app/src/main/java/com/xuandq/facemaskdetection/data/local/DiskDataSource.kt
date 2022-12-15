package com.xuandq.facemaskdetection.data.local.files

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.xuandq.facemaskdetection.data.model.Image
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CustomerFileStore @Inject constructor(val context: Context) {
    suspend fun getImagesByCustomer(customerId: Int): List<Image> {
        val folder = File(context.filesDir, customerId.toString())
        val listFileNames = folder.listFiles()
        return listFileNames.map {
            Image(
                path = it.absolutePath,
                name = it.name,
                customerId = customerId,
            )
        }
    }

    suspend fun insertImageForCustomer(customerId: Int, path: String) {
        val folder = File(context.filesDir, customerId.toString())
        val file = File(folder, System.currentTimeMillis().toString())
        val originalFile = File(path)
        originalFile.copyTo(file)
    }

}