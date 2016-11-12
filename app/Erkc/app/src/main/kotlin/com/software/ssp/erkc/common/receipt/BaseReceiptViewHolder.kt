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

            if (receipt.payment > 0) {
                receiptAmountText.text = "-" + receipt.payment.toString()
                receiptAmountText.textColor = context.getCompatColor(R.color.colorRed)
            } else {
                receiptAmountText.text = receipt.payment.toString()
                receiptAmountText.textColor = context.getCompatColor(R.color.colorLightInput)
            }

            receiptLastPayDateText.text = receipt.lastPayment
            receiptLastTransferDateText.text = receipt.lastValueTransfer

            when (receipt.autoPayMode) {
                0 -> receiptAutoPaymentText.visibility = View.GONE
                2 -> receiptAutoPaymentText.visibility = View.VISIBLE
            }

            deleteProgressBar.visibility = View.GONE
            receiptPayButton.enabled = true
            receiptTransferButton.enabled = true
            receiptMenuImage.isEnabled = true
            deleteButton.isEnabled = true

            swipeLayout.reset()
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        itemView.receiptAddressText.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
