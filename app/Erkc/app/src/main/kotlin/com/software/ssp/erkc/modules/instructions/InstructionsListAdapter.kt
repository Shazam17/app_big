package com.software.ssp.erkc.modules.instructions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import kotlinx.android.synthetic.main.item_instruction.view.*


class InstructionsListAdapter(val instructions: Array<InstructionType>, val listener: InteractionListener? = null) : RecyclerView.Adapter<InstructionsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_instruction, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindInstruction(instructions[position])
    }

    override fun getItemCount(): Int {
        return instructions.count()
    }

    class ViewHolder(itemView: View, val listener: InteractionListener?) : RecyclerView.ViewHolder(itemView) {
        fun bindInstruction(instructionType: InstructionType) {
            itemView.apply {
                instructionText.setText(instructionType.titleId)
                setOnClickListener { listener?.onInstructionClick(instructionType) }
            }
        }
    }

    interface InteractionListener {
        fun onInstructionClick(instructionType: InstructionType)
    }
}
