package com.software.ssp.erkc.modules.photoservice

import android.app.IntentService
import android.content.Intent
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PhotoSendingService : IntentService("photo_sending_service") {

    companion object {
        val EXTRA_TASK = "EXTRA_TASK"
    }

    @Inject lateinit var ipuRepository: IpuRepository


    override fun onHandleIntent(intent: Intent?) {
        DaggerAComponent.builder()
                .appComponent((this.application as ErkcApplication).appComponent)
                .build()
                .inject(this)

        val task = intent?.getSerializableExtra(EXTRA_TASK) as PhotoService.Task

        Timber.d("SENDING PHOTO: $task")
        ipuRepository.sendImageByMeters(
                task.code,
                task.id,
                task.value,
                File(task.path)
        )
                .toBlocking() //work on that thread
                .subscribe(
                {
                    PhotoService.taskCompleted(this, task)
                    Timber.d("photo sent")
                },
                { error -> Timber.d(error.localizedMessage) }
        )
    }
}