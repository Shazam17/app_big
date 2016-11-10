package com.software.ssp.erkc.modules.card.addcard

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_add_cards.*
import kotlinx.android.synthetic.main.cardname_layout.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onKey
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class AddCardActivity : MvpActivity(), IAddCardView {

    @Inject lateinit var presenter: IAddCardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cards)
        initViews()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerAddCardComponent.builder()
                .appComponent(appComponent)
                .addCardModule(AddCardModule(this))
                .build()
                .inject(this)

    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToUrl(url: String) {
        addCardWebView.loadUrl(url)
        addCardViewFlipper.showNext()
    }

    override fun navigateToResults(result: Boolean) {
        addCardDoneButtonWrapper.visibility = View.VISIBLE
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
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_info)
        setupWebView()
        addCardDoneButton.onClick {
            presenter.onDoneClick()
        }
        addCardNameEditText.onKey { view, i, keyEvent ->
            if ((keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE)
                    && keyEvent?.action == KeyEvent.ACTION_UP) {
                presenter.onNameConfirm(addCardNameEditText.text.toString())
            }
            return@onKey false
        }
    }

    private fun setupWebView() {
        addCardWebView.settings.useWideViewPort = true
        addCardWebView.settings.loadWithOverviewMode = true
        addCardWebView.settings.javaScriptEnabled = true
        addCardWebView.settings.builtInZoomControls = true
        addCardWebView.settings.displayZoomControls = false
        addCardWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("service.gazprombank.ru")) { //для проверки что не ушли на левую страницу (клик по лэйблу "газпромбанк" например)
                    view.loadUrl(url)
                    presenter.onBankConfirm()
                }
                return true
            }
        })
    }

}