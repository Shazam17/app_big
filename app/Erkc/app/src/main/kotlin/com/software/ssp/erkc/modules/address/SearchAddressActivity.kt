package com.software.ssp.erkc.modules.address


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.db.StreetCache
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_search_address.*
import org.jetbrains.anko.onClick
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
    }

    override fun navigateToDrawer(street: StreetCache) {
        val intent = Intent()
        intent.putExtra(SEARCH_ADDRESS_RESULT_KEY, street.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun navigateToDrawer() {
        finish()
    }

    override fun showData(streets: List<StreetCache>) {
        4
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
        searchAddressQuery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.onQuery(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
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
