package com.learn.worlds.data

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.jvm.javaio.toOutputStream
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

private fun writeResponseBodyToDisk(inputStream: InputStream, file: File): Boolean {
    return  try {
        inputStream.use {
            FileOutputStream(file).use { outputStream->
                val byteArray  = ByteArray(2024)
                var fileSizeDownloaded: Long = 0
                while (true) {
                    val read = inputStream.read(byteArray)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(byteArray, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
            }
        }
        true
    } catch (t: Throwable) {
        Timber.e(t)
        false
    }
}

/*
Example for file downloading
val result = client.post<HttpResponse>("url") {
    body = StreamContent(pdfFile)
}
* */

private class StreamContent(private val file: File, override val contentType: ContentType) :
    OutgoingContent.WriteChannelContent() {
    override suspend fun writeTo(channel: ByteWriteChannel) {
        file.inputStream().copyTo(channel.toOutputStream(), 1024)
    }

    override val contentLength: Long = file.length()


}