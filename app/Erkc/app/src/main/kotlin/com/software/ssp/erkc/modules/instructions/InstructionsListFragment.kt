package com.software.ssp.erkc.modules.instructions


import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.modules.instructions.instructiondetails.InstructionActivity
import kotlinx.android.synthetic.main.fragment_instructions_list.*
import org.jetbrains.anko.startActivity


class InstructionsListFragment : Fragment(), InstructionsListAdapter.InteractionListener {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_instructions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onInstructionClick(instructionType: InstructionType) {
        startActivity<InstructionActivity>("instructionType" to instructionType)
    }

    private fun initViews() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = InstructionsListAdapter(InstructionType.values(), this)
    }
}
