package com.software.ssp.erkc.modules.card.cards

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.card.addcard.AddCardActivity
import com.software.ssp.erkc.modules.card.editcard.EditCardActivity
import com.software.ssp.erkc.modules.confirmbyurl.ConfirmByUrlActivity
import kotlinx.android.synthetic.main.fragment_cards.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsFragment : BaseListFragment<Card, ICardsView, ICardsPresenter>(), ICardsView {

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
        presenter.onViewAttached()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cards_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.card_menu_add -> {
                presenter.onAddClick()
                return true
            }
            R.id.card_menu_help -> {
                presenter.onHelpClick()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)

        }
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        val adapter = CardsAdapter(dataset, object : CardsAdapter.CardClickListeners {
            override fun itemEditClick(card: Card) {
                presenter.onEditClick(card)
            }

            override fun itemByStatusClick(card: Card) {
                presenter.onByStatusClick(card)
            }

            override fun itemDeleteClick(card: Card) {
                presenter.onDeleteClick(card)
            }

        })
        return adapter
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewAttached()
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
        showMessage("not implemented")
    }

    override fun navigateToBankSite(url: String) {
        startActivity<ConfirmByUrlActivity>(Constants.KEY_URL to url)
    }

}