package com.software.ssp.erkc.modules.paymentscreen.payment

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.OpenCardsEvent
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.ApiErrorType
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.CardsRepository
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.CardStatus
import com.software.ssp.erkc.extensions.isEmail
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.extensions.toStringWithDot
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */
class PaymentPresenter @Inject constructor(view: IPaymentView) : RxPresenter<IPaymentView>(view), IPaymentPresenter {

    @Inject lateinit var cardsRepository: CardsRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var paymentRepository: PaymentRepository
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var eventBus: Relay<Any, Any>

    override lateinit var receipt: Receipt
    override var receiptId: String? = null

    private lateinit var user: RealmUser
    private lateinit var realmReceipt: RealmReceipt

    private val currentPayment = RealmPaymentInfo()

    private var paymentValue: Double = 0.0
        set(value) {
            field = value
            currentPayment.amount = paymentValue + commission
            view?.fillAmountAndCommission(commission, value + commission)
        }

    private var commission: Double = 0.0
        get() = paymentValue * (if (receiptId == null) receipt.percent else realmReceipt.percent) / 100

    override fun onViewAttached() {
        super.onViewAttached()

        if (receiptId == null) {
            view?.showReceiptInfo(receipt)
            paymentValue = receipt.amount
            return
        }

        view?.setProgressVisibility(true)
        subscriptions += realmRepository
                .fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            user = currentUser
                            realmReceipt = user.receipts.find { it.id == receiptId }!!
                            paymentValue = realmReceipt.amount
                            currentPayment.paymentCard = realmReceipt.linkedCard

                            view?.showReceiptInfo(realmReceipt)
                            view?.showUserInfo(user)
                            view?.showSelectedCard(currentPayment.paymentCard)

                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setProgressVisibility(false)
                        }
                )
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onSumTextChange(sum: String) {
        paymentValue = if (sum.isNullOrBlank()) 0.0 else sum.toDouble()
    }

    override fun onChooseCardClick() {
        val filteredCards = user.cards.filter { CardStatus.values()[it.statusId] == CardStatus.ACTIVATED }
        if (filteredCards.isEmpty()) {
            view?.showNavigateToCardsDialog()
        } else {
            view?.showCardSelectDialog(filteredCards.map { PaymentCardViewModel(it, it.id == currentPayment.paymentCard?.id) })
        }
    }

    override fun onCardSelected(card: RealmCard?) {
        currentPayment.paymentCard = card
        view?.showSelectedCard(currentPayment.paymentCard)
    }

    override fun onPaymentConfirmClick(email: String) {
        if (!validateData(email)) {
            return
        }

        view?.setProgressVisibility(true)

        val method = if (currentPayment.paymentCard == null) PaymentMethod.DEFAULT.ordinal else PaymentMethod.ONE_CLICK.ordinal

        subscriptions += paymentRepository.init(
                realmReceipt.barcode,
                method,
                currentPayment.amount.toStringWithDot(),
                email,
                currentPayment.paymentCard?.id)
                .subscribe(
                        {
                            response ->
                            view?.setProgressVisibility(false)
                            if (currentPayment.paymentCard == null) {
                                view?.navigateToResult(response.url)
                            } else {
                                view?.showResult(true)
                            }
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            if (error is ApiException && error.errorCode == ApiErrorType.PAYMENT_ERROR) {
                                view?.showResult(false)
                            }
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onNextClick(email: String) {
        if (!validateData(email)) {
            return
        }

        if (currentPayment.paymentCard != null) {
            view?.showPaymentConfirmDialog(
                    realmReceipt,
                    currentPayment.paymentCard!!,
                    commission,
                    currentPayment.amount,
                    email)
        } else {
            view?.setProgressVisibility(true)
            subscriptions += paymentRepository.init(
                    if (receiptId == null) receipt.barcode else realmReceipt.barcode,
                    PaymentMethod.DEFAULT.ordinal,
                    currentPayment.amount.toStringWithDot(),
                    email,
                    null
            ).subscribe(
                    {
                        response ->
                        view?.setProgressVisibility(false)
                        view?.navigateToResult(response.url)
                    },
                    {
                        error ->
                        view?.setProgressVisibility(false)
                        view?.showMessage(error.parsedMessage())
                    })
        }
    }

    override fun onNavigateToCardsConfirmClick() {
        eventBus.call(OpenCardsEvent())
        view?.close()
    }

    override fun onDoneClick() {
        view?.close()
    }

    private fun validateData(email: String): Boolean {
        var isValid = true
        if (paymentValue == 0.0) {
            view?.showSumError(R.string.error_field_required)
            isValid = false
        }

        if (email.isNullOrBlank()) {
            view?.showEmailError(R.string.error_field_required)
            isValid = false
        }

        if (!email.isEmail()) {
            view?.showEmailError(R.string.error_invalid_email)
            isValid = false
        }

        return isValid
    }
}
