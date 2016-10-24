package com.software.ssp.erkc.extensions

import android.widget.EditText
import org.jetbrains.anko.textChangedListener


fun EditText.onTextChange(listener: (CharSequence?)->Unit){
    textChangedListener { onTextChanged { charSequence, start, count, after ->  listener(charSequence) } }
}