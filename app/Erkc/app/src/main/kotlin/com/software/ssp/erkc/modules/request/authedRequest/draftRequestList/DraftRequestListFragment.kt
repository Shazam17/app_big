package com.software.ssp.erkc.modules.request.authedRequest.draftRequestList

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.ActiveRequestListAdapter
import kotlinx.android.synthetic.main.fragment_request_list.*
import javax.inject.Inject

class DraftRequestListFragment: BaseListFragment<RealmRequest>(), IDraftRequestListView {

    @Inject lateinit var presenter: IDraftRequestListPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_request_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun onSwipeToRefresh() {

    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ActiveRequestListAdapter(
                dataset,
                { request -> presenter.onRequestClick(request) }
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
        emptyMessageTextViewReqeust.text = ""
        emptyViewRequest.visibility = if (isVisible) View.VISIBLE else View.GONE
        emptyMessageTextViewReqeust.text = if (isVisible) resources.getString(R.string.draft_request_tap_empty_text) else ""
    }

    override fun setVisibleEmptyMessageWithFilter(isVisible: Boolean) {}

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }

    override fun navigateToRequestInfo(request: RealmRequest) {

    }
}