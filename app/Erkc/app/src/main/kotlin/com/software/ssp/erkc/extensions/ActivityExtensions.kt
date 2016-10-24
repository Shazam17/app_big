package com.software.ssp.erkc.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.afollestad.materialdialogs.MaterialDialog

inline fun Activity.materialDialog(setupBlock: (MaterialDialog.Builder.() -> (MaterialDialog.Builder))): MaterialDialog.Builder {
    return setupBlock.invoke(MaterialDialog.Builder(this))
}

fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = this.currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
