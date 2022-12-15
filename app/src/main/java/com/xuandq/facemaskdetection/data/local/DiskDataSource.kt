package com.xuandq.facemaskdetection.data.local

import android.content.Context
import android.util.Log
import com.xuandq.facemaskdetection.data.model.Customer
import com.xuandq.facemaskdetection.data.model.Image
import com.xuandq.facemaskdetection.data.model.common.BaseError
import com.xuandq.facemaskdetection.data.model.common.Result
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

class DiskDataSource @Inject constructor(val context: Context) {
    private fun getImages(customerId: Int): List<Image> {
        val folder = File(context.filesDir, customerId.toString())
        val listFileNames = folder.listFiles() ?: arrayOf()
        return listFileNames.map {
            Image(
                path = it.absolutePath,
                name = it.name,
                customerId = customerId,
                extension = it.extension
            )
        }
    }

    private fun insertImageForCustomer(customerId: Int, image: Image) {
        val folder = File(context.filesDir, customerId.toString())
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, image.name)
        val originalFile = File(image.path)
        originalFile.copyTo(file)
    }

    private fun deleteImageForCustomer(customerId: Int, image: Image) {
        val file = File(image.path)
        if (!file.exists()) return
        file.delete()
    }

    suspend fun addImagesForCustomer(customerId: Int, images: List<Image>?): Result<Unit> {
        images?.let {
            supervisorScope {
                for (image in images) {
                    launch {
                        insertImageForCustomer(customerId, image)
                    }
                }
            }
        }
        return Result.Success(Unit)
    }

    suspend fun getImagesByCustomer(customerId: Int): Result<List<Image>> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(getImages(customerId))
            } catch (e: Exception) {
                Result.Error(BaseError.FileError(e.message ?: ""))
            }
        }
    }

    suspend fun updateImagesForCustomer(customerId: Int, images: List<Image>): Result<Unit> =
        withContext(Dispatchers.IO) {
            val getOldList = getImagesByCustomer(customerId)
            if (getOldList is Result.Success) {
                val oldList = getOldList.data
                val removeList = oldList.filter { old -> !images.any { it.path == old.path } }
                val addList = images.filter { new -> !oldList.any { it.path == new.path } }
                supervisorScope {
                    for (remove in removeList) {
                        launch {
                            deleteImageForCustomer(customerId, remove)
                        }
                    }
                    for (add in addList) {
                        launch {
                            insertImageForCustomer(customerId, add)
                        }
                    }
                }
                return@withContext Result.Success(Unit)
            }
            return@withContext Result.Error(BaseError.FileError("Update không thành công"))
        }

    suspend fun deleteAllImageCustomer(customerId: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            val folder = File(context.filesDir, customerId.toString())
            val deleted = folder.deleteRecursively()
            return@withContext if (deleted) {
                Result.Success(Unit)
            } else {
                Result.Error(BaseError.FileError("Xóa ảnh không thành công"))
            }
        }
}