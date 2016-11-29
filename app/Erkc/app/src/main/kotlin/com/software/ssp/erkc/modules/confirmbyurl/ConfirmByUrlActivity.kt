package com.software.ssp.erkc.modules.confirmbyurl

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_change_status_card.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class ConfirmByUrlActivity : MvpActivity(), IConfirmByUrlView {

    @Inject lateinit var presenter: IConfirmByUrlPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_status_card)
        initViews()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerConfirmByUrlComponent.builder()
                .appComponent(appComponent)
                .confirmByUrlModule(ConfirmByUrlModule(this))
                .build()
                .inject(this)

    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToResult() {
        finish()
    }

    override fun showDoneButton() {
        changeStatusCardDoneButtonWrapper.visibility = View.VISIBLE
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
        val title = intent.getIntExtra(Constants.KEY_URL_ACTIVITY_TITLE, -1)
        if (title != -1) {
            supportActionBar?.setTitle(title)
        }
        setupWebView()
        changeStatusCardDoneButton.onClick {
            presenter.onDoneClick()
        }
        changeStatusCardWebView.loadUrl(intent.getStringExtra(Constants.KEY_URL))
    }

    private fun setupWebView() {
        changeStatusCardWebView.settings.useWideViewPort = true
        changeStatusCardWebView.settings.loadWithOverviewMode = true
        changeStatusCardWebView.settings.javaScriptEnabled = true
        changeStatusCardWebView.settings.builtInZoomControls = true
        changeStatusCardWebView.settings.displayZoomControls = false
        changeStatusCardWebView.setWebViewClient(object : WebViewClient() {
            var startLoading = false
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("service.gazprombank.ru")) { //для проверки что не ушли на левую страницу (клик по лэйблу "газпромбанк" например)
                    view.loadUrl(url)
                    startLoading = true
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (startLoading) {
                    presenter.onBankConfirm()
                }
            }
        })
    }

}