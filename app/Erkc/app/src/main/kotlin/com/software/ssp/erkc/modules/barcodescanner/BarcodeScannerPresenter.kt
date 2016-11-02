package com.software.ssp.erkc.modules.barcodescanner

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

/**
 * @author Alexander Popov on 24.10.2016.
 */
class BarcodeScannerPresenter @Inject constructor(view: IBarcodeScannerView) : RxPresenter<IBarcodeScannerView>(view), IBarcodeScannerPresenter {

    override fun onBarcodeCaptured(qrData: String) {
        view?.navigateToScan(qrData)
    }

}