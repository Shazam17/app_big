package com.software.ssp.erkc.modules.createrequest

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_create_request.*
import javax.inject.Inject

class CreateRequestActivity: MvpActivity(), ICreateRequestView {

    companion object {
        const val CREATE_REQUEST_EDIT_MODE = "create_request_edit_mode"
        const val REQUEST_ID = "request_id"
    }

    @Inject lateinit var presenter: ICreateRequestPresenter

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

    override fun setFieldByRealmReqeust(realmRequest: RealmRequest) {
        createRequestNameRequestTextEdit.setText(realmRequest.name)
        createRequestAddressTextEdit.setText(realmRequest.house?.address)
        createRequestServiceProviderTextEdit.setText(realmRequest.company?.name)
        createRequestTypeRequestTextEdit.setText(realmRequest.type?.name)
        createRequestTypeStoreTextEdit.setText(realmRequest.premise?.number)
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

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }
}