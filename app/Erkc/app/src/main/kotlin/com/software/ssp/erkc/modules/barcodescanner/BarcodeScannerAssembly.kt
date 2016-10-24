package com.software.ssp.erkc.modules.barcodescanner

import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.di.scopes.ActivityScope
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

/**
 * @author Alexander Popov on 24.10.2016.
 */

@ActivityScope
@Component(modules = arrayOf(BarcodeScannerModule::class), dependencies = arrayOf(AppComponent::class))
interface BarcodeScannerComponent {
    fun inject(barcodeScannerActivity: BarcodeScannerActivity)
}

@Module(includes = arrayOf(BarcodeScannerModule.Declarations::class))
class BarcodeScannerModule(val barcodeScannerView: IBarcodeScannerView) {

    @Provides
    fun provideBarcodeView(): IBarcodeScannerView {
        return barcodeScannerView
    }

    @Module
    interface Declarations {
        @Binds
        @ActivityScope
        fun bindBarcodePresenter(barcodeScannerPresenter: BarcodeScannerPresenter): IBarcodeScannerPresenter
    }

}