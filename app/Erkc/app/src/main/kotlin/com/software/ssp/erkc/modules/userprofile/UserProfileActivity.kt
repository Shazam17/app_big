package com.software.ssp.erkc.modules.userprofile

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.hideKeyboard
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.fastauth.changepin.ChangePinActivity
import com.software.ssp.erkc.modules.fastauth.createpin.CreatePinActivity
import com.software.ssp.erkc.modules.fastauth.deletepin.DeletePinActivity
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onEditorAction
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject


class UserProfileActivity : MvpActivity(), IUserProfileView {

    @Inject lateinit var presenter: IUserProfilePresenter

    var login = ""

    companion object {
        val USER_PROFILE_REQUEST_CODE = 23512
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
        setResult(Activity.RESULT_OK)
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

    fun getPin(): String {
        if(!login.isEmpty()) {
            val prefs = this.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
            return prefs.getString(EnterPinActivity.KEY_PIN + login, "")
        } else {
            return ""
        }
    }

    override fun navigateToPinCreateScreen() {
        startActivity<CreatePinActivity>()
    }

    override fun navigateToPinDeleteScreen() {
        startActivity<DeletePinActivity>()
    }

    override fun navigateToPinChangeScreen() {
        startActivity<ChangePinActivity>()
    }

    override fun showPinSuggestDialog() {
        val prefs = getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
        if (getPin().isEmpty() && prefs.getBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + login, true)) {
            prefs.edit().putBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + login, false).apply()
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.pin_suggest_dialog_message)
                .setPositiveButton(R.string.splash_offline_dialog_positive, DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this, CreatePinActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton(R.string.splash_offline_dialog_negative, DialogInterface.OnClickListener { dialog, id ->
                    presenter.onPinReject()
                })
                .setNeutralButton(R.string.splash_offline_dialog_neutral, DialogInterface.OnClickListener { dialog, id ->
                    presenter.onPinReject()
                    prefs.edit().putBoolean(EnterPinActivity.SHOULD_SUGGEST_SET_PIN + login, true).apply()
                })
                .setCancelable(false)
            builder.create().show()
        }
    }

    override fun setUserLogin(login: String) {
        this.login = login
        ErkcApplication.login = login
    }

    override fun onResume() {
        super.onResume()

        presenter.resumed()
    }

    override fun showPinStatus() {
        if (!getPin().isEmpty()) {
            pinChangeButton.visibility = View.VISIBLE
            pinDeleteButton.visibility = View.VISIBLE
            pinCreateButton.visibility = View.GONE
        } else {
            pinChangeButton.visibility = View.GONE
            pinDeleteButton.visibility = View.GONE
            pinCreateButton.visibility = View.VISIBLE
        }

        pinChangeButton.onClick { presenter.onPinChangeClick() }
        pinDeleteButton.onClick { presenter.onPinDeleteClick() }
        pinCreateButton.onClick { presenter.onPinCreateClick() }
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
