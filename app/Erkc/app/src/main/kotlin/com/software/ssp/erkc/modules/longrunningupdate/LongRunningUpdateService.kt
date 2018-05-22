package com.software.ssp.erkc.modules.longrunningupdate

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.support.annotation.MainThread
import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.common.OpenCardsEvent
import com.software.ssp.erkc.common.ServiceIconsSaved
import com.software.ssp.erkc.data.realm.models.CURRENT_VERSION
import com.software.ssp.erkc.data.realm.models.Migrations
import com.software.ssp.erkc.data.rest.repositories.DictionaryRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import io.realm.Realm
import io.realm.RealmConfiguration
import rx.lang.kotlin.onError
import timber.log.Timber
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LongRunningUpdateService : Service() {

    //run all code in caller thread
    @Target(AnnotationTarget.FUNCTION)
    annotation class Blocking


    @Inject lateinit var dictionaryRepository: DictionaryRepository
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var eventBus: Relay<Any, Any>

    //list of tasks to execute
    private val tasks: Array<BlockingTask> = arrayOf(
            BlockingTask(this::updateDictionaryOpcodes, 1, TimeUnit.SECONDS), //TODO: DAYS in release
            BlockingTask(this::updateTest, 1, TimeUnit.MINUTES)
    )

    companion object {
        var executionThread: Thread? = null
    }

    override fun onBind(p0: Intent?) = Binder()

    override fun onCreate() {
        super.onCreate()
        DaggerAComponent.builder()
                .appComponent((application as ErkcApplication).appComponent)
                .build()
                .inject(this)
    }

    private class BlockingTask(
        val updateOp: ()->Boolean,
        val updatePeriod: Long,
        val updateTimeUnit: TimeUnit
    )
    {
        private val SHARED_PREFS = "SHARED_PREFS_BLOCKING_TASKS"
        private val KEY_LAST_UPDATE_FOR_ = "KEY_LAST_UPDATE_FOR_"

        private fun key() = "$KEY_LAST_UPDATE_FOR_${updateOp}"

        fun runIfNeeded(context: Context) {
            val real_period_ms = updateTimeUnit.toMillis(updatePeriod)
            val prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            if (System.currentTimeMillis() - prefs.getLong(key(), 0) > real_period_ms) {
                if (updateOp()) {
                    prefs.edit().putLong(key(), System.currentTimeMillis()).commit()
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //test()
        processTasks()
        return START_NOT_STICKY
    }

    private fun test() {
        val s = realmRepository.fetchOneServiceType()
        if (s != null)
            Timber.d("one service type: {${s.id}, ${s.name}, ${s.service_code}}")
        else
            Timber.d("one service type: NULL")
    }

    private fun processTasks() {
        if (executionThread != null) {
            executionThread?.interrupt()
            executionThread = null
        }
        executionThread = Thread(
                {
                    for (task in tasks) {
                        task.runIfNeeded(this)
                        if (Thread.interrupted()) break;
                    }
                    stopSelf()
                })
        executionThread?.start()
    }

    @Blocking
    private fun updateDictionaryOpcodes(): Boolean {
        val realmConfiguration = RealmConfiguration
                .Builder()
                .schemaVersion(CURRENT_VERSION)
                //.migration(Migrations())
                .deleteRealmIfMigrationNeeded()
                .build()
        val realm = Realm.getInstance(realmConfiguration)
        val realmRepository = RealmRepository(realm)

        try {
            var succeed = true
            val list = dictionaryRepository.fetchServiceTypes()
                    .onError { succeed = false }
                    .toBlocking()
                    .first()
            list?.let {
//                realmRepository.saveServiceTypes(list)
//                        .toBlocking()
//                        .subscribe()
                if (realmRepository.saveServiceTypes(this, list))
                    eventBus.call(ServiceIconsSaved())
                else
                    succeed = false
            }

            return succeed
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }

    @Blocking
    private fun updateTest(): Boolean {
        Timber.d("test update")
        return true
    }
}