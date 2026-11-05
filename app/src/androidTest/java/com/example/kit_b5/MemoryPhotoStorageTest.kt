package com.example.kit_b5

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kit_b5.data.Room.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MemoryPhotoStorageTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var database: MemoryPhotoDatabase
    private lateinit var databaseName: String

    @Before fun setup() {
        databaseName = "photo-test-${UUID.randomUUID()}.db"
        database = Room.databaseBuilder(context, MemoryPhotoDatabase::class.java, databaseName).build()
    }

    @After fun cleanup() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test fun imagesAndTimestampsSurviveReopenAndDeletionUpdatesGallery() = runBlocking {
        val bytes = byteArrayOf(1, 2, 3, 4)
        val firstId = database.photoDao().insert(MemoryPhoto(image = bytes, timestamp = 100))
        val secondId = database.photoDao().insert(MemoryPhoto(image = byteArrayOf(5), timestamp = 200))
        assertNotEquals(firstId, secondId)
        database.close()
        database = Room.databaseBuilder(context, MemoryPhotoDatabase::class.java, databaseName).build()
        val restored = requireNotNull(database.photoDao().find(firstId))
        assertArrayEquals(bytes, restored.image)
        assertEquals(100L, restored.timestamp)
        assertEquals(listOf(secondId, firstId), database.photoDao().observePhotos().first().map { it.id })
        database.photoDao().delete(secondId)
        assertEquals(listOf(firstId), database.photoDao().observePhotos().first().map { it.id })
        database.photoDao().delete(firstId)
        assertTrue(database.photoDao().observePhotos().first().isEmpty())
        assertNull(database.photoDao().find(firstId))
    }

    @Test fun largeImageIsReducedAndStoredBelowBlobLimit() {
        val source = File.createTempFile("large-photo", ".jpg", context.cacheDir)
        try {
            val bitmap = Bitmap.createBitmap(3000, 2000, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.MAGENTA)
            source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
            bitmap.recycle()
            val image = PhotoImageEncoder.encode(context.contentResolver, Uri.fromFile(source))
            assertTrue(image.size <= 512 * 1024)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(image, 0, image.size, bounds)
            assertTrue(maxOf(bounds.outWidth, bounds.outHeight) <= 1280)
            assertTrue(bounds.outWidth > 0 && bounds.outHeight > 0)
        } finally { source.delete() }
    }

    @Test fun imageOrientationIsAppliedBeforeSaving() {
        val source = File.createTempFile("rotated-photo", ".jpg", context.cacheDir)
        try {
            val bitmap = Bitmap.createBitmap(60, 30, Bitmap.Config.ARGB_8888)
            source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            bitmap.recycle()
            ExifInterface(source).apply {
                setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90.toString())
                saveAttributes()
            }
            val image = PhotoImageEncoder.encode(context.contentResolver, Uri.fromFile(source))
            val decoded = requireNotNull(BitmapFactory.decodeByteArray(image, 0, image.size))
            assertEquals(30, decoded.width)
            assertEquals(60, decoded.height)
            decoded.recycle()
        } finally { source.delete() }
    }

    @Test(expected = IOException::class) fun invalidImageIsRejected() {
        val source = File.createTempFile("invalid-photo", ".jpg", context.cacheDir)
        try {
            source.writeText("not an image")
            PhotoImageEncoder.encode(context.contentResolver, Uri.fromFile(source))
        } finally { source.delete() }
    }
}
