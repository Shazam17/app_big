package com.software.ssp.erkc.modules.createrequest

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.software.ssp.erkc.BuildConfig
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmLocalImage
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.chatwithdispatcher.ChatWithDispatcherActivity
import com.software.ssp.erkc.modules.createrequest.adapters.CompaniesAdapter
import com.software.ssp.erkc.modules.createrequest.adapters.SelectedImagesAdapter
import com.software.ssp.erkc.modules.createrequest.adapters.TypeHouseAdapter
import com.software.ssp.erkc.modules.request.authedRequest.draftRequestList.DraftRequestListFragment
import com.software.ssp.erkc.utils.FileUtils
import com.tbruyelle.rxpermissions.RxPermissions
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_create_request.*
import kotlinx.android.synthetic.main.fragment_photo_values.*
import org.jetbrains.anko.*
import java.io.IOException
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
    private var saveFlag: Boolean = true
    private var selectImagesList: RealmList<RealmLocalImage> = RealmList()
    private var lastCameraImagePath: String? = null

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

    override fun setDraftData(realmDraft: RealmDraft) {
        createRequestNameRequestTextEdit.setText(realmDraft.title)
        createRequestDescriptionTextEdit.setText(realmDraft.description)
        createRequestFIOTextEdit.setText(realmDraft.fio)
        createRequestNumberPhoneTextEdit.setText(realmDraft.phoneNum)
        createRequestAddressTextEdit.setText(realmDraft.address)
        typeCompanySpinner.setText(realmDraft.company)
        typeHouseSpinner.setText(realmDraft.typeHouse)
        typeRequestSpinner.setText(realmDraft.typeRequest)
        setSelectedImagesAdapter(realmDraft.images)
    }

    private fun setSelectedImagesAdapter(images: RealmList<RealmLocalImage>) {
        val tmpImages: RealmList<RealmLocalImage> = RealmList()
        images.forEach { localImage ->
            tmpImages.add(RealmLocalImage(url = localImage.url))
        }
        selectImagesList = tmpImages
        createSelectedImagesAdapter(selectImagesList)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                when {
                    intent.getBooleanExtra(CREATE_REQUEST_DRAFT_MODE, false) -> {
                        presenter.deleteOldValue(intent.getStringExtra(DraftRequestListFragment.OLD_UUID))
                        saveFlag = true
                        onBackPressed()
                    }
                    intent.getBooleanExtra(CREATE_REQUEST_EDIT_MODE, false) -> {
                        saveFlag = false
                        onBackPressed()
                    }
                    else -> {
                        saveFlag = true
                        onBackPressed()
                    }
                }
                return true
            }
            R.id.request_bucket_item -> {
                when {
                    intent.getBooleanExtra(CREATE_REQUEST_DRAFT_MODE, false) -> {
                        presenter.deleteOldValue(intent.getStringExtra(DraftRequestListFragment.OLD_UUID))
                        saveFlag = false
                        onBackPressed()
                    }
                    intent.getBooleanExtra(CREATE_REQUEST_EDIT_MODE, false) -> onBackPressed()
                    else -> {
                        this.finish()
                        saveFlag = false
                        onBackPressed()
                    }
                }
                return true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.create_request_menu, menu)
        if (intent.getBooleanExtra(CREATE_REQUEST_EDIT_MODE, false)) {
            menu?.findItem(R.id.request_bucket_item)?.isVisible = false
        }
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

        createRequestAddPhotoButton.onClick {
            showGalleryOrCameraDialog()
        }

        createRequestPhotosRecyclerView.layoutManager = GridLayoutManager(this, 4)
        createRequestPhotosRecyclerView.setHasFixedSize(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            lastCameraImagePath = null
        }

        when (requestCode) {
            SearchAddressActivity.REQUEST_ADDRESS_RESULT_CODE -> {
                if (data?.getStringExtra(SearchAddressActivity.RESULT_ADDRESS_KEY) != null) {
                    createRequestAddressTextEdit.setText(data?.getStringExtra(SearchAddressActivity.RESULT_ADDRESS_KEY))
                    fias = data?.getStringExtra(SearchAddressActivity.RESULT_FIAS_KEY)!!
                    presenter.fetchCompanies(fias)
                    Log.e("FIAS", fias)
                }
            }

            ChatWithDispatcherActivity.CAMERA_REQUEST_CODE -> {
                // TODO Impl handle data from camera
                if (lastCameraImagePath == null) return

                selectImagesList.add(
                        RealmLocalImage(url = lastCameraImagePath)
                )
                notifySelectedImagesListDataChange()
            }

            ChatWithDispatcherActivity.GALLERY_REQUEST_CODE -> {
                // TODO Impl handle data from gallery
                if (data == null) return
                val imagePath = getAbsolutePathByUri(uri = data.data)
                selectImagesList.add(RealmLocalImage(url = imagePath))
                notifySelectedImagesListDataChange()
            }
        }
    }

    override fun notifySelectedImagesListDataChange() {
        createSelectedImagesAdapter(selectImagesList)
    }

    override fun createSelectedImagesAdapter(images: RealmList<RealmLocalImage>) {
        val adapter = SelectedImagesAdapter(
                images = images,
                onItemClick = { localImage -> presenter.onPhotoClick(localImage = localImage) },
                onDeleteItemClick = { localImage ->
                    selectImagesList.remove(localImage)
                    createSelectedImagesAdapter(selectImagesList)
                }
        )
        createRequestPhotosRecyclerView.adapter = adapter
//        createRequestPhotosRecyclerView.adapter?.notifyDataSetChanged()
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
                    phoneNum = createRequestNumberPhoneTextEdit.text.toString(),
                    images = selectImagesList
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

//    override fun onPause() {
//        super.onPause()
//        if (saveFlag) {
//            saveDraftRequest()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (saveFlag) {
            saveDraftRequest()
        }
        presenter.setEvent()
    }

    private fun showGalleryOrCameraDialog() {
        val alert = AlertDialog.Builder(this)
        alert.setItems(R.array.camera_or_gallery_alert_titles) { _, which ->
            when (which) {
                ChatWithDispatcherActivity.AttachmentType.GALLERY.ordinal -> presenter.onGalleryButtonClick()
                ChatWithDispatcherActivity.AttachmentType.CAMERA.ordinal -> {
                    val rxPermissions = RxPermissions.getInstance(this)
                    rxPermissions
                            .request(
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.CAMERA)
                            .subscribe {
                                if (it) {
                                    presenter.onCameraButtonClick()

                                } else {
                                    showMessage("Ошибка прав")
                                }
                            }
                }
            }
        }
        alert.show()
    }

    override fun showCameraScreen() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val imageFile = FileUtils.shareInstance.createImageFile()
            lastCameraImagePath = "file://" + imageFile.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, imageFile))
            startActivityForResult(intent, ChatWithDispatcherActivity.CAMERA_REQUEST_CODE)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }


    override fun showGalleryScreen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayListOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, ChatWithDispatcherActivity.GALLERY_REQUEST_CODE)
    }

    private fun getAbsolutePathByUri(uri: Uri): String {
        val cursor = this.contentResolver.query(uri, null, null, null, null)
        val imagePath = if (cursor == null) {
            uri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }

        return imagePath
    }
}