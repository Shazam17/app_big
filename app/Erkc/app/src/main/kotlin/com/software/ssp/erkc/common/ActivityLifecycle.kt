package com.software.ssp.erkc.common

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author Alexander Popov on 08/02/2017.
 */
// Жизненный цикл, нужен для определения стартанули мы активити находясь в background или нет
object ActivityLifecycle : Application.ActivityLifecycleCallbacks {

    var isForeground = false
        private set

    val isBackground: Boolean
        get() = !isForeground


    override fun onActivityPaused(p0: Activity?) {
    }

    override fun onActivityStarted(p0: Activity?) {
    }

    override fun onActivityDestroyed(p0: Activity?) {
        isForeground = false
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(p0: Activity?) {
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityResumed(p0: Activity?) {
        isForeground = true
    }
}
