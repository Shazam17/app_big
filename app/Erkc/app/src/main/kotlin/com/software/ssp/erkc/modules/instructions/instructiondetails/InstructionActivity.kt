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
            InstructionType.NON_AUTH_SEND_VALUES -> showFragment(InstructionsIpuNonAuthFragment())
            InstructionType.NON_AUTH_TITLE -> showFragment(InstructionsPaymentNonAuthFragment())
            InstructionType.PAYMENT_ADD_RECEIPT -> showFragment(InstructionsPaymentAddReceiptFragment())
            InstructionType.SEND_VALUES_ADD_RECEIPT -> showFragment(InstructionsIpuAddReceiptFragment())
            InstructionType.SEND_VALUES -> showFragment(InstructionsIpuFragment())
            InstructionType.PAYMENT -> showFragment(InstructionsPaymentFragment())
            InstructionType.PAYMENT_WITH_CARD -> showFragment(InstructionsPaymentCardFragment())
            InstructionType.CARDS -> showFragment(InstructionsCardsFragment())
            InstructionType.AUTO_PAYMENTS -> showFragment(InstructionsAutoPaymentsFragment())
            InstructionType.HISTORY_PAYMENT -> showFragment(InstructionsHistoryPaymentFragment())
            InstructionType.HISTORY_SEND_VALUES -> showFragment(InstructionsHistoryIpuFragment())
            InstructionType.NOTIFICATIONS -> showFragment(InstructionsNotificationsFragment())
        }
    }
}
