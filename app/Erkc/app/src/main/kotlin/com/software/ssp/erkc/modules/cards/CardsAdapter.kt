package com.software.ssp.erkc.modules.cards

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.CardStatus
import com.software.ssp.erkc.extensions.setBackgroundColorByContextCompat
import com.software.ssp.erkc.extensions.setCardBackgroundColorByContextCompat
import com.software.ssp.erkc.extensions.setTextColorByContextCompat
import kotlinx.android.synthetic.main.card_listitem.view.*
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsAdapter(var cards: List<Card>, val itemEditClick: (Card) -> Unit, val itemByStatusClick: (Card) -> Unit, val itemDeleteClick: (Card) -> Unit) : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_listitem, parent, false)
        return ViewHolder(view, itemEditClick, itemByStatusClick, itemDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindCard(cards[position])
    }

    override fun getItemCount(): Int {
        return cards.count()
    }

    class ViewHolder(itemView: View, val itemEditClick: (Card) -> Unit, val itemByStatusClick: (Card) -> Unit, val itemDeleteClick: (Card) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bindCard(card: Card) {
            with(itemView) {
                name.text = card.name
                no.text = card.maskCardNo
                edit.onClick {
                    itemEditClick(card)
                }
                status.onClick {
                    itemByStatusClick(card)
                }
                updateColors(card)
                when (card.statusId) {
                    CardStatus.NOT_REGISTERED.ordinal ->
                        status.text = context.getString(CardStatus.NOT_REGISTERED.stringResId)
                    CardStatus.REGISTERED.ordinal ->
                        status.text = context.getString(CardStatus.REGISTERED.stringResId)
                    CardStatus.ACTIVATED.ordinal ->
                        status.text = context.getString(CardStatus.ACTIVATED.stringResId)
                    CardStatus.DELETED.ordinal ->
                        status.text = context.getString(CardStatus.DELETED.stringResId)
                }
                swipe.showMode = SwipeLayout.ShowMode.LayDown
                delete.onClick {
                    itemDeleteClick(card)
                }
                swipe.addDrag(SwipeLayout.DragEdge.Left, deleteWrapper)
            }
        }

        fun updateColors(card: Card) {
            with(itemView) {
                if (card.statusId == CardStatus.ACTIVATED.ordinal) {
                    cardView.setCardBackgroundColorByContextCompat(R.color.colorPrimary)
                    cardContent.setBackgroundColorByContextCompat(R.color.colorPrimary)
                    name.setTextColorByContextCompat(R.color.colorWhite)
                    no.setTextColorByContextCompat(R.color.colorWhite)
//                    status.setTextColorByContextCompat(R.color.colorWhite)
                    status.visibility = View.INVISIBLE
                } else {
                    cardView.setCardBackgroundColorByContextCompat(R.color.colorCardView)
                    cardContent.setBackgroundColorByContextCompat(R.color.colorCardView)
                    name.setTextColorByContextCompat(R.color.colorBlack)
                    no.setTextColorByContextCompat(R.color.colorBlack)
                    status.setTextColorByContextCompat(R.color.colorBlack)
                    status.visibility = View.VISIBLE
                }
            }
        }
    }

}
