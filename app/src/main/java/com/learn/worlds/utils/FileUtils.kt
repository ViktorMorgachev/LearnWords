package com.learn.worlds.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import timber.log.Timber
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files


fun File.isMp3File(): Boolean{
    try {
        if (!this.exists()) return false
        val allBytes = Files.readAllBytes(FileSystems.getDefault().getPath(this.path))
        return isMp3Signature(allBytes)
    } catch (t: Throwable) {
        Timber.e(t)
    }
    return false
}

fun File?.toBitmap(): Bitmap?{
    if (this == null) return null
    if (this.isImage()){
        return BitmapFactory.decodeFile(this.absolutePath)
    }
    return null
}

fun File?.isImage(): Boolean {
    return try {
        if (this?.exists() == false) return false
        this?.isPngFile() == true || this?.isJpegFile() == true
    } catch (t: NoSuchFileException){
        false
    }

}


private fun File.isJpegFile(): Boolean{
    try {
        val allBytes = Files.readAllBytes(FileSystems.getDefault().getPath(this.path))
        return isJpegSignature(allBytes)
    } catch (t: Throwable) {
        Timber.e(t)
    }
    return false
}

private fun File.isPngFile(): Boolean{
    try {
        val allBytes = Files.readAllBytes(FileSystems.getDefault().getPath(this.path))
        return isPngSignature(allBytes)
    } catch (t: Throwable) {
        Timber.e(t)
    }
    return false
}

fun getImageFile(name: String?, context: Context): File?{
    if (name.isNullOrEmpty()) return null
    val  file = File(context.cacheDir, name)
    if (file.isImage()) {
        return file
    }
    return null
}

fun getMp3File(name: String?, context: Context): File?{
    if (name.isNullOrEmpty()) return null
    val  file = File(context.cacheDir, name)
    if (file.isMp3File()) {
        return file
    }
    return null
}



fun isJpegSignature(bytes: ByteArray): Boolean {
    if (bytes.size < 4) return false
    val id2FirstSignature = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
    val id2LastSignature = byteArrayOf(0xFF.toByte(), 0xD9.toByte())
    return bytes.copyOfRange(0, 2).contentEquals(id2FirstSignature) && bytes.copyOfRange(bytes.size - 2, bytes.size).contentEquals(id2LastSignature)
}
fun isPngSignature(bytes: ByteArray): Boolean {
    // begin 89 50 4e 47
    // end 49 45 4e 44 ae 42 60 82
    if (bytes.size < 12) return false
    val idFirstSignature = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4e.toByte(),0x47.toByte())
    val idLastSignature = byteArrayOf(0x49.toByte(), 0x45.toByte(), 0x4e.toByte(),0x44.toByte(),0xae.toByte(), 0x42.toByte(), 0x60.toByte(), 0x82.toByte())

    return bytes.copyOfRange(0, 4).contentEquals(idFirstSignature) &&
            bytes.copyOfRange(bytes.size - 8, bytes.size).contentEquals(idLastSignature)
}

fun isMp3Signature(bytes: ByteArray): Boolean {
    val id3Signature = byteArrayOf(0x49, 0x44, 0x33)
    return bytes.size >= 3 && bytes.copyOfRange(0, 3).contentEquals(id3Signature)
}

