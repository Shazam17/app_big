package com.software.ssp.erkc.extensions

import android.content.Context
import com.software.ssp.erkc.data.rest.models.ServiceType
import java.io.File


fun ServiceType.iconPath(context: Context) =
        context.filesDir.absolutePath +
        File.pathSeparator +
        "service_icons" +
        File.pathSeparator +
        this.service_code

fun ServiceType.folderPath(context: Context) =
        context.filesDir.absolutePath +
        File.pathSeparator +
        "service_icons"
