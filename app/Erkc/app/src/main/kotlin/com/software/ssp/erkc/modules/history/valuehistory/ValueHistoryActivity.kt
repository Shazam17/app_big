package com.software.ssp.erkc.modules.history.valuehistory

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.BaseListActivity
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import kotlinx.android.synthetic.main.activity_value_history.*
import kotlinx.android.synthetic.main.item_value_history_ipu.view.*
import javax.inject.Inject


/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryActivity : BaseListActivity<RealmIpuValue>(), IValueHistoryView {

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

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValueHistoryAdapter(dataset)
    }

    override fun fillData(name: String, total: String, average: String, unit: Int, drawable: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_value_history_ipu, null, false)
        view.apply {
            valueHistoryIpuNameTextView.text = name
            valueHistoryIpuNameTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
            valueHistoryIpuAverageTextView.text = getString(unit).format(average)
            valueHistoryIpuTotalTextView.text = getString(unit).format(total)
        }
        valueHistoryIpuContainer.addView(view)
    }

    override fun showPeriod(dateFrom: String, dateTo: String) {
        valueHistoryTotalTextView.text = getString(R.string.history_value_total).format(dateFrom, dateTo)
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun initViews() {
        super.initViews()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        swipeToRefreshEnabled = false
    }

    private fun showInfoDialog(){
        materialDialog {
            content(R.string.history_value_info_dialog_content)
            positiveText(R.string.history_value_info_dialog_positive)
        }
    }
}