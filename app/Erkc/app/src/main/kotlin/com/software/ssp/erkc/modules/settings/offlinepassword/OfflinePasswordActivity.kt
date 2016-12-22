package com.software.ssp.erkc.modules.settings.offlinepassword

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_offline_password.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject


class OfflinePasswordActivity : MvpActivity(), IOfflinePasswordView {

    @Inject lateinit var presenter: IOfflinePasswordPresenter

    companion object {
        const val REQUEST_CODE = 4431
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_password)

        initViews()
        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerOfflinePasswordComponent.builder()
                .appComponent(appComponent)
                .offlinePasswordModule(OfflinePasswordModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun didSavedOfflinePassword() {
        setResult(Activity.RESULT_OK)
    }

    override fun close() {
        finish()
    }

    override fun showPasswordError(errorResId: Int) {
        offlinePasswordInputLayout.error = getString(errorResId)
    }

    override fun showRePasswordError(errorResId: Int) {
        offlineRePasswordInputLayout.error = getString(errorResId)
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        supportActionBar?.elevation = 0f

        offlinePasswordEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k ->
                offlinePasswordInputLayout.error = ""
                presenter.onPasswordChange(charSequence.toString())
            }
        }

        offlineRePasswordEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k ->
                offlineRePasswordInputLayout.error = ""
                presenter.onConfirmPasswordChange(charSequence.toString())
            }
        }

        offlinePasswordSaveButton.onClick { presenter.onSaveButtonClick() }
    }
}
