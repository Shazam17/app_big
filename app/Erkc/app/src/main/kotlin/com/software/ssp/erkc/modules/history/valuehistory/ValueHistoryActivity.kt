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
import kotlinx.android.synthetic.main.activity_value_history.*
import kotlinx.android.synthetic.main.item_value_history_ipu.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryActivity : BaseListActivity<RealmIpuValue>(), IValueHistoryView {

    @Inject lateinit var presenter: IValueHistoryPresenter

    private var dateFrom: Date? by extras(Constants.KEY_DATE_FROM)
    private var dateTo: Date? by extras(Constants.KEY_DATE_TO)
    private var receiptId: String by extras(Constants.KEY_RECEIPT)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_history)
        initViews()
        presenter.onViewAttached(dateFrom, dateTo, receiptId)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValueHistoryAdapter(dataset)
    }

    override fun fillData(name: String, total: String, average: String, unit: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_value_history_ipu, null, false)
        view.valueHistoryIpuNameTextView.text = name
        view.valueHistoryIpuAverageTextView.text = getString(unit).format(average)
        view.valueHistoryIpuTotalTextView.text = getString(unit).format(total)
        valueHistoryIpuContainer.addView(view)
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

    override fun initViews() {
        super.initViews()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        val dateFormat = SimpleDateFormat(Constants.VALUES_DATE_FORMAT, Locale("RU"))
        valueHistoryTotalTextView.text = getString(R.string.history_value_total).format(dateFormat.format(dateFrom), dateFormat.format(dateTo))
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

}