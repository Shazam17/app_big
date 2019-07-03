package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.createrequest.CreateRequestActivity
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity
import kotlinx.android.synthetic.main.activity_request_details.*
import kotlinx.android.synthetic.main.fragment_request_tab.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

class ActiveRequestListFragment : BaseListFragment<RealmRequest>(), IActiveRequestListView {


    @Inject
    lateinit var presenter: IActiveRequestListPresenter


    override fun injectDependencies(appComponent: AppComponent) {
        DaggerActiveRequestListComponent.builder()
                .appComponent(appComponent)
                .activeRequestListModule(ActiveRequestListModule(this))
                .build()
                .inject(this)
    }

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
//                { request -> presenter.onRequestClick(request) }
                {startActivity<RequestDetailsActivity>()}
        )
    }


    override fun beforeDestroy() {
    }


    override fun navigateToRequestInfo(request: RealmRequest) {

    }


}