package com.software.ssp.erkc.modules.card.cards

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.card.addcard.AddCardActivity
import com.software.ssp.erkc.modules.card.editcard.EditCardActivity
import com.software.ssp.erkc.modules.confirmbyurl.ConfirmByUrlActivity
import com.software.ssp.erkc.modules.instructions.InstructionType
import com.software.ssp.erkc.modules.instructions.instructiondetails.InstructionActivity
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.fragment_cards.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsFragment : BaseListFragment<CardViewModel>(), ICardsView {

    @Inject lateinit var presenter: ICardsPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerCardsComponent.builder()
                .appComponent(appComponent)
                .cardsModule(CardsModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewAttached()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.cards_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.card_menu_add -> {
                presenter.onAddClick()
            }
            R.id.card_menu_help -> {
                presenter.onHelpClick()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return CardsAdapter(dataset, object : CardsAdapter.InteractionListener {
            override fun itemEditClick(card: RealmCard) {
                presenter.onEditClick(card)
            }

            override fun itemByStatusClick(card: RealmCard) {
                presenter.onStatusClick(card)
            }

            override fun itemDeleteClick(card: RealmCard) {
                presenter.onDeleteClick(card)
            }
        })
    }

    override fun cardDidNotDeleted(card: RealmCard) {
        val cardIndex = dataset.indexOfFirst { it.card == card }
        dataset[cardIndex].isDeletePending = false
        adapter?.notifyItemChanged(cardIndex)
    }

    override fun cardDeleted(card: RealmCard) {
        val cardIndex = dataset.indexOfFirst { it.card == card }
        dataset.removeAt(cardIndex)
        adapter?.notifyItemRemoved(cardIndex)
        if (dataset.count() == 0) {
            setEmptyViewVisible(true)
        }
    }

    override fun setCardPending(card: RealmCard, isPending: Boolean) {
        val cardIndex = dataset.indexOfFirst { it.card == card }
        dataset[cardIndex].isRequestPending = isPending
        adapter?.notifyItemChanged(cardIndex)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setEmptyViewVisible(visible: Boolean) {
        emptyView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    override fun navigateToEditCard(card: Card) {
        startActivity<EditCardActivity>(Constants.KEY_SELECTED_CARD_ITEM to card)
    }

    override fun navigateToAddCard() {
        startActivity<AddCardActivity>()
    }

    override fun navigateToHelp() {
        startActivity<InstructionActivity>("instructionType" to InstructionType.CARDS)
    }

    override fun navigateToBankSite(url: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.add_card_info)
            .setNeutralButton(R.string.add_card_info_understand, DialogInterface.OnClickListener { dialog, id ->
                startActivity<ConfirmByUrlActivity>(Constants.KEY_URL to url)
            })
            .setCancelable(false)
        builder.create().show()
    }
}
