package com.software.ssp.erkc.modules.request.authedRequest.archiveRequestList

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.ActiveRequestListAdapter
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.FilterRequestChipTag
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity
import kotlinx.android.synthetic.main.fragment_request_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class ArchiveRequestListFragment: BaseListFragment<RealmRequest>(), IArchiveRequestListView {

    @Inject lateinit var presenter: IArchiveRequestListPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_request_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun onSwipeToRefresh() {
        presenter.onRefreshClick()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ActiveRequestListAdapter(
                dataset,
                { request -> presenter.onRequestClick(request) }
        )
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerArchiveRequestListComponent
                .builder()
                .appComponent(appComponent)
                .archiveRequestListModule(ArchiveRequestListModule(this))
                .build()
                .inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.request_menu_filter -> {
                onFilterClick()
                return true
            }
            R.id.request_menu_refresh -> {
                presenter.onRefreshClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun onFilterClick() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.choose_filter)
                .setMultiChoiceItems(presenter.ownerStatusLabels.toTypedArray(), presenter.selectStatus.toBooleanArray()) { _, which, isChecked ->
                    presenter.arrayStatus[which].isChecked = isChecked
                }.setPositiveButton("Найти") { _, _ ->
                    presenter.onFilterClick()
                }.setNegativeButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }
        val alert = builder.create()
        alert.show()
    }


    override fun openFilterAlert(listStatus: ArrayList<StatusModel>) {
        listStatus.forEach {
            if (it.isChecked) {
                filterChipView.add(FilterRequestChipTag(it.label))
            } else {
                filterChipView.remove(filterChipView.chipList.find { chip -> chip.text == it.label })

            }
        }
        filterChipView.setOnChipClickListener { chip ->
            filterChipView.remove(chip)
            deleteFilter((chip as FilterRequestChipTag).field)
        }
        filterChipView.refresh()
    }

    private fun deleteFilter(filterField: String) {
        when (filterField) {
            "Выполнено" -> presenter.arrayStatus[0].isChecked = false
            "Отменено" -> presenter.arrayStatus[1].isChecked = false
            else -> return
        }
    }

    override fun setVisibleEmptyMessage(isVisible: Boolean) {
        // TODO Visible empty message ...
        emptyMessageTextView.text = ""
        emptyView.visibility = if (isVisible) View.VISIBLE else View.GONE
        emptyMessageTextView.text = if (isVisible) resources.getString(R.string.archive_request_tab_empty_text) else ""
    }

    override fun setVisibleEmptyMessageWithFilter(isVisible: Boolean) {
        // TODO Visible empty message when user use filter ...
        emptyMessageTextView.text = ""
        emptyView.visibility = if (isVisible) View.VISIBLE else View.GONE
        emptyMessageTextView.text = if (isVisible) resources.getString(R.string.empty_request_list_with_filter_text) else ""
    }


    override fun beforeDestroy() {
        presenter.onViewDetached()
    }

    override fun navigateToRequestDetails(requestId: Int, titleRequest: String) {
        startActivity<RequestDetailsActivity>(RequestDetailsActivity.REQUEST_DETAILS_REQUEST_ID_KEY to requestId, RequestDetailsActivity.REQUEST_DETAILS_TITLE_REQUEST_KEY to titleRequest)
    }
}