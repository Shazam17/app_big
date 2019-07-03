package com.software.ssp.erkc.modules.requestdetails

import android.content.res.Resources
import android.graphics.Point
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.dp
import kotlinx.android.synthetic.main.activity_request_details.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

const val HEIGHT_WITH_CONFIRM_BUTTON: Int = 115
const val HEIGHT_WITHOUT_CONFIRM_BUTTON: Int = 77

class RequestDetailsActivity : MvpActivity(), IRequestDetailsView {

    @Inject
    lateinit var presenter: IRequestDetailsPresenter
    var menuRequestDetails: Menu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_details)

        initViews()
        // TODO test Framelayout
        configureBottomFrameLayout(true)
        presenter.onViewAttached()
    }


    override fun navigateToEditRequestScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToChatScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSelectImagesList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showStatusesList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun configureBottomFrameLayout(isWorkStatus: Boolean) {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x

        val params = bottomFrameLayoutRequestDetails.layoutParams

        params.width = width
        params.height = if (isWorkStatus) HEIGHT_WITH_CONFIRM_BUTTON.dp else HEIGHT_WITHOUT_CONFIRM_BUTTON.dp
        bottomFrameLayoutRequestDetails.layoutParams = params

        requestDetailsSubmitCompleteButton.visibility = if (isWorkStatus) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.request_details_edit_item -> {
                // TODO impl
                return true
            }
            R.id.request_details_with_message_item -> {
                // TODO impl
                return true
            }
            R.id.request_details_without_message_item -> {
                // TODO impl
                return true
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.request_details_menu, menu)
        menu?.findItem(R.id.request_details_edit_item)?.isVisible = false
        menu?.findItem(R.id.request_details_with_message_item)?.isVisible = false
        menu?.findItem(R.id.request_details_without_message_item)?.isVisible = false
        menuRequestDetails = menu
        return true
    }

    // TODO do impl when get new API
//    fun visibleNeedMenuItem() {
//        when (statusType) {
//            "Ожидает рассмотрения" -> {
//                menuRequestDetails?.findItem(R.id.request_details_edit_item)?.isVisible = true
//                menuRequestDetails?.findItem(R.id.request_details_with_message_item)?.isVisible = false
//                menuRequestDetails?.findItem(R.id.request_details_without_message_item)?.isVisible = false
//            }
//            else -> {
//                if ("хотя бы одно сообщение") {
//                    menuRequestDetails?.findItem(R.id.request_details_edit_item)?.isVisible = false
//                    menuRequestDetails?.findItem(R.id.request_details_with_message_item)?.isVisible = true
//                    menuRequestDetails?.findItem(R.id.request_details_without_message_item)?.isVisible = false
//                } else {
//                    menuRequestDetails?.findItem(R.id.request_details_edit_item)?.isVisible = false
//                    menuRequestDetails?.findItem(R.id.request_details_with_message_item)?.isVisible = false
//                    menuRequestDetails?.findItem(R.id.request_details_without_message_item)?.isVisible = true
//                }
//            }
//        }
//    }


    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerRequestDetailsComponent.builder()
                .appComponent(appComponent)
                .requestDetailsModule(RequestDetailsModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }
}