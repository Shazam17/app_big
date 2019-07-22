package com.software.ssp.erkc.modules.address


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import com.software.ssp.erkc.data.realm.models.RealmStreet
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_search_address.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * @author Alexander Popov on 25/10/2016.
 */
class SearchAddressActivity : MvpActivity(), ISearchAddressView {
    @Inject lateinit var presenter: ISearchAddressPresenter

    companion object {
        val SEARCH_ADDRESS_REQUEST_CODE = 12501
        val SEARCH_ADDRESS_RESULT_KEY = "SEARCH_ADDRESS_RESULT_KEY"
        const val RESULT_FIAS_KEY = "RESULT_FIAS_KEY"
        const val RESULT_ADDRESS_KEY = "RESULT_ADDRESS_KEY"
        const val REQUEST_ADDRESS_RESULT_CODE = 12502
        const val SEARCH_ADDRESS_REQUEST_FLAG = "search_address_request_flag"
    }

    private var adapter: SearchAddressListAdapter? = null
    private var requestAddressAdapter: SearchRequestAddressListAdapter? = null
    private val streetsList: MutableList<RealmStreet> = ArrayList()
    private val requestAddressList: MutableList<RealmAddressRequest> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_address)
        checkSendParams()
//        presenter.onViewAttached()
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

    private fun checkSendParams() {
            if (intent.getBooleanExtra(SEARCH_ADDRESS_REQUEST_FLAG, false)) {
                initViews(isForRequestScreen = true)
                presenter.onViewAttachedRequestScreen()
            } else {
                initViews(isForRequestScreen = false)
                presenter.onViewAttached()
            }
    }

    override fun showData(streets: List<RealmStreet>) {
        streetsList.clear()
        streetsList.addAll(streets)
        adapter?.notifyDataSetChanged()
    }

    override fun showRequestAddressData(addressList: List<RealmAddressRequest>) {
        requestAddressList.clear()
        requestAddressList.addAll(addressList)
        requestAddressAdapter?.notifyDataSetChanged()
    }

    override fun setResult(street: RealmStreet) {
        val intent = Intent()
        intent.putExtra(SEARCH_ADDRESS_RESULT_KEY, street.name)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun setRequestAddressResult(address: RealmAddressRequest) {
        val intent = Intent()
        intent.putExtra(RESULT_ADDRESS_KEY, address.address)
        intent.putExtra(RESULT_FIAS_KEY, address.fias)
        setResult(Activity.RESULT_OK, intent)
    }

    override fun close() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    private fun initViews(isForRequestScreen: Boolean) {
        searchAddressQuery.textChangedListener {
            afterTextChanged { text ->
                if (isForRequestScreen) {
                    presenter.onQueryRequestAddress(text.toString())
                } else {
                    presenter.onQueryRequestAddress(text.toString())
                }
            }
        }

        searchAddressBack.onClick {
            presenter.onBackClick()
        }

        if (isForRequestScreen) {
            requestAddressAdapter = SearchRequestAddressListAdapter(
                    dataSet = requestAddressList,
                    onClick = {address ->
                        presenter.onItemRequestAddressSelected(addressRequest = address)
                    }
            )
        } else {
            adapter = SearchAddressListAdapter(
                    streetsList
            ) { address ->
                presenter.onItemSelected(address)

            }
        }

        if (requestAddressAdapter == null) {
            recyclerView.adapter = adapter
        } else {
            recyclerView.adapter = requestAddressAdapter
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
