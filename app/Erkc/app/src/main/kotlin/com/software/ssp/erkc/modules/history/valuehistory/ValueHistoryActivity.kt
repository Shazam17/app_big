package com.software.ssp.erkc.modules.history.valuehistory

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.BaseListActivity
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_value_history.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryActivity : BaseListActivity<RealmIpu>(), IValueHistoryView {

    @Inject lateinit var presenter: IValueHistoryPresenter

    private var dateFrom: Date? by extras(Constants.KEY_DATE_FROM)
    private var dateTo: Date? by extras(Constants.KEY_DATE_TO)
    private var receipt: RealmReceipt by extras(Constants.KEY_RECEIPT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_history)
        initViews()
        presenter.onViewAttached(dateFrom, dateTo, receipt)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValueHistoryAdapter(dataset)
    }

    override fun fillData(total: Int, average: Int) {
        val dateFormat = SimpleDateFormat(Constants.VALUES_DATE_FORMAT, Locale("RU"))
        valueHistoryTotalTextView.text = getString(R.string.history_value_total).format(dateFormat.format(dateFrom), dateFormat.format(dateTo))
    }

    override fun resolveDependencies(appComponent: AppComponent) {

    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun initViews() {
        super.initViews()

    }

}