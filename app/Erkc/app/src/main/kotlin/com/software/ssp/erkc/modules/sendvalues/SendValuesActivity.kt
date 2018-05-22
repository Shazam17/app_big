package com.software.ssp.erkc.modules.sendvalues

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.Manifest
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extractFromBundle
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.extensions.toString
import com.tbruyelle.rxpermissions.RxPermissions
import com.software.ssp.erkc.modules.useripu.Activity as UserIPUActivity
import kotlinx.android.synthetic.main.activity_send_values.*
import kotlinx.android.synthetic.main.sendparameters_ipu_layout.view.*
import org.jetbrains.anko.*
import javax.inject.Inject
import android.content.pm.PackageManager
import android.view.View.*
import com.bumptech.glide.Glide
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.Logger
import timber.log.Timber
import com.software.ssp.erkc.modules.sendvalues.CameraMode.*
import java.io.File
import android.graphics.Bitmap
import android.graphics.Canvas
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.software.ssp.erkc.extensions.hide
import com.software.ssp.erkc.extensions.show
import com.software.ssp.erkc.modules.photoservice.PhotoService
import io.fotoapparat.parameter.ScaleType
import org.jetbrains.anko.support.v4.onPageChangeListener


enum class CameraMode{CM_OFF, CM_INITIALIZING, CM_READY, CM_SAVING, CM_SHOT_COMMIT}

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesActivity : MvpActivity(), ISendValuesView {

    @Inject lateinit var presenter: ISendValuesPresenter

    private val receipt: Receipt? by extras()
    private val receiptId: String? by extras(Constants.KEY_RECEIPT)
    private var fromTransaction: Boolean by extras(Constants.KEY_FROM_TRANSACTION, defaultValue = false)

    var fotoapparat: Fotoapparat? = null

    private var camera_mode = CM_OFF
    private var photos = ArrayList<File>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_values)
        initViews()

        presenter.fromTransaction = fromTransaction
        presenter.receipt = receipt
        presenter.receiptId = receiptId

        presenter.onViewAttached()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerSendValuesComponent.builder()
                .appComponent(appComponent)
                .sendValuesModule(SendValuesModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showIpu(ipu: RealmIpu) {
        sendValuesBarcode.text = ipu.receipt?.barcode
        sendValuesAddress.text = ipu.receipt?.address
        val newIpus = ipu.ipuValues.filter { !it.isSent }
        sendValuesDebts.text = "${ipu.receipt?.amount} р. (${newIpus.first().period?.toString(Constants.PERIOD_DATE_FORMAT_UI)})"
        val layoutInflater = LayoutInflater.from(this)
        newIpus.forEach {
            val ipu_value = it
            val ipuLayout = layoutInflater.inflate(R.layout.sendparameters_ipu_layout, parametersContainer, false)
            ipuLayout.ipuLocation.text = it.installPlace
            ipuLayout.ipuValueWrapper.hint = getString(R.string.send_values_ipu_hint).format(it.shortName, it.number)
            ipuLayout.ipuValue.textChangedListener {
                var was_text: String = ""
                beforeTextChanged{
                    text,a,b,c ->
                    was_text = text.toString()
                }
                afterTextChanged {
                    text ->
                    it.value = text.toString()
                    if (text != null) {
                        if (text.length > was_text.length) presenter.symbolAdded(it.id, text.toString())
                    }
                }
            }
            ipuLayout.ipuValue.setText(it.value)
            ipuLayout.editUserIPU.visibility = if (it.userRegistered) View.VISIBLE else View.GONE
            ipuLayout.editUserIPU.onClick { presenter.editIPUClicked(ipu_value) }
            parametersContainer.addView(ipuLayout)
        }
    }

    override fun clearIPUs() {
        parametersContainer.removeAllViews()
    }

    override fun showInfoDialog(resId: Int) {
        materialDialog {
            content(resId)
            cancelable(false)
            positiveText(R.string.send_values_dialog_button)
            onPositive { dialog, which -> close() }
        }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun close() {
        finish()
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        sendValuesButton.enabled = !isVisible
        sendValuesProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        sendValuesButton.onClick { presenter.onSendValuesClick() }
        add_photo.onClick{
            findViewById(R.id.no_image_hint).visibility = GONE
            presenter.addPhotoClick()
        }
        camera_shot.onClick{ if (camera_mode == CM_READY) cameraShot() }
        camera_done.onClick{ if (camera_mode == CM_SHOT_COMMIT) presenter.goodShot() }
        camera_cancel.onClick{ if (camera_mode == CM_SHOT_COMMIT) presenter.badShot() }
        shot_cancel.onClick { if (camera_mode == CM_READY) cameraMode(CM_OFF)}
        initPhotos()

        photo_tabs.setupWithViewPager(photo_pager, true)
        photo_pager.onPageChangeListener { onPageSelected { page->presenter.currentPhotoIdxChanged(page) }  }

        Glide.get(this).clearMemory()
        Thread({
            Glide.get(this).clearDiskCache()
        }).start()
    }

    private fun checkCameraHardware() = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)

    private fun initPhotos() {
        if (!checkCameraHardware()) {
            photos_lay.visibility = View.GONE
            return
        }

        fotoapparat = Fotoapparat(
                context = this,
                view = camera_view,
                scaleType = ScaleType.CenterCrop,
                logger =  object : Logger {
                    override fun log(message: String) {
                        Timber.w("camera: $message")
                        when (message) {
                            "CameraDevice: startPreview" -> {
                                if (camera_mode == CM_OFF) cameraMode(CM_OFF) //fix for the first time after granting camera permission
                                else if (camera_mode == CM_INITIALIZING) cameraMode(CM_READY)
                            }
                        }
                    }
                }
        )

        cameraMode(CM_OFF)

        photo_pager.adapter = Adapter(supportFragmentManager, photos)
    }


    class Adapter(fm: FragmentManager?, val data: ArrayList<File>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val res = PhotoFragment()
            res.arguments = Bundle()
            res.arguments.putInt(PhotoFragment.KEY_IDX, position)
            res.arguments.putString(PhotoFragment.KEY_PATH, data.get(position).absolutePath)
            return res
        }

        override fun getCount(): Int {
            return data.size
        }
    }

    override fun onPause() {
        super.onPause()
        cameraMode(CM_OFF)
    }

    class PhotoFragment : Fragment() {
        companion object {
            val KEY_IDX = "KEY_IDX"
            val KEY_PATH = "KEY_PATH"
        }
        var root: View? = null
        var idx = 0
        var path = ""

        val colors = intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.BLUE
        )

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            root = inflater?.inflate(R.layout.fragment_photo_values, container, false)

            idx = arguments.getInt(KEY_IDX)
            path = arguments.getString(KEY_PATH)
            val image = root?.find<ImageView>(R.id.image)

            Timber.d("loading #$idx with $path")

            if (path.length > 0) {
                Glide.with(this)
                        .load(path)
                        .into(image!!)
                //root?.findViewById(R.id.no_image_hint)?.visibility = GONE
            }

            //root?.find<ImageView>(R.id.image)?.setBackgroundColor(colors[idx])
            return root
        }
    }

    override fun showCameraView() {
        cameraMode(CM_INITIALIZING)
    }


    private fun cameraShot() {
        cameraMode(CM_SAVING)
        presenter.cameraShot(fotoapparat?.takePicture(), camera_view.measuredWidth, camera_view.measuredHeight)
    }


    override fun photoError() {
        cameraMode(CM_OFF)
        toast(R.string.photo_error)
    }

    override fun tmpPhotoSaved(file: File) {
        val options = RequestOptions().signature(ObjectKey(System.currentTimeMillis()))
        Glide.with(this)
                .load(file)
                .apply(options)
                .into(camera_preview)
        cameraMode(CM_SHOT_COMMIT)
    }

    override fun hideFAB() {
        add_photo.hide()
    }

    override fun showFAB() {
        add_photo.show()
    }

    override fun showAddIPU() {
        val view = layoutInflater.inflate(R.layout.item_add_user_ipu, parametersContainer, false)
        view.onClick { presenter.addIPUClicked() }
        parametersContainer.addView(view)
    }

    override fun navigateToUserIPU(number: String?) {
        val intent = Intent(this, UserIPUActivity::class.java)
                .putExtra(Constants.KEY_RECEIPT, receiptId)
                .putExtra(Constants.KEY_IPU_NUMBER, number)
        startActivity(intent)
    }

    private fun cameraMode(mode: CameraMode) {
        Timber.d("mode: $mode")
        camera_mode = mode
        when (mode) {
            CM_OFF -> {
                camera_view.visibility = GONE
                camera_controls.visibility = GONE
                camera_preview.hide()
                shot_cancel.hide()
                showFAB()
                fotoapparat?.stop()
            }
            CM_INITIALIZING -> {
                camera_view.visibility = VISIBLE

                camera_preview.hide()
                camera_cancel.hide()
                camera_done.hide()
                camera_shot.hide()
                shot_cancel.hide()

                val p = RxPermissions.getInstance(this)
                camera_controls.visibility = VISIBLE
                p.request(android.Manifest.permission.CAMERA)
                        .subscribe(
                                {
                                    granted ->
                                    if (granted) {
                                        fotoapparat?.start()
                                    } else {
                                        longToast(R.string.need_camera_permission)
                                    }
                                }
                        )
            }
            CM_READY -> {
                camera_cancel.hide()
                camera_done.hide()
                camera_shot.show()
                shot_cancel.show()
            }
            CM_SAVING -> {
                camera_shot.hide()
                shot_cancel.hide()
            }
            CM_SHOT_COMMIT -> {
                fotoapparat?.stop()
                camera_preview.show()
                camera_cancel.show()
                camera_done.show()
                camera_shot.hide()
                shot_cancel.hide()
            }
        }
    }


    override fun nextCaptureFragment(file: File) {
        cameraMode(CM_OFF)
        photos.add(file)
        photo_pager.adapter.notifyDataSetChanged()
        photo_pager.setCurrentItem(photos.size-1, true)
    }

    override fun startPhotoSendingService() {
        startService(Intent(this, PhotoService::class.java))
    }
}