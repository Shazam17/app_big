package com.software.ssp.erkc.common.mvp

import android.app.Dialog
import android.app.Fragment
import android.os.Bundle
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.di.AppComponent
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast


abstract class MvpFragment : Fragment(), IView {

    private var progressDialog : Dialog?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(application().appComponent)
    }

    override fun onDestroy() {
        beforeDestroy()
        progressDialog?.dismiss()
        super.onDestroy()
    }

    abstract fun injectDependencies(appComponent: AppComponent)

    abstract fun beforeDestroy()

    override fun application(): ErkcApplication {
        return activity.application as ErkcApplication
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun showMessage(messageResId: Int) {
        toast(messageResId)
    }
    override fun setProgressVisibility(isVisible: Boolean) {
        if (progressDialog== null) {
            progressDialog = indeterminateProgressDialog(R.string.data_loading)
            progressDialog!!.setCanceledOnTouchOutside(false)
        }
        if (isVisible) progressDialog?.show() else progressDialog?.dismiss()
    }

}