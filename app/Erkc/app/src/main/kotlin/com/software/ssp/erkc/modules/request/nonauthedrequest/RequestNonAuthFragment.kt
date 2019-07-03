package com.software.ssp.erkc.modules.request.nonauthedrequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.mainscreen.MainScreenFragment
import kotlinx.android.synthetic.main.fragment_request_non_auth.*
import org.jetbrains.anko.onClick
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
    }

    private fun navigateToAuthScreen() {
        val fragment = MainScreenFragment()
        val bundle = Bundle()
        bundle.putBoolean("nonAuthImitation", true)
        bundle.putBoolean("navigateToLogin", true)
        fragment.arguments = bundle

        fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, fragment)
                .commitAllowingStateLoss()
    }

    override fun showSupportInfo() {
        requestNonAuthMainInfoConstraintLayout.visibility = View.VISIBLE
    }

    override fun setSupportInfo(nameManagerCompany: String, placeCompany: String, numberPhone: String) {

    }

    override fun beforeDestroy() {
        presenter.onViewDetached()
    }

}