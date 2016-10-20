package com.software.ssp.erkc.modules.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.fragment_contacts.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import javax.inject.Inject

class ContactsFragment : MvpFragment(), IContactsView, OnMapReadyCallback {

    @Inject lateinit var presenter: IContactsPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerContactsComponent.builder()
                .appComponent(appComponent)
                .contactsModule(ContactsModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings?.isCompassEnabled = false

        val location = LatLng(56.473696, 84.973129)

        val marker = MarkerOptions()
        marker.position(location)
        marker.title(getString(com.software.ssp.erkc.R.string.contacts_map_marker_title))

        val cameraPosition = CameraPosition(location, 16f, 0f, 0f)

        val cameraUpdatePosition = CameraUpdateFactory.newCameraPosition(cameraPosition)

        map.moveCamera(cameraUpdatePosition)
        map.addMarker(marker).showInfoWindow()

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun sendEmailMessage() {

        val message = contactsMessageEditText.text
        val subject = getString(R.string.contacts_email_prefix) + message.subSequence(0, Math.min(message.length, 60))

        val mailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.contacts_email_address), null))
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mailIntent.putExtra(Intent.EXTRA_TEXT, contactsMessageEditText.text)

        startActivity(Intent.createChooser(mailIntent, "Отправить сообщение с..."))
    }

    override fun setControlsEnabled(isEnabled: Boolean) {
        contactsSendButton.enabled = isEnabled
        contactsMessageEditText.enabled = isEnabled
    }

    private fun initViews() {
        contactsPhoneTextView.movementMethod = LinkMovementMethod.getInstance()
        contactsSendButton.onClick { presenter.onSendButtonClick() }

        initMap()
    }

    private fun initMap() {
        var mapFragment = this.childFragmentManager.findFragmentByTag(MapFragment::class.java.simpleName) as? MapFragment

        if (mapFragment == null) {
            mapFragment = MapFragment()
            this.childFragmentManager
                    .beginTransaction()
                    .add(R.id.contactsMapContainer, mapFragment, MapFragment::class.java.simpleName)
                    .commit()
            this.childFragmentManager.executePendingTransactions()
        }

        mapFragment.getMapAsync(this)
    }
}

