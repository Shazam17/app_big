package com.software.ssp.erkc.modules.createrequest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.createrequest.adapters.CompaniesAdapter
import com.software.ssp.erkc.modules.createrequest.adapters.TypeHouseAdapter
import com.software.ssp.erkc.modules.request.authedRequest.draftRequestList.DraftRequestListFragment
import kotlinx.android.synthetic.main.activity_create_request.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject

class CreateRequestActivity : MvpActivity(), ICreateRequestView {


    companion object {
        const val CREATE_REQUEST_EDIT_MODE = "create_request_edit_mode"
        const val REQUEST_ID = "request_id"
        const val CREATE_REQUEST_DRAFT_MODE = "create_request_draft_mode"
    }

    @Inject
    lateinit var presenter: ICreateRequestPresenter

    private var fias: String = ""
    private var uuid: String = UUID.randomUUID().toString()
    private var draftFlag:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_request)
        initViews()
        checkParams()
    }

    private fun checkParams() {
        when {
            intent.getBooleanExtra(CREATE_REQUEST_EDIT_MODE, false) -> {
                presenter.requestId = intent.getIntExtra(REQUEST_ID, -1)
                configureBottomFrame(isEditMode = true)
                presenter.onViewLoadWithEditMode()
                presenter.onViewAttached()
            }
            intent.getBooleanExtra(CREATE_REQUEST_DRAFT_MODE, false) -> {
                configureBottomFrame(isEditMode = false)
                presenter.fetchDraftData(intent.getStringExtra(DraftRequestListFragment.OLD_UUID))
                presenter.onViewAttached()
            }
            else -> {
                presenter.onViewAttached()
                configureBottomFrame(isEditMode = false)
            }
        }
    }

    override fun setDraftData(realmDraft: RealmDraft){
        createRequestNameRequestTextEdit.setText(realmDraft.title)
        createRequestDescriptionTextEdit.setText(realmDraft.description)
        createRequestFIOTextEdit.setText(realmDraft.fio)
        createRequestNumberPhoneTextEdit.setText(realmDraft.phoneNum)
        createRequestAddressTextEdit.setText(realmDraft.address)
        typeCompanySpinner.setText(realmDraft.company)
        typeHouseSpinner.setText(realmDraft.typeHouse)
        typeRequestSpinner.setText(realmDraft.typeRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.request_bucket_item->{
                presenter.deleteOldValue(intent.getStringExtra(DraftRequestListFragment.OLD_UUID))
                draftFlag=true
                this.finish()
                return true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.create_request_menu, menu)
        return true
    }

    override fun setFieldByRealmRequest(realmRequest: RealmRequest) {
        createRequestNameRequestTextEdit.setText(realmRequest.name)
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
        createRequestAddressTextEdit.keyListener = null
        createRequestAddressTextEdit.onClick {
            presenter.onAddressFieldClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            SearchAddressActivity.REQUEST_ADDRESS_RESULT_CODE -> {
                createRequestAddressTextEdit.setText(data?.getStringExtra(SearchAddressActivity.RESULT_ADDRESS_KEY))
                fias = data?.getStringExtra(SearchAddressActivity.RESULT_FIAS_KEY)!!
                presenter.fetchCompanies(fias)
                Log.e("FIAS", fias)
            }
        }
    }

    override fun onFetchCompaniesError() {
        createRequestAddressTextEdit.text = null
        typeCompanySpinner.setAdapter(null)
        typeCompanySpinner.setText("")
        Snackbar.make(findViewById(android.R.id.content), "Плохое соединение с сетью", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.splash_try_again_text) { presenter.onTryAgainClicked(fias) }
                .show()
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

    override fun setTypeHouseSpinner(typeHouseList: List<String>) {
        val adapter = TypeHouseAdapter(this, R.layout.item_spinner_property_type_request, typeHouseList = typeHouseList)
        typeHouseSpinner.setAdapter(adapter)
        typeHouseSpinner.keyListener = null
        var visible = false
        typeHouseSpinner.onClick {
            if (!visible) {
                visible = true
                typeHouseSpinner.showDropDown()
            } else {
                visible = false
                typeHouseSpinner.dismissDropDown()
            }
        }
    }

    override fun setCompaniesSpinner(companiesList: List<String>) {
        val adapter = CompaniesAdapter(this, R.layout.item_spinner_company_request, companiesList = companiesList)
        typeCompanySpinner.setAdapter(adapter)
        var visible = false
        typeCompanySpinner.onClick {
            if (!visible) {
                visible = true
                typeCompanySpinner.showDropDown()
            } else {
                visible = false
                typeCompanySpinner.dismissDropDown()
            }
        }
    }

    private fun saveDraftRequest() {
        if (createRequestNameRequestTextEdit.text!!.isNotEmpty()
                || createRequestAddressTextEdit.text!!.isNotEmpty()
                || typeCompanySpinner.text!!.isNotEmpty()
                || typeRequestSpinner.text!!.isNotEmpty()
                || typeHouseSpinner.text!!.isNotEmpty()
                || createRequestDescriptionTextEdit.text!!.isNotEmpty()
                || createRequestFIOTextEdit.text!!.isNotEmpty()
                || createRequestNumberPhoneTextEdit.text!!.isNotEmpty()
        ) {
            val draft = RealmDraft(
                    id = uuid,
                    title = createRequestNameRequestTextEdit.text.toString(),
                    address = createRequestAddressTextEdit.text.toString(),
                    company = typeCompanySpinner.text.toString(),
                    typeRequest = typeRequestSpinner.text.toString(),
                    typeHouse = typeHouseSpinner.text.toString(),
                    description = createRequestDescriptionTextEdit.text.toString(),
                    fio = createRequestFIOTextEdit.text.toString(),
                    phoneNum = createRequestNumberPhoneTextEdit.text.toString()
            )
            presenter.saveDraftRequest(draft)
        }
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

    override fun onPause() {
        super.onPause()
        if (!draftFlag) {
            saveDraftRequest()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!draftFlag) {
            saveDraftRequest()
        }
    }
}