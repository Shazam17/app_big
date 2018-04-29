package com.software.ssp.erkc.extensions

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.view.View
import android.widget.EditText
import org.jetbrains.anko.selector
import org.jetbrains.anko.textChangedListener
import java.util.*

private fun EditText.focusSafeOp(op: ()->Unit) {
    val listener = this.onFocusChangeListener
    this.onFocusChangeListener = null
    op()
    this.onFocusChangeListener = listener
}


fun EditText.beFiltered(activity: Activity, options: List<String>, null_focused: View? = null, other_allowed: Boolean = false, default_idx: Int = -1) {
    this.setOnFocusChangeListener({view, focused ->

        if (focused) {
            activity.selector("", options, { pos ->
                val text = options.get(pos)
                this.setText(text)
                if (!other_allowed) {
                    activity.hideKeyboard()
                    focusSafeOp { if (null_focused != null) null_focused.requestFocus() else this.clearFocus() }
                } else {
                    focusSafeOp { this.requestFocus() }

                    this.setSelection(text.length)
                    if (pos == options.size-1) this.selectAll()
                }
            })
        }
    })
    if (!other_allowed) this.inputType = InputType.TYPE_NULL
    if (default_idx >= 0) this.setText(options.get(default_idx))
}

fun EditText.beDatePicker(activity: Activity, null_focused: View? = null) {
    this.setOnFocusChangeListener({v, focused ->
        if (focused) {
            this.pickDate(activity)
            focusSafeOp { if (null_focused != null) null_focused.requestFocus() else this.clearFocus() }
            activity.hideKeyboard()
        }
    })
}

private fun EditText.pickDate(context: Context) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(context,
            {picker,year,month,day->
                this.setText(String.format("%02d",day) + '.' + String.format("%02d",month) + '.' + year)
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}

fun TextInputEditText.setTilError(s: String?) {
    val parent2 = this.parent.parent
    if (parent2 is TextInputLayout) {
        parent2.error = s
    }
}

fun EditText.bind(op: (s: String) -> Unit) {
    this.textChangedListener { afterTextChanged { e -> op(e.toString()) } }
}