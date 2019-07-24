package com.software.ssp.erkc.modules.request.authedRequest.draftRequestList

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.createrequest.CreateRequestActivity
import com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.ActiveRequestListAdapter
import kotlinx.android.synthetic.main.fragment_request_list.*
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class DraftRequestListFragment: BaseListFragment<RealmDraft>(), IDraftRequestListView {
    override fun onSwipeToRefresh() {
        setLoadingVisible(false)
    }

    @Inject lateinit var presenter: IDraftRequestListPresenter

    companion object {
        const val CREATE_REQUEST_DRAFT_MODE = "create_request_draft_mode"
        const val OLD_UUID="old_uuid"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_request_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }



    override fun createAdapter(): RecyclerView.Adapter<*> {
        return DraftListAdapter(
                dataset,
                { request -> presenter.onDraftClick(request) }
        )
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerDraftRequestListComponent.builder()
                .appComponent(appComponent)
                .draftRequestListModule(DraftRequestListModule(this))
                .build()
                .inject(this)
    }

    override fun setVisibleEmptyMessage(isVisible: Boolean) {
        // TODO Visible empty message ...
        emptyMessageTextView.text = ""
        emptyView.visibility = if (isVisible) View.VISIBLE else View.GONE
        emptyMessageTextView.text = if (isVisible) resources.getString(R.string.draft_request_tap_empty_text) else ""
    }

    override fun setVisibleEmptyMessageWithFilter(isVisible: Boolean) {}

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }

    override fun navigateToRequestInfo(request: RealmDraft?) {
        startActivity<CreateRequestActivity>(RequestDetailsActivity.REQUEST_DETAILS_REQUEST_ID_KEY to request!!.id,CREATE_REQUEST_DRAFT_MODE to true,OLD_UUID to request.id)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewAttached()
    }
}