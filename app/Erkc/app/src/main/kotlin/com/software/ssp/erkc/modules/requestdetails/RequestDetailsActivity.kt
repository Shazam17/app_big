package com.software.ssp.erkc.modules.requestdetails

import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmComment
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RealmRequestStatus
import com.software.ssp.erkc.data.realm.models.RequestStatusTypes
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.dp
import com.software.ssp.erkc.modules.chatwithdispatcher.ChatWithDispatcherActivity
import com.software.ssp.erkc.modules.createrequest.CreateRequestActivity
import com.software.ssp.erkc.modules.fullscreenphoto.FullscreenPhotoActivity
import kotlinx.android.synthetic.main.activity_request_details.*
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val HEIGHT_WITH_CONFIRM_BUTTON: Int = 115
const val HEIGHT_WITHOUT_CONFIRM_BUTTON: Int = 77

class RequestDetailsActivity : MvpActivity(), IRequestDetailsView {


    companion object {
        const val REQUEST_DETAILS_REQUEST_ID_KEY = "request_details_request_id_key"
        const val REQUEST_DETAILS_TITLE_REQUEST_KEY = "request_details_title_request_key"
        const val PHOTO_LINK = "photo_link"
        const val IS_EDITABLE="is_editable"

    }

    enum class StateActionMenu {
        EDIT,
        WITH_MESSAGE,
        WITHOUT_MESSAGE
    }

    @Inject
    lateinit var presenter: IRequestDetailsPresenter
    var menuRequestDetails: Menu? = null
    var stateActionMenu: StateActionMenu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)

        initViews()
        // TODO when will API model set work status in arg
        presenter.requestId = intent.getIntExtra(REQUEST_DETAILS_REQUEST_ID_KEY, -1)

        presenter.onViewAttached()
    }


    override fun navigateToEditRequestScreen(requestId: Int) {
        startActivity<CreateRequestActivity>(CreateRequestActivity.CREATE_REQUEST_EDIT_MODE to true, CreateRequestActivity.REQUEST_ID to requestId)
    }

    override fun navigateToChatScreen(requestId: Int) {
        startActivity<ChatWithDispatcherActivity>(REQUEST_DETAILS_REQUEST_ID_KEY to requestId)
    }



    override fun showStatusesList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun configureBottomFrameLayout(statusType: String) {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x

        val params = bottomFrameLayoutRequestDetails.layoutParams

        params.width = width
        when (statusType) {
            RequestStatusTypes.PendingReview.name, RequestStatusTypes.OnReview.name -> {
                requestDetailsSubmitCompleteButton.visibility = View.GONE
                requestDetailsCancelButton.visibility = View.VISIBLE
                params.height = HEIGHT_WITHOUT_CONFIRM_BUTTON.dp
            }

            RequestStatusTypes.InWork.name -> {
                requestDetailsSubmitCompleteButton.visibility = View.VISIBLE
                requestDetailsCancelButton.visibility = View.VISIBLE
                params.height = HEIGHT_WITH_CONFIRM_BUTTON.dp
            }

            else -> {
                requestDetailsSubmitCompleteButton.visibility = View.GONE
                requestDetailsCancelButton.visibility = View.GONE
                params.height = 0.dp
            }

        }
        bottomFrameLayoutRequestDetails.layoutParams = params
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.request_details_edit_item -> {
                // TODO impl
                presenter.onEditMenuItemClick()
                return true
            }
            R.id.request_details_with_message_item -> {
                presenter.onChatMenuItemClick()
                return true
            }
            R.id.request_details_without_message_item -> {
                presenter.onChatMenuItemClick()
                return true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.request_details_menu, menu)
        when (stateActionMenu) {

            StateActionMenu.EDIT -> {
                menu?.findItem(R.id.request_details_edit_item)?.isVisible = true
                menu?.findItem(R.id.request_details_with_message_item)?.isVisible = false
                menu?.findItem(R.id.request_details_without_message_item)?.isVisible = false
            }

            StateActionMenu.WITH_MESSAGE -> {
                menu?.findItem(R.id.request_details_edit_item)?.isVisible = false
                menu?.findItem(R.id.request_details_with_message_item)?.isVisible = true
                menu?.findItem(R.id.request_details_without_message_item)?.isVisible = false
            }

            StateActionMenu.WITHOUT_MESSAGE -> {
                menuRequestDetails?.findItem(R.id.request_details_edit_item)?.isVisible = false
                menuRequestDetails?.findItem(R.id.request_details_with_message_item)?.isVisible = false
                menuRequestDetails?.findItem(R.id.request_details_without_message_item)?.isVisible = true
            }

            null -> {
                menu?.findItem(R.id.request_details_edit_item)?.isVisible = false
                menu?.findItem(R.id.request_details_with_message_item)?.isVisible = true
                menu?.findItem(R.id.request_details_without_message_item)?.isVisible = false
            }
        }

        menuRequestDetails = menu
        return true
    }

    override fun showRequestDetails(realmRequest: RealmRequest) {
        supportActionBar?.title = realmRequest.name
        val formatter = SimpleDateFormat("dd.MM.yyyy hh:mm")
        val date=formatter.format((Date(realmRequest.created_at!!)))
        requestDetailsMainTitleTextView.text = "Обращение №${realmRequest.id} от ${date}"
        requestDetailsInfoProblemTextView.text = realmRequest.type?.name
        requestDetailsDescriptionTextView.text = realmRequest.message
    }

    // TODO do impl when get new API
    override fun visibleNeedMenuItem(statusType: String) {
        when (statusType) {
            RequestStatusTypes.PendingReview.name -> {
                stateActionMenu = StateActionMenu.EDIT
            }
            else -> {
                if (true /* if have one or more messages */) {
                    stateActionMenu = StateActionMenu.WITH_MESSAGE
                } else {
                    stateActionMenu = StateActionMenu.WITHOUT_MESSAGE
                }
            }
        }
        invalidateOptionsMenu()
    }

    override fun showSelectImagesList(comment: List<RealmComment>) {
        val adapter = RequestDetailsFileListAdapter(
                requestComments = comment.filter { it.downloadLink!=null },
                onItemClick = {presenter.openFullScreen(it)}
        )

        requestDetailsPhotosRecyclerView.adapter = adapter
        requestDetailsPhotosRecyclerView.adapter?.notifyDataSetChanged()
    }


    override fun createStatusAdapter(statusList: List<RealmRequestStatus>) {
        val adapter = RequestDetailsStatusListAdapter(
                requestStatusItems = statusList
        )

        requestDetailsStatuesRecyclerView.adapter = adapter
        requestDetailsStatuesRecyclerView.adapter?.notifyDataSetChanged()
    }


    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(REQUEST_DETAILS_TITLE_REQUEST_KEY)
        requestDetailsPhotosRecyclerView.layoutManager = GridLayoutManager(this,4)
        requestDetailsPhotosRecyclerView.setHasFixedSize(true)
        requestDetailsStatuesRecyclerView.layoutManager = LinearLayoutManager(this)
        requestDetailsStatuesRecyclerView.setHasFixedSize(true)
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerRequestDetailsComponent.builder()
                .appComponent(appComponent)
                .requestDetailsModule(RequestDetailsModule(this))
                .build()
                .inject(this)
    }

    override fun setVisibleProgressBar(isVisible: Boolean) {
        loadDetailsRequestProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        scrollViewContainerRequestDetails.visibility = if (isVisible) View.GONE else View.VISIBLE
        bottomFrameLayoutRequestDetails.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }

    override fun openFullScreen(downloadLink: String) {
        startActivity<FullscreenPhotoActivity>(PHOTO_LINK to downloadLink, IS_EDITABLE to false)
    }

}