package com.software.ssp.erkc.modules.drawer

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.autopayments.AutoPaymentsTabFragment
import com.software.ssp.erkc.modules.contacts.ContactsFragment
import com.software.ssp.erkc.modules.mainscreen.MainScreenFragment
import com.software.ssp.erkc.modules.paymentscreen.PaymentScreenFragment
import com.software.ssp.erkc.modules.settings.SettingsFragment
import com.software.ssp.erkc.modules.signin.SignInActivity
import com.software.ssp.erkc.modules.signup.SignUpActivity
import com.software.ssp.erkc.modules.userprofile.UserProfileActivity
import com.software.ssp.erkc.modules.valuetransfer.ValueTransferFragment
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.drawer_header_layout.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject
import kotlin.properties.Delegates

class DrawerActivity : MvpActivity(), IDrawerView {

    @Inject lateinit var presenter: IDrawerPresenter

    private var drawerToggle: ActionBarDrawerToggle? = null
    private var selectedDrawerItem: DrawerItem = DrawerItem.MAIN
    private var isSelectedDrawerItemChanged = false
    private var drawerHeaderView: View by Delegates.notNull<View>()

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerDrawerComponent.builder()
                .appComponent(appComponent)
                .drawerModule(DrawerModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        initViews()

        if (savedInstanceState != null) {
            selectedDrawerItem = DrawerItem.values()[savedInstanceState.getInt(Constants.KEY_SELECTED_DRAWER_ITEM, DrawerItem.MAIN.ordinal)]
        }

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(Constants.KEY_SELECTED_DRAWER_ITEM, selectedDrawerItem.ordinal)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle!!.isDrawerIndicatorEnabled &&
                drawerToggle!!.onOptionsItemSelected(item)) {
            return true
        } else if (item.itemId == android.R.id.home &&
                fragmentManager.popBackStackImmediate()) {
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            UserProfileActivity.USER_PROFILE_UPDATED -> presenter.onUserProfileUpdated()
            SignInActivity.DID_SIGN_IN -> presenter.onUserProfileUpdated()
            SignUpActivity.DID_SIGN_UP -> presenter.onUserProfileUpdated()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showUserInfo(user: RealmUser) {
        drawerHeaderView.drawerUserNameTextView.text = user.name
        drawerHeaderView.drawerEmailTextView.text = user.email
    }

    override fun clearUserInfo() {
        drawerHeaderView.drawerUserNameTextView.text = ""
        drawerHeaderView.drawerEmailTextView.text = ""
    }

    override fun setAuthedMenuVisible(isVisible: Boolean) {
        val menu = drawerNavigationView.menu

        menu.findItem(DrawerItem.CARDS.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.HISTORY.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.AUTOPAY.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.NOTIFY.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.SETTINGS.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.EXIT.itemId).isVisible = isVisible

        drawerHeaderView.isEnabled = isVisible
    }

    override fun navigateToMainScreen() {
        selectedDrawerItem = DrawerItem.MAIN
        drawerNavigationView.setCheckedItem(DrawerItem.MAIN.itemId)
        navigateToModule(DrawerItem.MAIN)
    }

    override fun updateCurrentScreen() {
        navigateToModule(selectedDrawerItem)
    }

    override fun navigateToUserProfile() {
        startActivityForResult<UserProfileActivity>(UserProfileActivity.USER_PROFILE_TAG)
    }

    private fun navigateToModule(drawerItem: DrawerItem) {
        val fragment = when (drawerItem) {
            DrawerItem.MAIN -> MainScreenFragment()
            DrawerItem.PAYMENT -> PaymentScreenFragment()
            DrawerItem.VALUES -> ValueTransferFragment()
            DrawerItem.CARDS -> Fragment()
            DrawerItem.HISTORY -> Fragment()
            DrawerItem.AUTOPAY -> AutoPaymentsTabFragment()
            DrawerItem.NOTIFY -> Fragment()
            DrawerItem.SETTINGS -> SettingsFragment()
            DrawerItem.TUTORIAL -> Fragment()
            DrawerItem.CONTACTS -> ContactsFragment()
            DrawerItem.EXIT -> {
                presenter.onLogoutClick()
                return
            }
        }

        isSelectedDrawerItemChanged = false

        fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, fragment)
                .commit()
    }

    private fun initViews() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        drawerHeaderView = drawerNavigationView.getHeaderView(0)

        drawerHeaderView.onClick {
            drawerLayout.closeDrawers()
            presenter.onUserProfileClick()
        }

        drawerNavigationView.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            isSelectedDrawerItemChanged = (id != selectedDrawerItem.itemId)
            if (isSelectedDrawerItemChanged) {
                when (id) {
                    DrawerItem.MAIN.itemId -> selectedDrawerItem = DrawerItem.MAIN
                    DrawerItem.PAYMENT.itemId -> selectedDrawerItem = DrawerItem.PAYMENT
                    DrawerItem.VALUES.itemId -> selectedDrawerItem = DrawerItem.VALUES
                    DrawerItem.CARDS.itemId -> selectedDrawerItem = DrawerItem.CARDS
                    DrawerItem.HISTORY.itemId -> selectedDrawerItem = DrawerItem.HISTORY
                    DrawerItem.AUTOPAY.itemId -> selectedDrawerItem = DrawerItem.AUTOPAY
                    DrawerItem.NOTIFY.itemId -> selectedDrawerItem = DrawerItem.NOTIFY
                    DrawerItem.SETTINGS.itemId -> selectedDrawerItem = DrawerItem.SETTINGS
                    DrawerItem.TUTORIAL.itemId -> selectedDrawerItem = DrawerItem.TUTORIAL
                    DrawerItem.CONTACTS.itemId -> selectedDrawerItem = DrawerItem.CONTACTS
                    DrawerItem.EXIT.itemId -> selectedDrawerItem = DrawerItem.EXIT
                }

                supportActionBar?.title = getString(selectedDrawerItem.titleId)
            }
            drawerLayout.closeDrawers()
            true
        }

        drawerToggle = object : ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open_text,
                R.string.drawer_close_text) {

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                if (isSelectedDrawerItemChanged) {
                    navigateToModule(selectedDrawerItem)
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }

        drawerLayout.addDrawerListener(drawerToggle as ActionBarDrawerToggle)
        drawerToggle?.syncState()

        drawerNavigationView.setCheckedItem(selectedDrawerItem.itemId)
        supportActionBar?.title = getString(selectedDrawerItem.titleId)
        navigateToModule(selectedDrawerItem)

        fragmentManager.addOnBackStackChangedListener {
            drawerToggle?.isDrawerIndicatorEnabled = fragmentManager.backStackEntryCount == 0
        }
    }
}
