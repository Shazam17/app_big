package com.software.ssp.erkc.common.receipt

import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.extensions.getCompatColor
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor

abstract class BaseReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bindReceipt(receipt: Receipt) {

        with(itemView) {

            receiptName.text = receipt.serviceName
            receiptBarcodeText.text = receipt.barcode

            receiptAddressText.text = receipt.address

            //TODO Replace with icon
            //receiptTypeImage.image = ColorDrawable(context.getCompatColor(receipt.type.getResId()))

            receiptAmountText.text = receipt.amount
            receiptAmountText.textColor = context.getCompatColor(if (receipt.amount.startsWith("-")) R.color.colorRed else R.color.colorLightInput)

            receiptLastPayDateText.text = receipt.lastPayment
            receiptLastTransferDateText.text = receipt.lastValueTransfer

            //TODO Autopay indicator
            //receiptAutoPayImageView.visibility = if(receipt.isCardLinked) View.VISIBLE else View.GONE
            //receiptAutoPayImageView.setColorFilter(context.getCompatColor(if(receipt.isAutoPayment) R.color.colorPrimary else R.color.colorInactive))

            deleteProgressBar.visibility = View.GONE
            receiptPayButton.enabled = true
            receiptTransferButton.enabled = true
            receiptPaymentHistoryImageButton.isEnabled = true
            deleteButton.isEnabled = true

            swipeLayout.reset()
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        itemView.receiptAddressText.visibility = if(isVisible) View.VISIBLE else View.GONE
    }
}
