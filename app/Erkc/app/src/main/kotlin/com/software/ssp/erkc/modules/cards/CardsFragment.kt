package com.software.ssp.erkc.modules.cards

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.fragment_cards.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsFragment : MvpFragment(), ICardsView {

    @Inject lateinit var presenter: ICardsPresenter
    var mAdapter: CardsAdapter? = null

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerCardsComponent.builder()
                .appComponent(appComponent)
                .cardsModule(CardsModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
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

    override fun onResume() {
        super.onResume()
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun showData(cards: List<Card>) {
        if (cards.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            cardRecyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            cardRecyclerView.visibility = View.VISIBLE
            mAdapter?.cards = cards
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun navigateToEditCard(card: Card) {
        showMessage("not implemented")
    }

    override fun navigateToAddCard() {
        showMessage("not implemented")
    }

    override fun navigateToHelp() {
        showMessage("not implemented")
    }

    private fun initViews() {
        setHasOptionsMenu(true)
        cardRecyclerView.layoutManager = LinearLayoutManager(application())
        mAdapter = CardsAdapter(emptyList(), {
            card ->
            presenter.onEditClick(card)
        }, {
            card ->
            presenter.onByStatusClick(card)
        }, {
            card ->
            presenter.onDeleteClick(card)
        })
        cardRecyclerView.adapter = mAdapter
    }

}