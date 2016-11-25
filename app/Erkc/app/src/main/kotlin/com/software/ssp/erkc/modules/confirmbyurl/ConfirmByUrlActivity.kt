package com.software.ssp.erkc.modules.confirmbyurl


import android.app.Activity
import android.content.Intent
import android.net.UrlQuerySanitizer
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
    private var successUrl: String? = null
    private var failUrl: String? = null

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
        confirmByUrlDoneButtonWrapper.visibility = View.VISIBLE
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
        confirmByUrlDoneButton.onClick {
            presenter.onDoneClick()
        }
        val url = intent.getStringExtra(Constants.KEY_URL)
        val sanitizer = UrlQuerySanitizer(url)
        if (sanitizer.hasParameter("back_url_s") && sanitizer.hasParameter("back_url_f")) {
            successUrl = sanitizer.getValue("back_url_s")
            failUrl = sanitizer.getValue("back_url_f")
        }
        confirmByUrlWebView.loadUrl(url)

    }

    private fun setupWebView() {
        confirmByUrlWebView.settings.useWideViewPort = true
        confirmByUrlWebView.settings.loadWithOverviewMode = true
        confirmByUrlWebView.settings.javaScriptEnabled = true
        confirmByUrlWebView.settings.builtInZoomControls = true
        confirmByUrlWebView.settings.displayZoomControls = false
        confirmByUrlWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                when {
                    url.contains("gazprombank.ru") -> {
                        view.loadUrl(url)
                    }
                    url == successUrl -> {
                        val intent = Intent()
                        intent.putExtra(Constants.KEY_URL_RESULT, true)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    url == failUrl -> {
                        val intent = Intent()
                        intent.putExtra(Constants.KEY_URL_RESULT, false)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null && url.contains("payment-params")) {
                    presenter.onBankConfirm()
                }
            }
        })
    }
}
