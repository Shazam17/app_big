package com.software.ssp.erkc.modules.instructions.instructiondetails

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.modules.instructions.InstructionType

class InstructionActivity : AppCompatActivity() {

    private val instructionType: InstructionType by extras(defaultValue = InstructionType.AUTO_PAYMENTS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_with_container)
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    private fun showFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.containerLayout, fragment)
                .commit()
    }

    fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(instructionType.titleId)

        when (instructionType) {
            InstructionType.NON_AUTH_SEND_VALUES -> showFragment(Instructions1Fragment())
            InstructionType.NON_AUTH_TITLE -> showFragment(Instructions2Fragment())
            InstructionType.PAYMENT_ADD_RECEIPT -> showFragment(Instructions2Fragment())
            InstructionType.SEND_VALUES_ADD_RECEIPT -> showFragment(Instructions1Fragment())
            InstructionType.SEND_VALUES -> showFragment(Instructions3Fragment())
            InstructionType.PAYMENT -> showFragment(Instructions4Fragment())
            InstructionType.PAYMENT_WITH_CARD -> showFragment(Instructions5Fragment())
            InstructionType.CARDS -> showFragment(Instructions6Fragment())
            InstructionType.AUTO_PAYMENTS -> showFragment(Instructions7Fragment())
            InstructionType.HISTORY_PAYMENT -> showFragment(Instructions8Fragment())
            InstructionType.HISTORY_SEND_VALUES -> showFragment(Instructions9Fragment())
            InstructionType.NOTIFICATIONS -> showFragment(Instructions10Fragment())
        }
    }
}
