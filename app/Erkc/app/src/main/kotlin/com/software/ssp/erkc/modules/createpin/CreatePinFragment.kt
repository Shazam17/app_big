package com.software.ssp.erkc.modules.createpin

import android.os.Bundle
import android.view.LayoutInflater
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.fragment_create_pin.*
import javax.inject.Inject

class CreatePinFragment : MvpFragment(), ICreatePinView {

    @Inject lateinit var presenter: ICreatePinPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_pin, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerCreatePinComponent.builder()
                .appComponent(appComponent)
                .createPinModule(CreatePinModule(this))
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
