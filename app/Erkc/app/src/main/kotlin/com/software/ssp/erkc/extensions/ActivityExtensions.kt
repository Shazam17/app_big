package com.software.ssp.erkc.extensions

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog

inline fun Activity.materialDialog(setupBlock: (MaterialDialog.Builder.() -> (MaterialDialog.Builder))): MaterialDialog.Builder {
    return setupBlock.invoke(MaterialDialog.Builder(this))
}
