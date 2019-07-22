package com.software.ssp.erkc.modules.createrequest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RealmTypeHouse
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.createrequest.adapters.AddressRequestAdapter
import com.software.ssp.erkc.modules.createrequest.adapters.TypeHouseAdapter
import kotlinx.android.synthetic.main.activity_create_request.*
import org.jetbrains.anko.*
import javax.inject.Inject

class CreateRequestActivity: MvpActivity(), ICreateRequestView {

    companion object {
        const val CREATE_REQUEST_EDIT_MODE = "create_request_edit_mode"
        const val REQUEST_ID = "request_id"
    }

    @Inject lateinit var presenter: ICreateRequestPresenter
    var fias: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)
        initViews()
        checkParams()
    }

    private fun checkParams() {
        if (intent.getBooleanExtra(CREATE_REQUEST_EDIT_MODE, false)) {
            presenter.requestId = intent.getIntExtra(REQUEST_ID, -1)
            configureBottomFrame(isEditMode = true)
            presenter.onViewLoadWithEditMode()
        } else {
            presenter.onViewAttached()
            configureBottomFrame(isEditMode = false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    override fun setFieldByRealmRequest(realmRequest: RealmRequest) {
        createRequestNameRequestTextEdit.setText(realmRequest.name)
        createRequestServiceProviderTextEdit.setText(realmRequest.company?.name)
        crashCheckBox.isChecked = realmRequest.isCrash ?: false
        createRequestDescriptionTextEdit.setText(realmRequest.message)
        createRequestFIOTextEdit.setText(realmRequest.applicant)
        createRequestNumberPhoneTextEdit.setText(realmRequest.contact)
    }


    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerCreateRequestComponent.builder()
                .appComponent(appComponent)
                .createRequestModule(CreateRequestModule(this))
                .build()
                .inject(this)
    }

    private fun initViews() {
        // TODO init views in activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addressInputLayout.isHintAnimationEnabled = false

        createRequestAddressTextEdit.onTouch { view, motionEvent ->
            when {
                motionEvent.actionMasked == MotionEvent.ACTION_UP -> {
                    rootLayoutCreateRequest.requestFocus()
                    presenter.onAddressFieldClick()
                    true
                }
                else -> true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            SearchAddressActivity.REQUEST_ADDRESS_RESULT_CODE -> {
                createRequestAddressTextEdit.setText(data?.getStringExtra(SearchAddressActivity.RESULT_ADDRESS_KEY))
                fias = data?.getStringExtra(SearchAddressActivity.RESULT_FIAS_KEY)!!
                Log.e("FIAS", fias)
            }
        }
    }

    private fun configureBottomFrame(isEditMode: Boolean) {
        if (isEditMode) {
            createRequestSaveButton.visibility = View.VISIBLE
            createRequestSendButton.visibility = View.GONE
        } else {
            createRequestSaveButton.visibility = View.GONE
            createRequestSendButton.visibility = View.VISIBLE
        }
    }

    override fun setTypeHouseSpinner(typeHouseList: List<RealmTypeHouse>) {
        val adapter = TypeHouseAdapter(this, android.R.layout.simple_spinner_item, typeHouseList = typeHouseList)
        typeHouseSpinner.adapter = adapter
    }

    override fun setTextAddress(addressText: String) {
        createRequestAddressTextEdit.setText(addressText)
    }

    override fun navigateToSearchAddress() {
        startActivityForResult<SearchAddressActivity>(SearchAddressActivity.REQUEST_ADDRESS_RESULT_CODE, SearchAddressActivity.SEARCH_ADDRESS_REQUEST_FLAG to true)
    }

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }
}