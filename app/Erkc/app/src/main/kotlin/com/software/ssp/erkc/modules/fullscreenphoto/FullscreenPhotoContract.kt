package com.software.ssp.erkc.modules.fullscreenphoto

import com.bumptech.glide.load.model.GlideUrl
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IFullscreenPhotoView:IView{
    fun openPhoto(downloadLink: GlideUrl)
}
interface IFullscreenPhotoPresenter:IPresenter<IFullscreenPhotoView>{
    fun showPhoto(downloadLink:String)
}