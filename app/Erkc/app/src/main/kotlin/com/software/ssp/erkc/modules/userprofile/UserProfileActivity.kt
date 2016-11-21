package com.software.ssp.erkc.modules.userprofile

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject


class UserProfileActivity : MvpActivity(), IUserProfileView {

    @Inject lateinit var presenter: IUserProfilePresenter

    companion object {
        val USER_PROFILE_TAG = 23512

        val USER_PROFILE_UPDATED = 23513
    }

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
        saveButton.isEnabled = !isVisible
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun didUserProfileUpdated() {
        setResult(USER_PROFILE_UPDATED)
    }

    override fun close() {
        finish()
    }

    override fun showUserInfo(user: RealmUser) {
        nameEditText.setText(user.name)
        emailEditText.setText(user.email)
    }

    override fun showErrorNameMessage(resId: Int) {
        nameInputLayout.error = getString(resId)
    }

    override fun showErrorEmailMessage(resId: Int) {
        emailInputLayout.error = getString(resId)
    }

    override fun showErrorPasswordMessage(resId: Int) {
        passwordInputLayout.error = getString(resId)
        rePasswordInputLayout.error = getString(resId)
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                nameInputLayout.error = null
            }
        }

        emailEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                emailInputLayout.error = null
            }
        }

        passwordEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                passwordInputLayout.error = null
            }
        }

        rePasswordEditText.textChangedListener {
            onTextChanged { charSequence, begin, end, count ->
                rePasswordInputLayout.error = null
            }
        }

        rePasswordEditText.onEditorAction { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                rootLayout.requestFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        saveButton.onClick {
            presenter.onSaveButtonClick(
                    nameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    rePasswordEditText.text.toString()
            )
        }
    }
}
