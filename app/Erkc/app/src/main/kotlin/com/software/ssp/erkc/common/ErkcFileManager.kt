package com.software.ssp.erkc.common

import android.content.Context
import java.io.File
import javax.inject.Inject

/**
 * @author Alexander Popov on 06/12/2016.
 */

class ErkcFileManager @Inject constructor(private val context: Context) {

    fun saveFile(fileName: String, file: File) {
        try {
            val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream.write(file.readBytes())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}