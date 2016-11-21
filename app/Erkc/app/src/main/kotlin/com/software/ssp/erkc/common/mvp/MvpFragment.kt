package com.software.ssp.erkc.common.mvp

import android.app.Fragment
import android.os.Bundle
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.di.AppComponent
import org.jetbrains.anko.toast


abstract class MvpFragment : Fragment(), IView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(application().appComponent)
    }

    override fun onDestroy() {
        beforeDestroy()
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

}