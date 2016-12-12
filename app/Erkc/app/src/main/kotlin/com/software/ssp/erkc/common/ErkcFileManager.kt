package com.software.ssp.erkc.common

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import com.software.ssp.erkc.BuildConfig
import com.software.ssp.erkc.R
import com.tbruyelle.rxpermissions.RxPermissions
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


/**
 * @author Alexander Popov on 06/12/2016.
 */

class ErkcFileManager @Inject constructor(private val context: Context) {

    fun saveFile(uri: Uri) {
        RxPermissions.getInstance(context)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({ granted ->
                    if (granted) {
                        try {
                            val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), uri.lastPathSegment)
                            val outputStream = FileOutputStream(outputFile)
                            outputStream.write(context.contentResolver.openInputStream(uri).readBytes())
                            outputStream.close()
                            context.toast(context.getString(R.string.payment_check_success_save))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        context.toast(context.getString(R.string.payment_check_permission_denied))
                    }
                })

    }

    fun createTempFile(byteArray: ByteArray, fileName: String): Uri {
        val path = File(context.filesDir, "export")
        val pdf = File(path.path + File.separator + fileName)
        pdf.parentFile.mkdirs()

        if (!pdf.exists())
            pdf.createNewFile()

        val outputStream = FileOutputStream(pdf)
        outputStream.write(byteArray)
        outputStream.close()

        return FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID,
                pdf)
    }

}