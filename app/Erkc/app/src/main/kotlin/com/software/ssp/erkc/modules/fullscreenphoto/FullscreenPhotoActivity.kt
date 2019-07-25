package com.software.ssp.erkc.modules.fullscreenphoto

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity
import com.software.ssp.erkc.modules.requestdetails.RequestDetailsActivity.Companion.IS_EDITABLE
import kotlinx.android.synthetic.main.activity_photo_fullscreen.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

class FullscreenPhotoActivity : MvpActivity(), IFullscreenPhotoView {


    @Inject
    lateinit var presenter: IFullscreenPhotoPresenter

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerFullscreenPhotoComponent.builder()
                .appComponent(appComponent)
                .fullscreenPhotoModule(FullscreenPhotoModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_fullscreen)
        presenter.showPhoto(intent.getStringExtra(RequestDetailsActivity.PHOTO_LINK))
        initViews()
    }

    private fun initViews() {
        if (!intent.getBooleanExtra(IS_EDITABLE, true)) {
            deleteButton.visibility = View.GONE

        } else {
            deleteButton.visibility = View.VISIBLE
            deleteButton.onClick {
            }
        }
        backButton.onClick {
            this.finish()
        }
    }

    override fun openPhoto(downloadLink: GlideUrl) {
        Glide.with(this).load(downloadLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(fullscreen_photo_id)
    }

    override fun beforeDestroy() {
    }
}



