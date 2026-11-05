package com.example.kit_b5.data.Room

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.IOException

/** BLOBをCursorWindowに収まる大きさにし、巨大画像の展開も避ける。 */
object PhotoImageEncoder {
    private const val MAX_EDGE = 1280
    private const val MAX_BYTES = 512 * 1024

    fun encode(resolver: ContentResolver, uri: Uri): ByteArray {
        fun open() = resolver.openInputStream(uri) ?: throw IOException("画像を開けません")
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open().use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IOException("画像を読み込めません")
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / sample > MAX_EDGE * 2) sample *= 2
        var bitmap = open().use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: throw IOException("画像を読み込めません")
        try {
            val exif = open().use { ExifInterface(it) }
            val matrix = Matrix().apply {
                if (exif.isFlipped) postScale(-1f, 1f)
                postRotate(exif.rotationDegrees.toFloat())
            }
            if (!matrix.isIdentity) {
                val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (rotated !== bitmap) { bitmap.recycle(); bitmap = rotated }
            }
            val edge = maxOf(bitmap.width, bitmap.height)
            if (edge > MAX_EDGE) {
                val factor = MAX_EDGE.toFloat() / edge
                val scaled = Bitmap.createScaledBitmap(bitmap,
                    (bitmap.width * factor).toInt().coerceAtLeast(1),
                    (bitmap.height * factor).toInt().coerceAtLeast(1), true)
                if (scaled !== bitmap) { bitmap.recycle(); bitmap = scaled }
            }
            var quality = 90
            while (true) {
                val bytes = ByteArrayOutputStream().use {
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)) throw IOException("画像を保存できません")
                    it.toByteArray()
                }
                if (bytes.size <= MAX_BYTES) return bytes
                if (quality > 40) quality -= 10
                else {
                    val scaled = Bitmap.createScaledBitmap(bitmap,
                        (bitmap.width * .75f).toInt().coerceAtLeast(1),
                        (bitmap.height * .75f).toInt().coerceAtLeast(1), true)
                    if (scaled === bitmap) throw IOException("画像のサイズを調整できません")
                    bitmap.recycle()
                    bitmap = scaled
                }
            }
        } finally { bitmap.recycle() }
    }
}
