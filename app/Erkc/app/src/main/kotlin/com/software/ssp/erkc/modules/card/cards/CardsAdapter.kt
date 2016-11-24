package com.software.ssp.erkc.modules.card.cards

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.extensions.*
import kotlinx.android.synthetic.main.item_card.view.*
import org.jetbrains.anko.onClick

/**
 * @author Alexander Popov on 28/10/2016.
 */
class CardsAdapter(val cards: List<CardViewModel>, val listener: InteractionListener? = null) : RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindCard(cards[position])
    }

    override fun getItemCount(): Int {
        return cards.count()
    }

    class ViewHolder(itemView: View, val listener: InteractionListener?) : RecyclerView.ViewHolder(itemView) {
        fun bindCard(cardViewModel: CardViewModel) {
            itemView.apply {
                cardListItemNameTextView.text = cardViewModel.card.name
                cardListItemNoTextView.text = cardViewModel.card.maskedCardNumber

                val cardStatus = CardStatus.values()[cardViewModel.card.statusId]
                cardView.setCardBackgroundColorByContextCompat(cardStatus.backgroundColor())
                cardListItemCardContent.setBackgroundColorByContextCompat(cardStatus.backgroundColor())
                cardListItemNameTextView.setTextColorByContextCompat(cardStatus.nameColor())
                cardListItemNoTextView.setTextColorByContextCompat(cardStatus.nameColor())
                cardDivider.setBackgroundColorByContextCompat(cardStatus.dividerColor())
                cardListItemStatusTextView.visibility = if (cardStatus == CardStatus.ACTIVATED) View.INVISIBLE else View.VISIBLE
                cardListItemStatusTextView.setText(cardStatus.stringResId)
                cardListItemEditImageView.setImageResource(if (cardStatus == CardStatus.ACTIVATED) R.drawable.ic_edit_white else R.drawable.ic_edit_grey)

                cardListItemEditImageView.onClick {
                    listener?.itemEditClick(cardViewModel.card)
                }

                cardListItemStatusTextView.onClick {
                    listener?.itemByStatusClick(cardViewModel.card)
                }

                deleteButton.onClick {
                    cardListItemEditImageView.isEnabled = false
                    cardListItemStatusTextView.isEnabled = false
                    deleteButton.isEnabled = false
                    deleteProgressBar.visibility = View.VISIBLE
                    cardViewModel.isDeletePending = true
                    listener?.itemDeleteClick(cardViewModel.card)
                }

                cardListItemEditImageView.isEnabled = !(cardViewModel.isDeletePending || cardViewModel.isRequestPending)
                cardListItemStatusTextView.isEnabled = !(cardViewModel.isDeletePending || cardViewModel.isRequestPending)
                deleteButton.isEnabled = !(cardViewModel.isDeletePending || cardViewModel.isRequestPending)
                deleteProgressBar.visibility = if(cardViewModel.isDeletePending) View.VISIBLE else View.GONE
                cardRequestProgressBar.visibility = if(cardViewModel.isRequestPending) View.VISIBLE else View.INVISIBLE

                if(cardViewModel.isDeletePending){
                    swipeLayout.offset = -60.dp
                } else {
                    swipeLayout.reset()
                }
            }
        }
    }

    interface InteractionListener {
        fun itemEditClick(card: RealmCard)
        fun itemByStatusClick(card: RealmCard)
        fun itemDeleteClick(card: RealmCard)
    }
}
