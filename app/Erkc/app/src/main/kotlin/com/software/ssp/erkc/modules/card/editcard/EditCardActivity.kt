package com.software.ssp.erkc.modules.card.editcard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_add_card.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onEditorAction
import javax.inject.Inject


class EditCardActivity : MvpActivity(), IEditCardView {

    @Inject lateinit var presenter: IEditCardPresenter
    private var card: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        card = intent.getParcelableExtra<Card>(Constants.KEY_SELECTED_CARD_ITEM)
        initViews()
        presenter.onViewAttached()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerEditCardComponent.builder()
                .appComponent(appComponent)
                .editCardModule(EditCardModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun close() {
        finish()
    }

    override fun setPending(isPending: Boolean) {
        progressBar.visibility = if(isPending) View.VISIBLE else View.GONE
        addCardNameEditText.enabled = !isPending
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        addCardNameEditText.setText(card!!.name)

        addCardNameEditText.onEditorAction { textView, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onSaveClick(card!!, addCardNameEditText.text.toString())
                true
            } else {
                false
            }
        }
    }
}
