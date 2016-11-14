package com.software.ssp.erkc.modules.paymentscreen.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.models.User
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.drawer.DrawerItem
import kotlinx.android.synthetic.main.activity_payment.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */
class PaymentActivity : MvpActivity(), IPaymentView {

    @Inject lateinit var presenter: IPaymentPresenter
    private var receipt: Receipt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        receipt = intent.getParcelableExtra<Receipt>(Constants.KEY_RECEIPT)
        initViews()
        presenter.onViewAttached(receipt!!)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }


    override fun showActivatedCards(cards: List<Card>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToDrawer() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToResult() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showConfirmDialog() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNotificationsDialog() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fillAmountAndCommission(commission: String, sum: String) {
        paymentCommission.text = commission
        paymentAmount.text = sum
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentComponent.builder()
                .appComponent(appComponent)
                .paymentModule(PaymentModule(this))
                .build()
                .inject(this)
    }

    override fun fillData(user: User?, cards: List<Card>) {
        if (user != null) {
            paymentEmail.setText(user.email)
            if (cards.isEmpty()) {
                paymentCardAdd.visibility = View.VISIBLE
                paymentCardWrapper.visibility = View.GONE
            } else {
                paymentCardNo.text = cards.first().maskCardNo
                paymentCardName.text = cards.first().name
                paymentCardAdd.visibility = View.GONE
                paymentCardWrapper.visibility = View.VISIBLE
            }
        } else {
            paymentCardChooseContainer.visibility = View.GONE
        }
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        paymentButton.onClick {
            presenter.onNextClick()
        }
        paymentSum.setText("${receipt?.amount.toString().format(2)} р.")
        paymentSum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.onSumChange(p0.toString().toDouble())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        paymentAmount.text = "${receipt?.amount.toString().format(2)} р."
        paymentDebts.text = "${receipt?.amount.toString().format(2)} р."
        paymentBarcode.text = "${receipt?.barcode} (${receipt?.serviceName})"
        paymentAddress.text = receipt?.address

        paymentCardAdd.onClick {
            val intent = Intent()
            intent.putExtra(Constants.KEY_DRAWER_ITEM_FOR_SELECT, DrawerItem.CARDS)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        paymentCardWrapper.onClick {
            //dialog
        }
    }

}