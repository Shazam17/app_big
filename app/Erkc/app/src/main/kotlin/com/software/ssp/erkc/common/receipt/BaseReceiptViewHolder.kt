package com.software.ssp.erkc.common.receipt

import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.extensions.getCompatColor
import com.software.ssp.erkc.extensions.getIconResId
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor

abstract class BaseReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bindReceipt(receipt: Receipt) {

        with(itemView) {

            //section header
            receiptAddressText.text = receipt.address

            receiptName.text = receipt.name
            receiptBarcodeText.text = receipt.barcode

            receiptTypeImage.setImageResource(receipt.receiptType.getIconResId())

            receiptAmountText.text = receipt.payment.toString()
            receiptAmountText.textColor = context.getCompatColor(if (receipt.payment > 0) R.color.colorRed else R.color.colorLightInput)

            receiptLastPayDateText.text = receipt.lastPayment
            receiptLastTransferDateText.text = receipt.lastValueTransfer

            receiptAutoPayImageView.visibility = if (receipt.linkedCardId == null) View.GONE else View.VISIBLE

            when (receipt.autoPayMode) {
                0 -> receiptAutoPayImageView.setColorFilter(context.getCompatColor(R.color.colorInactive))
                2 -> receiptAutoPayImageView.setColorFilter(context.getCompatColor(R.color.colorBlueButton))
            }

            deleteProgressBar.visibility = View.GONE
            receiptPayButton.enabled = true
            receiptTransferButton.enabled = true
            receiptPaymentHistoryImageButton.isEnabled = true
            deleteButton.isEnabled = true

            swipeLayout.reset()
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        itemView.receiptAddressText.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
