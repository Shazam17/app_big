package com.software.ssp.erkc.modules.history.valuehistory

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import kotlinx.android.synthetic.main.activity_value_history.*
import kotlinx.android.synthetic.main.item_value_expandable_history.view.*
import net.cachapa.expandablelayout.ExpandableLayout
import org.jetbrains.anko.find
import org.jetbrains.anko.include
import org.jetbrains.anko.onClick
import javax.inject.Inject

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

            R.id.menu_info -> {
                showInfoDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun showReceiptData(receipt: RealmReceipt) {
        barcodeTextView.text = getString(R.string.history_value_barcode_format, receipt.barcode, receipt.name)
        addressTextView.text = receipt.address
    }

    override fun addServiceData(name: String, total: String, average: String) {

        val unitResId: Int
        val picRecId: Int

        when {
            name.contains(Constants.HOT_WATER) -> {
                unitResId = R.string.history_value_water_unit
                picRecId = R.drawable.pic_hot_water
            }
            name.contains(Constants.COLD_WATER) -> {
                unitResId = R.string.history_value_water_unit
                picRecId = R.drawable.pic_cold_water
            }
            else -> {
                unitResId = R.string.history_value_electro_unit
                picRecId = R.drawable.pic_electro
            }
        }

        averageValueContainer.include<ViewGroup>(R.layout.item_value_history_ipu).apply {
            find<TextView>(R.id.ipuNameTextView).apply {
                text = name
                setCompoundDrawablesWithIntrinsicBounds(picRecId, 0, 0, 0)
            }

            find<TextView>(R.id.ipuValueTextView).apply {
                text = getString(unitResId, average)
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

    override fun addIpuData(ipu: ValueHistoryViewModel) {
        valuesContainer.include<ViewGroup>(R.layout.item_value_expandable_history) {

            val expandableView = find<ExpandableLayout>(R.id.itemValueExpandableLayout)
            val anyIpu = ipu.values.first()

            find<TextView>(R.id.itemValueHistoryTitle).apply {
                text = getString(R.string.history_value_item_title, anyIpu.serviceName, anyIpu.number, anyIpu.installPlace)
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

            val unitResId = when {
                anyIpu.serviceName.contains(Constants.HOT_WATER) -> {
                    R.string.history_value_water_unit
                }
                anyIpu.serviceName.contains(Constants.COLD_WATER) -> {
                    R.string.history_value_water_unit
                }
                else -> {
                    R.string.history_value_electro_unit
                }
            }

            find<TextView>(R.id.totalByIpuTextView).text = getString(unitResId, ipu.total)
            find<TextView>(R.id.averageByIpuTextView).text = getString(unitResId, ipu.average)
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
}