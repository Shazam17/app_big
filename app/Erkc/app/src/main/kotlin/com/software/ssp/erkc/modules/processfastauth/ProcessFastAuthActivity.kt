package com.software.ssp.erkc.modules.processfastauth

import android.content.Context
import android.os.Bundle
import com.hanks.passcodeview.PasscodeView
import com.securepreferences.SecurePreferences
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.drawer.DrawerActivity
import kotlinx.android.synthetic.main.activity_process_fast_auth.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.annotation.NonNull
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint
import org.jetbrains.anko.toast
import java.lang.reflect.AccessibleObject.setAccessible
import junit.framework.Test
import java.lang.reflect.AccessibleObject.setAccessible






class ProcessFastAuthActivity : MvpActivity(), IProcessFastAuthView {

    @Inject lateinit var presenter: IProcessFastAuthPresenter
    var isAppOnPause = false
    var fingerprintIdentify: FingerprintIdentify? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_fast_auth)

        initViews()

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerProcessFastAuthComponent.builder()
                .appComponent(appComponent)
                .processFastAuthModule(ProcessFastAuthModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        fingerprintIdentify?.cancelIdentify()
        presenter.dropView()
    }

    override fun onPause() {
        super.onPause()
        fingerprintIdentify?.cancelIdentify()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.fast_auth_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_user -> {
                presenter.onChangeUserClick()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
    override fun onResume() {
        super.onResume()
        val securePrefs = SecurePreferences(this, "", getString(R.string.secure_prefs_filename))
        val useFingerprint = securePrefs.getBoolean(getString(R.string.use_fingerprint_key), false)
        val mFingerprintIdentify = FingerprintIdentify(this);
        fingerprintIdentify = mFingerprintIdentify
        if (useFingerprint
                && mFingerprintIdentify.isFingerprintEnable()
                && mFingerprintIdentify.isHardwareEnable()
                && mFingerprintIdentify.isRegisteredFingerprint())
        {
            mFingerprintIdentify.startIdentify(3, object : BaseFingerprint.FingerprintIdentifyListener {
                override fun onSucceed() {
                    passcodeView.runOkAnimation()
                }

                override fun onNotMatch(availableTimes: Int) {
                    toast(R.string.fingerprint_not_match)
                }

                override fun onFailed(isDeviceLocked: Boolean) {
                    toast(R.string.fingerprint_failed)
                }

                override fun onStartFailedByDeviceLocked() {

                }
            })
        }
    }

    fun getPin(): String {
        val securePrefs = SecurePreferences(this, "", getString(R.string.secure_prefs_filename))
        return securePrefs.getString(getString(R.string.user_pin_key), "")
    }

    override fun setPin(pin: String) {
        val securePrefsEditor = SecurePreferences(this, "", getString(R.string.secure_prefs_filename)).edit()
        securePrefsEditor.putString(getString(R.string.user_pin_key), "")
        securePrefsEditor.commit()
    }

    fun getFailAttempsCount(): Int {
        val securePrefs = SecurePreferences(this, "", getString(R.string.secure_prefs_filename))
        return securePrefs.getInt(getString(R.string.fail_attemps_count_key), 0)
    }

    fun increaseFailAttempsCount() {
        val newFailAttempsCount = getFailAttempsCount() + 1
        val securePrefsEditor = SecurePreferences(this, "", getString(R.string.secure_prefs_filename)).edit()
        if (newFailAttempsCount  >= 3) {
            //showMessage(R.string.fast_auth_pin_attemp_error_text)
            securePrefsEditor.putInt(getString(R.string.fail_attemps_count_key), 0)
            securePrefsEditor.putBoolean(getString(R.string.fail_attemps_message_key), true)
            securePrefsEditor.commit()
            presenter.onChangeUserClick()
            return
        }
        securePrefsEditor.putInt(getString(R.string.fail_attemps_count_key), newFailAttempsCount)
        securePrefsEditor.commit()
    }

    override fun finishFastAuthActivity() {
        finish()
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
    private fun initViews() {
        val pin = getPin()
        val field = PasscodeView::class.java.getDeclaredField("secondInput")
        field.isAccessible = true
        field.setBoolean(passcodeView, true)
        val securePrefsEditor = SecurePreferences(this, "", getString(R.string.secure_prefs_filename)).edit()
        passcodeView
                .setPasscodeLength(pin.length)
                .setLocalPasscode(pin)
                .listener = object : PasscodeView.PasscodeViewListener {
            override fun onFail() {
                increaseFailAttempsCount()
            }
            override fun onSuccess(number: String) {
                securePrefsEditor.putInt(getString(R.string.fail_attemps_count_key), 0)
                securePrefsEditor.commit()
                finish()
                startActivity<DrawerActivity>()
            }
        }

    }
}
