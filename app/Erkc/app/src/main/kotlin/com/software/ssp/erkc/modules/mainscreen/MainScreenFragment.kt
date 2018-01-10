package com.software.ssp.erkc.modules.mainscreen

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen.NonAuthedMainScreenFragment
import com.software.ssp.erkc.modules.mainscreen.receiptlist.ReceiptListFragment
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import javax.inject.Inject
import android.content.DialogInterface
import android.content.Intent
import com.securepreferences.SecurePreferences
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast


class MainScreenFragment : MvpFragment(), IMainScreenView {

    @Inject lateinit var presenter: IMainScreenPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_with_container, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerMainScreenComponent.builder()
                .appComponent(appComponent)
                .mainScreenModule(MainScreenModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showNonAuthedScreen() {
        showFragment(NonAuthedMainScreenFragment(), R.string.main_screen_non_authed_title)
    }

    override fun showAddReceiptScreen() {
        showFragment(NewReceiptFragment(), R.string.main_screen_authed_title)
    }

    override fun showReceiptListScreen() {
        showFragment(ReceiptListFragment(), R.string.main_screen_authed_title)
    }

    override fun showProcessFastAuthScreen() {
        val intent = Intent(this.activity, EnterPinActivity::class.java)
        startActivity(intent)
//        startActivity(intentFor<ProcessFastAuthActivity>())
    }

    override fun showPinSuggestDialog() {
        val prefs = this.activity.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
        val pin = prefs.getString(EnterPinActivity.KEY_PIN, "")
        if (pin.isNullOrEmpty()) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(R.string.pin_suggest_dialog_message)
                    .setPositiveButton(R.string.splash_offline_dialog_positive, DialogInterface.OnClickListener { dialog, id ->
                        val intent = Intent(this.activity, EnterPinActivity::class.java)
                        startActivity(intent)
                    })
                    .setNegativeButton(R.string.splash_offline_dialog_negative, DialogInterface.OnClickListener { dialog, id ->
                        presenter.onPinReject()
                    })
            builder.create().show()
        }
    }

    private fun showFragment(fragment: Fragment, titleResId: Int) {
        (activity as AppCompatActivity).supportActionBar?.title = getString(titleResId)
        childFragmentManager.beginTransaction()
                .replace(R.id.containerLayout, fragment)
                .commit()
    }
}
