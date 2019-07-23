package com.software.ssp.erkc.utils

import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.getExternalStoragePublicDirectory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class FileUtils {

    companion object {
        val shareInstance = FileUtils()
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        //This is the directory in which the file will be created. This is the default location of Camera photos
        val storageDir = File(getExternalStoragePublicDirectory(DIRECTORY_DCIM), "Camera")
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        // Save a file: path for using again
        return image
    }
}