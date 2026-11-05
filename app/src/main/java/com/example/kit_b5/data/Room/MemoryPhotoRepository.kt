package com.example.kit_b5.data.Room

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MemoryPhotoRepository(context: Context) {
    private val appContext = context.applicationContext
    private val dao = MemoryPhotoDatabase.getInstance(appContext).photoDao()
    val photos = dao.observePhotos()

    suspend fun add(uri: Uri) = withContext(Dispatchers.IO) {
        val image = PhotoImageEncoder.encode(appContext.contentResolver, uri)
        dao.insert(MemoryPhoto(image = image, timestamp = System.currentTimeMillis()))
    }

    suspend fun delete(id: Long) { dao.delete(id) }

    suspend fun thumbnail(id: Long): ImageBitmap? = withContext(Dispatchers.IO) {
        val image = dao.find(id)?.image ?: return@withContext null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(image, 0, image.size, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 640) sample *= 2
        BitmapFactory.decodeByteArray(image, 0, image.size,
            BitmapFactory.Options().apply { inSampleSize = sample })?.asImageBitmap()
    }
}
