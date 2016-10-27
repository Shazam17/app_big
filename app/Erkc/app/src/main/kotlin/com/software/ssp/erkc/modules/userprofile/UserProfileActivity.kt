package com.software.ssp.erkc.modules.userprofile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.User
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.textChangedListener
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
            close()
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
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun close() {
        finish()
    }

    override fun showUserInfo(user: User) {
        userProfileNameEditText.setText(user.name)
        userProfileEmailEditText.setText(user.email)
    }

    override fun showErrorNameMessage(resId: Int) {
        userProfileNameLayout.error = getString(resId)
    }

    override fun showErrorEmailMessage(resId: Int) {
        userProfileEmailLayout.error = getString(resId)
    }

    override fun showErrorPasswordMessage(resId: Int) {
        userProfilePasswordLayout.error = getString(resId)
        userProfileRePasswordLayout.error = getString(resId)
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userProfileNameEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                userProfileNameLayout.error = null
            }
        }

        userProfileEmailEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                userProfileEmailLayout.error = null
            }
        }

        userProfilePasswordEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                userProfilePasswordLayout.error = null
            }
        }

        userProfileRePasswordEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                userProfileRePasswordLayout.error = null
            }
        }

        userProfileRePasswordEditText.onEditorAction { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                hiddenViewForFocus.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        userProfileSaveButton.onClick {
            presenter.onSaveButtonClick(
                    userProfileNameEditText.text.toString(),
                    userProfileEmailEditText.text.toString(),
                    userProfilePasswordEditText.text.toString(),
                    userProfileRePasswordEditText.text.toString()
            )
        }
    }
}
