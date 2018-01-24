package com.software.ssp.erkc.modules.paymentscreen.payment

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.common.OpenCardsEvent
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.*
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
import java.util.*
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
    override var paymentId: String? = null
    override var fromTransaction: Boolean = false

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

        if (paymentId != null) {
            retryByPaymentId()
            return
        }

        if (receiptId == null) {
            view?.showReceiptInfo(receipt)
            paymentValue = receipt.amount
            return
        }

        view?.setProgressVisibility(true)
        if (!fromTransaction) {
            fetchPaymentInfo()
        } else {
            fetchPaymentInfoFromTransaction()
        }
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

    override fun onNextClick(email: String) {
        if (!validateData(email)) {
            return
        }

        currentPayment.paymentCard?.let {
            view?.showPaymentConfirmDialog(
                    realmReceipt,
                    it,
                    commission,
                    currentPayment.amount,
                    email
            )
            return
        }

        onPaymentConfirmClick(email)
    }

    override fun onPaymentConfirmClick(email: String) {
        if (!validateData(email)) {
            return
        }

        if (activeSession.isOfflineSession) {
            saveToTransactions(email)
        } else {
            initPayment(email)
        }
    }

    override fun onNavigateToCardsConfirmClick() {
        eventBus.call(OpenCardsEvent())
        view?.close()
    }

    override fun onDoneClick() {
        view?.close()
    }

    private fun deletePaymentFromTransactions() {
        subscriptions += realmRepository.fetchCurrentUser()
            .concatMap {
                currentUser ->
                realmRepository.deleteOfflinePayment(RealmOfflinePayment(
                    login = currentUser.login,
                    receipt = realmReceipt
                ))
            }
            .subscribe(
                {
                    // Nothing
                },
                {
                    error ->
                    // Nothing
                }
            )
    }

    private fun initPayment(userEmail: String) {
        view?.setProgressVisibility(true)

        subscriptions += paymentRepository.init(
                code = if (receiptId == null) receipt.barcode else realmReceipt.barcode,
                method = if (currentPayment.paymentCard == null) PaymentMethod.DEFAULT.ordinal else PaymentMethod.ONE_CLICK.ordinal,
                sum = currentPayment.amount.toStringWithDot(),
                email = userEmail,
                cardId = currentPayment.paymentCard?.id
            )
            .subscribe(
                {
                    response ->
                    deletePaymentFromTransactions()
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

    private fun saveToTransactions(userEmail: String) {
        view?.setProgressVisibility(true)

        val offlinePayment = RealmOfflinePayment(
                login = user.login,
                receipt = realmReceipt,
                paymentSum = paymentValue,
                email = userEmail,
                card = currentPayment.paymentCard,
                createDate = Calendar.getInstance().time
        )

        subscriptions += realmRepository.saveOfflinePayment(offlinePayment)
                .subscribe(
                        {
                            view?.setProgressVisibility(false)
                            view?.showMessage(R.string.transaction_save_to_transaction_help_text)
                            view?.close()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun fetchPaymentInfoFromTransaction() {
        subscriptions += realmRepository.fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    user = currentUser
                    realmRepository.fetchOfflinePaymentByReceiptId(user.login, receiptId!!)
                }
                .subscribe(
                        {
                            offlinePayment ->
                            realmReceipt = offlinePayment.receipt
                            currentPayment.paymentCard = offlinePayment.card

                            view?.showPaymentSum(offlinePayment.paymentSum)
                            view?.showReceiptInfo(realmReceipt)
                            view?.showEmail(offlinePayment.email)
                            view?.showSelectedCard(currentPayment.paymentCard)

                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setProgressVisibility(false)
                        })
    }

    private fun fetchPaymentInfo() {
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
                            view?.showEmail(user.email)
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

    private fun retryByPaymentId() {
        view?.setProgressVisibility(true)
        subscriptions += realmRepository
                .fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            user = currentUser

                            val payment = currentUser.paymentsInfo.first { it.id == paymentId }
                            realmReceipt = payment.receipt!!
                            receiptId = realmReceipt.id

                            currentPayment.apply {
                                paymentCard = payment.paymentCard
                            }

                            view?.showReceiptInfo(realmReceipt)
                            view?.showEmail(user.email)
                            view?.showSelectedCard(currentPayment.paymentCard)

                            val paymentWithoutCommission = payment.sum * 100 / (payment.receipt!!.percent + 100)
                            view?.showPaymentSum(paymentWithoutCommission)

                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setProgressVisibility(false)
                        }
                )
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
