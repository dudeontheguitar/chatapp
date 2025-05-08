package com.example.chat.presentation.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

suspend fun cropImageToSquareAndCache(context: Context, uri: Uri): Uri? = withContext(Dispatchers.IO) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val size = min(original.width, original.height)
        val x = (original.width - size) / 2
        val y = (original.height - size) / 2
        val croppedBitmap = Bitmap.createBitmap(original, x, y, size, size)

        val file = File(context.cacheDir, "cropped_avatar.jpg")
        val outputStream = FileOutputStream(file)
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        outputStream.flush()
        outputStream.close()

        return@withContext FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
