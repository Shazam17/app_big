package com.software.ssp.erkc.modules.setupfastauth

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.hanks.passcodeview.PasscodeView
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_setup_fast_auth.*
import org.jetbrains.anko.toast
import javax.inject.Inject
import com.securepreferences.SecurePreferences
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import org.jetbrains.anko.intentFor


class SetupFastAuthActivity : MvpActivity(), ISetupFastAuthView {

    @Inject lateinit var presenter: ISetupFastAuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_fast_auth)

        initViews()

        presenter.onViewAttached()
    }

    override fun onBackPressed() {

    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSetupFastAuthComponent.builder()
                .appComponent(appComponent)
                .setupFastAuthModule(SetupFastAuthModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    private fun initViews() {
        val context = this
        passcodeView.listener = object : PasscodeView.PasscodeViewListener {
            override fun onFail() {

            }
            override fun onSuccess(number: String) {
                val securePrefsEditor = SecurePreferences(context, "", getString(R.string.secure_prefs_filename)).edit()
                securePrefsEditor.putString(getString(R.string.user_pin_key), number).commit();
                securePrefsEditor.putInt(getString(R.string.fail_attemps_count_key), 0)
                securePrefsEditor.commit()
                presenter.saveApiToken()
                val mFingerprintIdentify = FingerprintIdentify(context);
                if (mFingerprintIdentify.isFingerprintEnable()
                        && mFingerprintIdentify.isHardwareEnable()
                        && mFingerprintIdentify.isRegisteredFingerprint()) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(R.string.fingerprint_suggest_dialog_message)
                            .setPositiveButton(R.string.splash_offline_dialog_positive, DialogInterface.OnClickListener { dialog, id ->
                                securePrefsEditor.putBoolean(getString(R.string.use_fingerprint_key), true).commit();
                                securePrefsEditor.commit()
                                finish()
                            })
                            .setNegativeButton(R.string.splash_offline_dialog_negative, DialogInterface.OnClickListener { dialog, id ->
                                securePrefsEditor.putBoolean(getString(R.string.use_fingerprint_key), false).commit();
                                securePrefsEditor.commit()
                                finish()
                            })
                    builder.create().show()
                } else {
                    finish()
                }
            }
        }
    }
}
