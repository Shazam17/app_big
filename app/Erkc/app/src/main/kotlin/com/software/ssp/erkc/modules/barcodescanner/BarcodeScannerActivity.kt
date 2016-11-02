package com.software.ssp.erkc.modules.barcodescanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.hardware.Camera
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.zxing.Result
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.common.views.CustomBarcodeScannerFrameView
import com.software.ssp.erkc.di.AppComponent
import com.tbruyelle.rxpermissions.RxPermissions
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView
import javax.inject.Inject

/**
 * @author Alexander Popov on 24.10.2016.
 */
class BarcodeScannerActivity : MvpActivity(), IBarcodeScannerView, ZXingScannerView.ResultHandler {

    @Inject lateinit var presenter: IBarcodeScannerPresenter
    lateinit var scannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)

        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA)
                .subscribe({ granted ->
                    if (!granted) {
                        showMessage(getString(R.string.barcode_scanner_camera_permission_denied))
                    }
                })
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerBarcodeScannerComponent.builder()
                .appComponent(appComponent)
                .barcodeScannerModule(BarcodeScannerModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToScan(qrData: String) {
        val intent = Intent()
        intent.putExtra(Constants.KEY_SCAN_RESULT, qrData)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun handleResult(rawResult: Result) {
        presenter.onBarcodeCaptured(rawResult.text)
    }

    override fun onResume() {
        super.onResume()

        scannerView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
        scannerView.setResultHandler(this)
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        scannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context): IViewFinder {
                val finderView = CustomBarcodeScannerFrameView(context)
                val finderRect = Rect()
                finderFrameLayout.getHitRect(finderRect)
                finderRect.apply {
                    set(left + 2, top + 2, right - 2, bottom - 3)
                }
                finderView.finderRect = finderRect

                return finderView
            }
        }
        contentFrameLayout.addView(scannerView)
    }

}