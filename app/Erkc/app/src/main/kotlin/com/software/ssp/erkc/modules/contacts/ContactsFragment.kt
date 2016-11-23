package com.software.ssp.erkc.modules.contacts

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.TextView
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
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject

class ContactsFragment : MvpFragment(), IContactsView, OnMapReadyCallback {

    @Inject lateinit var presenter: IContactsPresenter

    private val defaultLatitude = 56.473696
    private val defaultLongitude = 84.973129
    private val mapCameraZoom = 16f

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings?.isCompassEnabled = false

        val location = LatLng(defaultLatitude, defaultLongitude)

        val marker = MarkerOptions()
        marker.position(location)
        marker.title(getString(com.software.ssp.erkc.R.string.contacts_map_marker_title))

        val cameraPosition = CameraPosition.fromLatLngZoom(location, mapCameraZoom)

        val cameraUpdatePosition = CameraUpdateFactory.newCameraPosition(cameraPosition)

        map.moveCamera(cameraUpdatePosition)
        map.addMarker(marker).showInfoWindow()

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setPending(isPending: Boolean) {
        sendButton.enabled = !isPending
        messageEditText.enabled = !isPending
    }

    override fun didSentMessage() {
        messageEditText.text.clear()

        val snack = Snackbar.make(view, R.string.contacts_email_send_dialog_message, Snackbar.LENGTH_LONG)
        val textView = snack.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snack.setAction(R.string.contacts_snack_bar_action_text, {})
        snack.setActionTextColor(Color.WHITE)
        snack.show()
    }

    override fun showMessageEmptyError(resId: Int) {
        messageInputLayout.error = getString(resId)
    }

    override fun setControlsVisible(isVisible: Boolean) {
        sendButton.visibility = if(isVisible) View.VISIBLE else View.GONE
        messageEditText.visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    private fun initViews() {
        contactsPhoneTextView.movementMethod = LinkMovementMethod.getInstance()
        sendButton.onClick { presenter.onSendButtonClick(messageEditText.text.toString(), getString(R.string.contacts_email_prefix)) }

        messageEditText.textChangedListener {
            onTextChanged { charSequence, start, end, count -> messageInputLayout.error = null }
        }

        initMap()
    }

    private fun initMap() {
        var mapFragment = this.childFragmentManager.findFragmentByTag(MapFragment::class.java.simpleName) as? MapFragment

        if (mapFragment == null) {
            mapFragment = MapFragment()
            this.childFragmentManager
                    .beginTransaction()
                    .add(R.id.mapContainer, mapFragment, MapFragment::class.java.simpleName)
                    .commit()
            this.childFragmentManager.executePendingTransactions()
        }

        mapFragment.getMapAsync(this)
    }
}

