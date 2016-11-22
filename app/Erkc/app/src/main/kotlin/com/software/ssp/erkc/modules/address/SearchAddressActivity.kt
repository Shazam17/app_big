package com.software.ssp.erkc.modules.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.db.AddressCache
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_search_address.*
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

    override fun navigateToDrawer(address: AddressCache) {
        val intent = Intent()
        //intent.putExtra(Constants.KEY_ADDRESS_FIND_RESULT, address.id)
        intent.putExtra(SEARCH_ADDRESS_RESULT_KEY, address.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showData(addresses: List<AddressCache>) {
        mSearchAdapter?.swapData(addresses)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_addresses_menu, menu)

        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        return true
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun initViews() {
        setSupportActionBar(toolbar)
        initSearchView()

        mSearchAdapter = SearchAddressListAdapter({
            address ->
            presenter.onItemSelected(address)

        })
        recyclerView.adapter = mSearchAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initSearchView() {

        searchView.setVoiceSearch(false)
        searchView.setEllipsize(true)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                presenter.onQuery(p0)
                return false
            }
        })

    }


}
