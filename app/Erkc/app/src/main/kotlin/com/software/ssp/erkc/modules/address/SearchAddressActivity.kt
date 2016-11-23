package com.software.ssp.erkc.modules.address


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmStreet
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_search_address.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class SearchAddressActivity : MvpActivity(), ISearchAddressView {
    @Inject lateinit var presenter: ISearchAddressPresenter

    companion object {
        val SEARCH_ADDRESS_REQUEST_CODE = 12501
        val SEARCH_ADDRESS_RESULT_KEY = "SEARCH_ADDRESS_RESULT_KEY"
    }

    var mSearchAdapter: SearchAddressListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_address)
        initViews()
        presenter.onViewAttached()
    }

    override fun setResult(street: RealmStreet) {
        val intent = Intent()
        intent.putExtra(SEARCH_ADDRESS_RESULT_KEY, street.name)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun close() {
        finish()
    }

    override fun showData(streets: List<RealmStreet>) {
        mSearchAdapter?.swapData(streets)
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSearchAddressComponent.builder()
                .appComponent(appComponent)
                .searchAddressModule(SearchAddressModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    fun initViews() {
        searchAddressQuery.textChangedListener {
            afterTextChanged { text -> presenter.onQuery(text.toString()) }
        }

        searchAddressBack.onClick {
            presenter.onBackClick()
        }

        mSearchAdapter = SearchAddressListAdapter({
            address ->
            presenter.onItemSelected(address)

        })

        recyclerView.adapter = mSearchAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
