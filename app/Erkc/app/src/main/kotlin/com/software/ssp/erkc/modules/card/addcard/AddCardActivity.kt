package com.software.ssp.erkc.modules.card.addcard

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.confirmbyurl.ConfirmByUrlActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class AddCardActivity : MvpActivity(), IAddCardView {

    @Inject lateinit var presenter: IAddCardPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
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
        startActivity<ConfirmByUrlActivity>(Constants.KEY_URL to url, Constants.KEY_URL_ACTIVITY_TITLE to R.string.add_card_title)
        finish()
    }

    override fun close() {
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

    override fun setPending(isPending: Boolean) {
        progressBar.visibility = if(isPending) View.VISIBLE else View.GONE
        addCardNameEditText.enabled = !isPending
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)

        addCardNameEditText.onEditorAction { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.add_card_info)
                        .setNeutralButton(R.string.add_card_info_understand, DialogInterface.OnClickListener { dialog, id ->
                            presenter.onCreateCardClick(addCardNameEditText.text.toString())
                        })
                builder.create().show()
                true
            } else {
                false
            }
        }
    }
}
