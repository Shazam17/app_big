package com.software.ssp.erkc.modules.request.nonauthedrequest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.address.SearchAddressActivity
import com.software.ssp.erkc.modules.mainscreen.MainScreenFragment
import com.software.ssp.erkc.modules.signin.SignInActivity
import kotlinx.android.synthetic.main.fragment_request_non_auth.*
import org.jetbrains.anko.*
import javax.inject.Inject

class RequestNonAuthFragment: MvpFragment(), IRequestNonAuthView {

    @Inject lateinit var presenter: IRequestNonAuthPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerRequestNonAuthComponent.builder()
                .appComponent(appComponent)
                .requestNonAuthModule(RequestNonAuthModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_request_non_auth, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.onViewAttached()
    }

    private fun initViews() {
        requestNonAuthSignInSystemTextView.onClick {
            navigateToAuthScreen()
        }

        /* Address Edit Text Field */

        request_non_auth_root_layout.requestFocus()
        textInputLayoutRequestNonAuth.isHintAnimationEnabled = false
        yourAddressTextEdit.onClick {
            presenter.onAddressClick()
        }

        yourAddressTextEdit.textChangedListener {
            onTextChanged { _, _, _, _ -> textInputLayoutRequestNonAuth.error = null }
        }

        yourAddressTextEdit.onTouch { view, motionEvent ->
            when {
                motionEvent.actionMasked == MotionEvent.ACTION_UP -> {
                    request_non_auth_root_layout.requestFocus()
                    presenter.onAddressClick()
                    true
                }
                else -> true
            }
        }

        /* Call Button */
        requestNonAuthCallButton.onClick {
            presenter.onCallButtonClick()
        }
    }

    override fun navigateToCallScreen() {
        // TODO impl real number phone when will
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${requestNonAuthCallButton.text}")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            SearchAddressActivity.REQUEST_ADDRESS_RESULT_CODE -> {
                if (data?.getStringExtra(SearchAddressActivity.RESULT_ADDRESS_KEY) != null) {
                    yourAddressTextEdit.setText(data?.getStringExtra(SearchAddressActivity.RESULT_ADDRESS_KEY))
                    var fias = data?.getStringExtra(SearchAddressActivity.RESULT_FIAS_KEY)!!
                    presenter.fetchCompanies(fias)
                    Log.e("FIAS", fias)
                }
            }
        }
    }

    private fun navigateToAuthScreen() {
//        val fragment = MainScreenFragment()
//        val bundle = Bundle()
//        bundle.putBoolean("nonAuthImitation", true)
//        bundle.putBoolean("navigateToLogin", true)
//        fragment.arguments = bundle
//
//        fragmentManager.beginTransaction()
//                .replace(R.id.drawerFragmentContainer, fragment)
//                .commitAllowingStateLoss()
        startActivity<SignInActivity>()
    }

    override fun setStreetField(street: String) {
        yourAddressTextEdit.setText(street)
        // TODO when "ok" address, need show support info for non-auth user
        requestNonAuthMainInfoConstraintLayout.visibility = View.VISIBLE
    }

    override fun navigateToStreetSelectScreen() {
        startActivityForResult<SearchAddressActivity>(SearchAddressActivity.REQUEST_ADDRESS_RESULT_CODE, SearchAddressActivity.SEARCH_ADDRESS_REQUEST_FLAG to true)
    }

    override fun showSupportInfo() {
        requestNonAuthMainInfoConstraintLayout.visibility = View.VISIBLE
    }

    override fun setSupportInfo(nameManagerCompany: String, placeCompany: String, numberPhone: String) {
        requestNonAuthMainInfoConstraintLayout.visibility = View.VISIBLE
        var text="Ваша управляющая компания <font color=#094997>${nameManagerCompany}</font>, находится по адресу <font color=#4676B1>${placeCompany}</font>"
        requestNonAuthYourManagerCompany.setText(Html.fromHtml(text))
        requestNonAuthCallButton.setText("8 (${numberPhone.substring(0,3)}) ${numberPhone.substring(3,6)} ${numberPhone.substring(6,8)} ${numberPhone.substring(8)}")
    }

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }


}