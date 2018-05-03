package com.software.ssp.erkc.modules.photoservice

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import timber.log.Timber
import java.io.File
import java.io.Serializable

class PhotoService : Service() {

    class Task(
            val code: String, //barcode of receipt
            val path: String, //full path to image
            val signature: String,
            val id: String, //ipu id (from backend)
            val value: String
    ) : Serializable

    companion object {
        val EXT_FILE = ".jpg"
        val ID_PREFIX = "ipu_"
        val CODE_PREFIX = "code_"
        val VALUE_PREFIX = "value_"
        val PREFERENCES = "PREFERENCES_phs"

        fun file(context: Context, signature: String): File {
            val tasks = File(context.filesDir, "tasks")
            tasks.mkdirs()
            return File(tasks, "$signature$EXT_FILE")
        }

        fun addTask(context: Context, full_path: String, ipu_id: String, ipu_value: String, code: String) {
            Timber.d("add task: {ipu_num: $ipu_id} {ipu_value: $ipu_value} path = $full_path")

            val signature = System.currentTimeMillis()
            val file = File(full_path)

            file.copyTo(file(context, "$signature"))
            val prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            prefs.edit().putStringSet("$signature", setOf(
                    "$ID_PREFIX$ipu_id",
                    "$VALUE_PREFIX$ipu_value",
                    "$CODE_PREFIX$code"
            )).apply()
        }


        fun tasks(context: Context): List<Task> {
            val prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            val res = ArrayList<Task>()

            for (signature in prefs.all.keys) {
                val set = prefs.getStringSet(signature, emptySet())
                val id = set.filter{ it.startsWith(ID_PREFIX) }.first().substring(ID_PREFIX.length)
                val code = set.filter{ it.startsWith(CODE_PREFIX) }.first().substring(CODE_PREFIX.length)
                val value = set.filter{ it.startsWith(VALUE_PREFIX) }.first().substring(VALUE_PREFIX.length)
                res.add(Task(
                        code,
                        file(context, signature).absolutePath,
                        signature,
                        id, value))

            }
            return res
        }

        fun taskCompleted(context: Context, task: PhotoService.Task) {
            File(task.path).delete()
            val prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            prefs.edit().remove(task.signature).apply()
            Timber.d("TASK COMPLETED: ${task.signature}")
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //testTask()
        processTasks()
        return START_STICKY
    }

    private fun testTask() {
        Timber.d("PROCESSING TEST TASK:")
        processTask(Task(
                code = "3523740000873",
                path = "/data/data/com.software.ssp.erkc/files/tasks/12345.jpg",
                signature = "12345",
                id = "12384",
                value = "10"
        ))
    }

    private fun processTasks() {
        val tasks = tasks(this)
        for (task in tasks) processTask(task)
    }

    private fun processTask(task: Task) {
        val intent = Intent(this, PhotoSendingService::class.java)
        intent.putExtra(PhotoSendingService.EXTRA_TASK, task)
        startService(intent)
    }

}