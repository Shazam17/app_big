package com.software.ssp.erkc.modules.fastauth

import android.os.Bundle
import android.view.LayoutInflater
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.fragment_fast_auth.*
import javax.inject.Inject

class FastAuthFragment : MvpFragment(), IFastAuthView {

    @Inject lateinit var presenter: IFastAuthPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_fast_auth, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerFastAuthComponent.builder()
                .appComponent(appComponent)
                .fastAuthModule(FastAuthModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    private fun initViews() {
        //TODO init views
    }
}
