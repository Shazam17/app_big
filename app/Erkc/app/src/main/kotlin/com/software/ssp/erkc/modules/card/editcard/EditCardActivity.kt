package com.software.ssp.erkc.modules.card.editcard

import android.app.ProgressDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.cardname_layout.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.onKey
import javax.inject.Inject

/**
 * @author Alexander Popov on 08/11/2016.
 */
class EditCardActivity : MvpActivity(), IEditCardView {

    @Inject lateinit var presenter: IEditCardPresenter
    private var card: Card? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cardname_layout)
        card = intent.getParcelableExtra<Card>(Constants.KEY_SELECTED_CARD_ITEM)
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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

    override fun navigateToDrawer() {
        finish()
    }

    override fun setLoadingVisible(isVisible: Boolean) {
        if (progressDialog == null) {
            progressDialog = indeterminateProgressDialog(R.string.load_data)
        }
        if (isVisible) progressDialog?.show() else progressDialog?.dismiss()
    }


    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_info)
        addCardNameEditText.setText(card!!.name)
        addCardNameEditText.onKey { view, i, keyEvent ->
            if ((keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE)
                    && keyEvent?.action == KeyEvent.ACTION_UP) {
                presenter.onSaveClick(card!!, addCardNameEditText.text.toString())
            }
            return@onKey false
        }
    }


}