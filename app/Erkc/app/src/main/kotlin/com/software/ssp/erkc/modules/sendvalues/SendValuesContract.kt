package com.software.ssp.erkc.modules.sendvalues

import android.graphics.Bitmap
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.rest.models.Receipt
import io.fotoapparat.result.PhotoResult
import java.io.File

/**
 * @author Alexander Popov on 26/10/2016.
 */
interface ISendValuesView : IView {
    fun close()
    fun showIpu(ipu: RealmIpu)
    fun setProgressVisibility(isVisible: Boolean)
    fun showInfoDialog(resId: Int)
    fun showAddIPU()
    fun navigateToUserIPU(number: String? = null)
    fun clearIPUs()
    fun showCameraView()
    fun hideFAB()
    fun showFAB()
    fun nextCaptureFragment(file: File)
    fun tmpPhotoSaved(file: File)
    fun photoError()
    fun startPhotoSendingService()
}

interface ISendValuesPresenter : IPresenter<ISendValuesView> {
    var receiptId : String?
    var receipt : Receipt?
    var fromTransaction: Boolean
    fun onSendValuesClick()
    fun addIPUClicked()
    fun editIPUClicked(ipu_value: RealmIpuValue)
    fun onResume()
    fun addPhotoClick()
    fun cameraShot(takePicture: PhotoResult?, pic_width: Int, pic_height: Int)
    //fun cameraShot(bmp: Bitmap, pic_width: Int, pic_height: Int)
    fun goodShot()
    fun badShot()
    fun symbolAdded(ipu_number: String, text: String)
    fun currentPhotoIdxChanged(idx: Int)
}