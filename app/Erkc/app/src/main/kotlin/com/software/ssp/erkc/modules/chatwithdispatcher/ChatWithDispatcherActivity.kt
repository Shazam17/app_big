package com.software.ssp.erkc.modules.chatwithdispatcher

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Comment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity
import kotlinx.android.synthetic.main.activity_chat_with_dispatcher.*
import javax.inject.Inject

class ChatWithDispatcherActivity: MvpActivity(), IChatWithDispatcherView {

    @Inject lateinit var presenter: IChatWithDispatcherPresenter

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

    private fun initViews() {
        // TODO init views
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.setHasFixedSize(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = "Обращение #3597842 от 12.20.2018 10:00"
        supportActionBar?.title = "Сломанная лампочка"
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
    override fun createChatAdapter(comments: List<Comment>) {
        val adapter = ChatWithDispatcherAdapter(
                dataList = comments
        )

        messagesRecyclerView.adapter = adapter
        messagesRecyclerView.adapter?.notifyDataSetChanged()
    }
}