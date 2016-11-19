package com.software.ssp.erkc.modules.card.addcard

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.confirmbyurl.ConfirmByUrlActivity
import kotlinx.android.synthetic.main.cardname_layout.*
import org.jetbrains.anko.onKey
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class AddCardActivity : MvpActivity(), IAddCardView {

    @Inject lateinit var presenter: IAddCardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cardname_layout)
        initViews()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerAddCardComponent.builder()
                .appComponent(appComponent)
                .addCardModule(AddCardModule(this))
                .build()
                .inject(this)

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToUrl(url: String) {
        finish()
        startActivity<ConfirmByUrlActivity>(Constants.KEY_URL to url, Constants.KEY_URL_ACTIVITY_TITLE to R.string.add_card_title)
    }

    override fun navigateToCards() {
        finish()
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

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        addCardNameEditText.onKey { view, i, keyEvent ->
            if ((keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE)
                    && keyEvent?.action == KeyEvent.ACTION_UP) {
                presenter.onNameConfirm(addCardNameEditText.text.toString())
            }
            return@onKey false
        }
    }
}