package com.software.ssp.erkc.modules.userprofile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.onEditorAction
import javax.inject.Inject


class UserProfileActivity : MvpActivity(), IUserProfileView {

    @Inject lateinit var presenter: IUserProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

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

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerUserProfileComponent.builder()
                .appComponent(appComponent)
                .userProfileModule(UserProfileModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        userProfileSaveButton.isEnabled = !isVisible
        userProfileSaveButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToMain() {
        finish()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userProfilePasswordConfirmEditText.onEditorAction { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                hiddenViewForFocus.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }
}
