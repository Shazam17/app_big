package com.software.ssp.erkc.modules.cards

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.extensions.*
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
            itemView.apply {
                name.text = card.name
                no.text = card.maskCardNo
                edit.onClick {
                    itemEditClick(card)
                }
                status.onClick {
                    itemByStatusClick(card)
                }
                when (card.statusId) {
                    CardStatus.NOT_REGISTERED.ordinal -> {
                        updateColors(CardStatus.NOT_REGISTERED)
                        status.text = context.getString(CardStatus.NOT_REGISTERED.stringResId)
                    }
                    CardStatus.REGISTERED.ordinal -> {
                        updateColors(CardStatus.NOT_REGISTERED)
                        status.text = context.getString(CardStatus.REGISTERED.stringResId)
                    }
                    CardStatus.ACTIVATED.ordinal -> {
                        updateColors(CardStatus.NOT_REGISTERED)
                        status.text = context.getString(CardStatus.ACTIVATED.stringResId)
                    }
                    CardStatus.DELETED.ordinal -> {
                        status.text = context.getString(CardStatus.DELETED.stringResId)
                    }
                }
                swipe.showMode = SwipeLayout.ShowMode.LayDown
                delete.onClick {
                    itemDeleteClick(card)
                }
                swipe.addDrag(SwipeLayout.DragEdge.Left, deleteWrapper)
            }
        }

        fun updateColors(cardStatus: CardStatus) {
            itemView.apply {
                cardView.setCardBackgroundColorByContextCompat(cardStatus.backgroundColor())
                cardContent.setBackgroundColorByContextCompat(cardStatus.backgroundColor())
                name.setTextColorByContextCompat(cardStatus.textColor())
                no.setTextColorByContextCompat(cardStatus.textColor())
                status.visibility = if (cardStatus == CardStatus.ACTIVATED) View.INVISIBLE else View.VISIBLE
            }
        }
    }

}
