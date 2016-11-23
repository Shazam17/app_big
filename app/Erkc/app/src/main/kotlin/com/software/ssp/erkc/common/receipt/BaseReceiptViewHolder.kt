package com.software.ssp.erkc.common.receipt

import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.extensions.dp
import com.software.ssp.erkc.extensions.getCompatColor
import kotlinx.android.synthetic.main.item_receipt.view.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor

abstract class BaseReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bindReceipt(receiptViewModel: ReceiptViewModel) {
        itemView.apply {
            //section header
            receiptAddressText.text = receiptViewModel.receipt.address

            receiptName.text = receiptViewModel.receipt.name
            receiptBarcodeText.text = receiptViewModel.receipt.barcode

            receiptTypeImage.setImageResource(receiptViewModel.receipt.receiptType.getIconResId())

            val paymentText: String

            if (receiptViewModel.receipt.amount > 0) {
                paymentText = "-" + receiptViewModel.receipt.amount.toString()
                receiptAmountText.textColor = context.getCompatColor(R.color.colorRed)
            } else {
                paymentText = receiptViewModel.receipt.amount.toString()
                receiptAmountText.textColor = context.getCompatColor(R.color.colorLightInput)
            }

            receiptAmountText.text = String.format("%s %s", paymentText, context.getString(R.string.receipts_currency))

            receiptLastPayDateText.text = if (receiptViewModel.receipt.lastPayment.isNullOrBlank()) "-" else receiptViewModel.receipt.lastPayment
            receiptLastTransferDateText.text = if (receiptViewModel.receipt.lastValueTransfer.isNullOrBlank()) "-" else receiptViewModel.receipt.lastValueTransfer

            when (receiptViewModel.receipt.autoPayMode) {
                0 -> receiptAutoPaymentText.visibility = View.GONE
                2 -> receiptAutoPaymentText.visibility = View.VISIBLE
            }

            deleteProgressBar.visibility = if (receiptViewModel.isRemovePending) View.VISIBLE else View.GONE
            receiptPayButton.enabled = !receiptViewModel.isRemovePending
            receiptTransferButton.enabled = !receiptViewModel.isRemovePending
            receiptMenuImage.isEnabled = !receiptViewModel.isRemovePending
            deleteButton.isEnabled = !receiptViewModel.isRemovePending

            swipeLayout.reset()

            if (receiptViewModel.isRemovePending) {
                swipeLayout.offset = -100.dp
            }
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        itemView.receiptAddressText.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
