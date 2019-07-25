package com.software.ssp.erkc.modules.chatwithdispatcher

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity
import kotlinx.android.synthetic.main.activity_chat_with_dispatcher.*
import org.jetbrains.anko.onClick
import java.io.IOException
import javax.inject.Inject
import android.support.v4.content.FileProvider
import android.provider.MediaStore
import com.software.ssp.erkc.BuildConfig
import com.software.ssp.erkc.data.realm.models.RealmComment
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.utils.FileUtils
import com.tbruyelle.rxpermissions.RxPermissions


class ChatWithDispatcherActivity: MvpActivity(), IChatWithDispatcherView, Animation.AnimationListener {

    @Inject lateinit var presenter: IChatWithDispatcherPresenter
    private var cameraFilePath: String = ""

    companion object {
        const val GALLERY_REQUEST_CODE = 1009
        const val CAMERA_REQUEST_CODE = 1010
    }

    internal enum class AttachmentType(id: Int) {
        GALLERY(0),
        CAMERA(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_with_dispatcher)
        initViews()
        presenter.requestId = intent.getIntExtra(RequestDetailsActivity.REQUEST_DETAILS_REQUEST_ID_KEY, -1)
        presenter.onViewAttached()
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

    override fun setTitleAndSubTitle(title: String, subtitle: String) {
        supportActionBar?.subtitle = subtitle
        supportActionBar?.title = title
    }

    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(this)
        messagesRecyclerView.layoutManager = linearLayoutManager
        messagesRecyclerView.setHasFixedSize(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        deleteAttachmentImageButton.onClick {
            presenter.onDeleteAttachmentButtonClick()
        }

        cameraButtonChat.onClick {
            showCameraOrGalleryDialog()
        }

        sendButtonChat.onClick {
            presenter.onSendMessageButtonClick(textMessage = textMessageByUserEditText.text.toString(), context = this)
        }
    }

    override fun setVisibleProgressBar(isVisible: Boolean) {
        loadingMessagesProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerChatWithDispatcherComponent.builder()
                .appComponent(appComponent)
                .chatWithDispatcherModule(ChatWithDispatcherModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }
    override fun createChatAdapter(comments: List<RealmComment>) {
        val adapter = ChatWithDispatcherAdapter(
                dataList = comments.sortedBy { it.id }
        )

        messagesRecyclerView.adapter = adapter
        messagesRecyclerView.adapter?.notifyDataSetChanged()
        messagesRecyclerView.scrollToPosition(comments.count() - 1)
    }

    private fun animateCloseAttachmentContainer() {
        val attachmentContainer = findViewById<ConstraintLayout>(R.id.imageAttachmentByUserChat)
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_attachment_chat_message_container)
        animation.setAnimationListener(this)
        attachmentContainer.startAnimation(animation)
    }

    override fun hideAttachmentContainer() {
        animateCloseAttachmentContainer()
    }

    override fun setVisibleInputContainer(isVisible: Boolean) {
        bottomConstraintLayoutChat.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showAttachmentContainer() {
        imageAttachmentByUserChat.visibility = View.VISIBLE
    }

    override fun onAnimationStart(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        imageAttachmentByUserChat.visibility = View.GONE
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (data == null) return
                presenter.imageUri = null
                addAttachmentForMessage(uri = data.data)
            }

            CAMERA_REQUEST_CODE -> {
                presenter.imageUri = null
                addAttachmentForMessage(uri = Uri.parse(cameraFilePath))
            }
        }
    }

    private fun addAttachmentForMessage(uri: Uri) {
        presenter.imageUri = uri
        imageAttachmentByUserChatImageView.setImageURI(uri)
        showAttachmentContainer()
    }

    override fun showGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayListOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun showCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val imageFile = FileUtils.shareInstance.createImageFile()
            cameraFilePath = "file://" + imageFile.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, imageFile))
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun showCameraOrGalleryDialog() {
        val alert = AlertDialog.Builder(this)
        alert.setItems(R.array.camera_or_gallery_alert_titles) { _, which ->
            when (which) {
                AttachmentType.GALLERY.ordinal -> presenter.onGalleryButtonClick()
                AttachmentType.CAMERA.ordinal -> {
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

    override fun cleanInputContainer() {
        textMessageByUserEditText.text.clear()
        presenter.imageUri = null
        imageAttachmentByUserChat.visibility = View.GONE
        hideKeyboard()
    }

    override fun setEnableInputContainer(isEnable: Boolean) {
        textMessageByUserEditText.isEnabled = isEnable
        imageAttachmentByUserChat.isEnabled = isEnable
        cameraButtonChat.isEnabled = isEnable
        sendButtonChat.isEnabled = isEnable
        sendButtonChat.visibility = if (isEnable) View.VISIBLE else View.GONE
        deleteAttachmentImageButton.isEnabled = isEnable
        sendingMessageProgressBar.visibility = if (isEnable) View.GONE else View.VISIBLE
    }
}