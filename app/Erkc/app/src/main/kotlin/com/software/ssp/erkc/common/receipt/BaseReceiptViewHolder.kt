package com.software.ssp.erkc.common.receipt

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.extensions.getCompatColor
import com.software.ssp.erkc.extensions.getResId
import kotlinx.android.synthetic.main.item_receipt.view.*
import kotlinx.android.synthetic.main.item_receipt_header.view.*
import org.jetbrains.anko.image
import org.jetbrains.anko.textColor

abstract class BaseReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bindReceipt(receipt: ReceiptViewModel) {

        with(itemView) {

            receiptName.text = receipt.name
            receiptBarcodeText.text = receipt.barcode

            //TODO Replace with icon
            receiptTypeImage.image = ColorDrawable(context.getCompatColor(receipt.type.getResId()))

            receiptAmountText.text = receipt.amount
            receiptAmountText.textColor = context.getCompatColor(if (receipt.amount.startsWith("-")) R.color.colorRed else R.color.colorLightInput)

            receiptLastPayDateText.text = receipt.lastPayDate
            receiptLastTransferDateText.text = receipt.lastValueTransferDate

            receiptAutoPayImageView.visibility = if(receipt.isCardLinked) View.VISIBLE else View.GONE
            receiptAutoPayImageView.setColorFilter(context.getCompatColor(if(receipt.isAutoPayment) R.color.colorPrimary else R.color.colorInactive))

            swipeLayout.reset()
        }
    }

    fun bindHeader(title: String) {
        itemView.receiptAddressHeaderText.text = title
    }
}
