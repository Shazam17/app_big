package com.software.ssp.erkc.modules.history.valuehistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.IpuType
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.*
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import kotlinx.android.synthetic.main.activity_value_history.*
import kotlinx.android.synthetic.main.item_value_expandable_history.view.*
import net.cachapa.expandablelayout.ExpandableLayout
import javax.inject.Inject
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Environment
import android.support.v4.content.FileProvider
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.webkit.MimeTypeMap
import com.software.ssp.erkc.utils.ExcelUtils
import org.jetbrains.anko.*
import java.io.File


/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryActivity : MvpActivity(), IValueHistoryView {

    @Inject lateinit var presenter: IValueHistoryPresenter

    private var receiptId: String by extras(Constants.KEY_RECEIPT)
    private var historyFilter: HistoryFilterModel by extras(Constants.KEY_HISTORY_FILTER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_history)

        initViews()

        presenter.currentFilter = historyFilter
        presenter.receiptId = receiptId

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerValueHistoryComponent.builder()
                .appComponent(appComponent)
                .valueHistoryModule(ValueHistoryModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.value_history_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.menu_share -> {
                presenter.shareAction()
                return true
            }

            R.id.menu_info -> {
                showInfoDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun setProgressVisible(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showReceiptData(receipt: RealmReceipt) {
        barcodeTextView.text = getString(R.string.history_value_barcode_format, receipt.barcode, receipt.name)
        addressTextView.text = receipt.address
    }

    override fun addServiceData(name: String, ipuType: IpuType, total: Long, average: Double) {

        val unitResId = ipuType.getUnitResId()
        val picRecId = ipuType.getIconResId()

        averageValueContainer.include<ViewGroup>(R.layout.item_value_history_ipu).apply {
            find<TextView>(R.id.ipuNameTextView).apply {
                text = name
                setCompoundDrawablesWithIntrinsicBounds(picRecId, 0, 0, 0)
            }

            find<TextView>(R.id.ipuValueTextView).apply {
                text = if (average > 0) getString(unitResId, average.toStringWithDot()) else getString(R.string.history_value_not_available)
            }
        }

        totalValueContainer.include<ViewGroup>(R.layout.item_value_history_ipu).apply {
            find<TextView>(R.id.ipuNameTextView).apply {
                text = name
                setCompoundDrawablesWithIntrinsicBounds(picRecId, 0, 0, 0)
            }

            find<TextView>(R.id.ipuValueTextView).apply {
                text = getString(unitResId, total)
            }
        }
    }

    private fun spanIPUTitle(byuser: Boolean, s: String): SpannableString {
        if (!byuser) return SpannableString(s);
        else {
            val span = SpannableString(s+"_")
            span.setSpan(ImageSpan(this, R.drawable.ic_edit_black), s.length, s.length+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            return span
        }
    }

    override fun addIpuData(ipu: ValueHistoryViewModel) {
        valuesContainer.include<ViewGroup>(R.layout.item_value_expandable_history) {

            val expandableView = find<ExpandableLayout>(R.id.itemValueExpandableLayout)
            val anyIpu = ipu.values.first()

            find<TextView>(R.id.itemValueHistoryTitle).apply {
                text = spanIPUTitle(anyIpu.userRegistered, getString(R.string.history_value_item_title, anyIpu.shortName, anyIpu.number, anyIpu.installPlace))
                setCompoundDrawablesWithIntrinsicBounds(0, 0, if (expandableView.isExpanded) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down, 0)

                onClick {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, if (!expandableView.isExpanded) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down, 0)
                    expandableView.toggle()
                }
            }

            ipu.values.forEach {
                itemsContainer.include<ViewGroup>(R.layout.item_value_history) {
                    find<TextView>(R.id.itemValueHistoryDate).text = it.date?.toString(Constants.VALUES_DATE_FORMAT)
                    find<TextView>(R.id.itemValueHistoryValue).text = it.value
                }
            }

            val unitResId = anyIpu.ipuType.getUnitResId()

            find<TextView>(R.id.totalByIpuTextView).text = getString(unitResId, ipu.total)
            find<TextView>(R.id.averageByIpuTextView).text = if (ipu.average > 0) getString(unitResId, ipu.average.toStringWithDot()) else getString(R.string.history_value_not_available)
        }
    }

    override fun showPeriod(dateFrom: String, dateTo: String) {
        periodTextView.text = getString(R.string.history_value_period_format).format(dateFrom, dateTo)
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }

    private fun showInfoDialog() {
        materialDialog {
            content(R.string.history_value_info_dialog_content)
            positiveText(R.string.history_value_info_dialog_positive)
        }.show()
    }

    override fun shareIntent(data: ValueHistoryPresenter.ShareData, filename: String, subject: String) {
            val string_data = data.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.history_share_clipboard_label), string_data)
            clipboard.setPrimaryClip(clip)
            toast(R.string.history_share_copied_to_clipboard)

            val file = File(filesDir.path + filename)
            File(filesDir, "xml").mkdirs()
            file.createNewFile()


            if (!ExcelUtils.writeToFile(file, data)) {
                longToast(R.string.xls_write_error)
                setProgressVisible(false)
                return
            }

            Log.d("hist_", string_data)
            val uri = FileProvider.getUriForFile(this, "com.software.ssp.erkc", file)
            val intent = Intent(Intent.ACTION_SEND)
//                .putExtra(Intent.EXTRA_TEXT, string_data)
//                .setType("text/plain")
                    //.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(filename)))
                    //.setType("application/vnd.ms-excel")
                    .setType("application/*")
                    //.setType("*/*")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(Intent.createChooser(intent, getString(R.string.history_share)))
    }

    override fun shareDataNotReady() {
        toast(getString(R.string.history_share_data_not_ready))
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }
}