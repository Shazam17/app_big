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
import io.fotoapparat.Fotoapparat


/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesActivity : MvpActivity(), ISendValuesView {

    @Inject lateinit var presenter: ISendValuesPresenter

    private val receipt: Receipt? by extras()
    private val receiptId: String? by extras(Constants.KEY_RECEIPT)
    private var fromTransaction: Boolean by extras(Constants.KEY_FROM_TRANSACTION, defaultValue = false)

    val fotoapparat = Fotoapparat(
            context = this,
            view = camera_view
    )

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
                afterTextChanged {
                    text ->
                    it.value = text.toString()
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
            positiveText(R.string.send_values_dialog_button)
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
        add_photo.onClick{ presenter.addPhotoClick() }
        initPhotos()
    }

    private fun checkCameraHardware() = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)

    private fun initPhotos() {
        if (!checkCameraHardware()) {
            photos_lay.visibility = View.GONE
            return
        }

        fotoapparat.start()

        photo_pager.adapter = Adapter(supportFragmentManager)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

    class Adapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val res = PhotoFragment()
            res.arguments = Bundle()
            res.arguments.putInt(PhotoFragment.KEY_IDX, position)
            return res
        }

        override fun getCount(): Int {
            return 2
        }
    }

    class PhotoFragment : Fragment() {
        companion object {
            val KEY_IDX = "KEY_IDX"
        }
        var root: View? = null
        var idx = 0

        val colors = intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.BLUE
        )

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            root = inflater?.inflate(R.layout.fragment_photo_values, container, false)

            idx = arguments.getInt(KEY_IDX)
            root?.find<ImageView>(R.id.image)?.setBackgroundColor(colors[idx])
            return root
        }
    }

    override fun showCameraView() {
        camera_lay.visibility = View.VISIBLE
        camera_progress.visibility = View.VISIBLE
        val p = RxPermissions.getInstance(this)
        p.request(android.Manifest.permission.CAMERA)
                .subscribe(
                        {
                            granted ->
                            if (granted) {
                                accessCamera()
                            } else {
                                alert(R.string.need_camera_permission) {
                                    positiveButton(R.string.answer_positive, {})
                                }
                            }
                        }
                )
    }

    private fun accessCamera() {

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
}