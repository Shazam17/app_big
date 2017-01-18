package com.software.ssp.erkc.common.mvp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.di.AppComponent
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

abstract class MvpActivity : AppCompatActivity(), IView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveDependencies(application().appComponent)
    }

    override fun onDestroy() {
        beforeDestroy()
        super.onDestroy()
    }

    abstract fun resolveDependencies(appComponent: AppComponent)

    abstract fun beforeDestroy()

    override fun application(): ErkcApplication {
        return application as ErkcApplication
    }

    override fun showMessage(message: String) {
        longToast(message)
    }

    override fun showMessage(messageResId: Int) {
        longToast(messageResId)
    }
}
