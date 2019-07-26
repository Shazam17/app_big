package com.software.ssp.erkc.modules.createrequest.adapters

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmLocalImage
import io.realm.RealmList
import org.jetbrains.anko.onClick
import java.io.File

class SelectedImagesAdapter(private val images: RealmList<RealmLocalImage>,
                            val onItemClick: ((RealmLocalImage) -> Unit)? = null,
                            val onDeleteItemClick: ((RealmLocalImage) -> Unit)? = null) : RecyclerView.Adapter<SelectedImagesAdapter.SelectedImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_request, parent, false)
        return SelectedImagesViewHolder(
                view,
                onItemClick = onItemClick,
                onDeleteItemClick = onDeleteItemClick)
    }

    override fun getItemCount(): Int  = images.count()

    override fun onBindViewHolder(holder: SelectedImagesViewHolder, position: Int) {
        holder.bindImage(images[position])
    }


    class SelectedImagesViewHolder(
            val view: View,
            val onItemClick: ((RealmLocalImage) -> Unit)?,
            val onDeleteItemClick: ((RealmLocalImage) -> Unit)?
    ) : RecyclerView.ViewHolder(view) {
        fun bindImage(selectedImage: RealmLocalImage) {
            view.apply {
                val photoImageView = findViewById<ImageView>(R.id.photoRequestImage)

                Glide.with(this).load(selectedImage.url).into(photoImageView)
                photoImageView.onClick {
                    onItemClick?.invoke(selectedImage)
                }
                val removeImageButton = findViewById<ImageView>(R.id.removeImageButton)
                removeImageButton.onClick {
                    onDeleteItemClick?.invoke(selectedImage)
                }
            }
        }
    }
}