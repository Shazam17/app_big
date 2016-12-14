package com.software.ssp.erkc.common.receipt

import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.AutoPaymentMode
import com.software.ssp.erkc.extensions.dp
import com.software.ssp.erkc.extensions.getCompatColor
import com.software.ssp.erkc.extensions.getIconResId
import com.software.ssp.erkc.extensions.toString
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

            when {
                receiptViewModel.receipt.amount > 0 -> {
                    paymentText = "-%.2f".format(receiptViewModel.receipt.amount)
                    receiptAmountText.textColor = context.getCompatColor(R.color.colorRed)
                }

                receiptViewModel.receipt.amount < 0 -> {
                    paymentText = "%.2f".format(receiptViewModel.receipt.amount).replace("-", "+")
                    receiptAmountText.textColor = context.getCompatColor(R.color.colorGreen)
                }

                else -> {
                    paymentText = "%.2f".format(receiptViewModel.receipt.amount)
                    receiptAmountText.textColor = context.getCompatColor(R.color.colorLightInput)
                }
            }

            receiptAmountText.text = String.format("%s %s", paymentText, context.getString(R.string.receipts_currency))

            receiptLastPayDateText.text = receiptViewModel.receipt.lastPaymentDate?.toString(Constants.RECEIPT_DATE_FORMAT) ?: context.getString(R.string.receipts_date_never)
            receiptLastTransferDateText.text = receiptViewModel.receipt.lastIpuTransferDate?.toString(Constants.RECEIPT_DATE_FORMAT) ?: context.getString(R.string.receipts_date_never)

            when (receiptViewModel.receipt.autoPayMode) {
                AutoPaymentMode.AUTO.ordinal -> receiptAutoPaymentText.visibility = View.VISIBLE
                else -> receiptAutoPaymentText.visibility = View.GONE
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
