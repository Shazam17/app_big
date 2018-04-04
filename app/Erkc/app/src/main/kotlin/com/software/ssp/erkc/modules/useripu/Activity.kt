package com.software.ssp.erkc.modules.useripu

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import kotlinx.android.synthetic.main.activity_add_user_ipu.*
import kotlinx.android.synthetic.main.activity_send_values.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import rx.Observable
import javax.inject.Inject

class Activity : MvpActivity(), IModuleView {

    @Inject lateinit var presenter: IModulePresenter

    private val receiptId: String? by extras(Constants.KEY_RECEIPT)
    private val ipu_number: String? by extras(Constants.KEY_IPU_NUMBER)

    private var delete_menu_item: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_ipu)
        initViews()

        presenter.ipu_number = ipu_number
        presenter.receiptId = receiptId

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerAComponent.builder()
                .appComponent(appComponent)
                .aModule(AModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_ipu_menu, menu)
        delete_menu_item = menu?.findItem(R.id.menu_delete)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.menu_delete -> {
                presenter.delete()
            }
        }
        return true
    }

    private fun initViews() {
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        commitButton.onClick { presenter.commitClicked() }
    }

    override fun bindData(ipu_data: Presenter.UserIPUData) {
        number.textChangedListener { afterTextChanged { e->ipu_data.number = e.toString() } }
        location.textChangedListener { afterTextChanged { e->ipu_data.location = e.toString() } }
    }

    override fun close() {
        finish()
    }

    override fun setModeAdd() {
        delete_menu_item?.setVisible(false)
        commitButton.setText(R.string.user_ipu_commit)
        setTitle(R.string.user_ipu_add_mode)
    }

    override fun setModeEdit() {
        delete_menu_item?.setVisible(true)
        commitButton.setText(R.string.user_ipu_commit_save)
        setTitle(R.string.user_ipu_edit_mode)
    }

    override fun setData(data: Presenter.UserIPUData) {
        number.setText(data.number)
        location.setText(data.location)
    }

    override fun askDeleteIPUAndData(): Observable<Boolean> {
        return Observable.create({
            e->
                alert(R.string.user_ipu_ask_delete) {
                    positiveButton(R.string.answer_positive, {e.onNext(true)})
                    onCancel { e.onNext(false) }
                }.show()
        })
    }
}