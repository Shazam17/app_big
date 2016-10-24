package com.software.ssp.erkc.modules.barcodescanner

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

/**
 * @author Alexander Popov on 24.10.2016.
 */
interface IBarcodeScannerView : IView {
    fun navigateToScan(qrData: String)
}

interface IBarcodeScannerPresenter : IPresenter<IBarcodeScannerView> {
    fun onBarcodeCaptured(qrData: String)
}