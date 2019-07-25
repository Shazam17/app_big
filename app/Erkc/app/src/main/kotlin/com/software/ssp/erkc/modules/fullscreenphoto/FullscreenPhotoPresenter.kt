package com.software.ssp.erkc.modules.fullscreenphoto

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.modules.request.IRequestTabView
import javax.inject.Inject

class FullscreenPhotoPresenter @Inject constructor(view: IFullscreenPhotoView):RxPresenter<IFullscreenPhotoView>(view),IFullscreenPhotoPresenter{


    override fun showPhoto(downloadLink: String) {
        val glideUrl = GlideUrl("http://fon.zayavki.pro$downloadLink", LazyHeaders.Builder()
                .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
                .build())
        view?.openPhoto(glideUrl)
    }

}