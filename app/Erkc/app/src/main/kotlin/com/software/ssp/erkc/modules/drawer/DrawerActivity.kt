package com.software.ssp.erkc.modules.drawer

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.User
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.contacts.ContactsFragment
import com.software.ssp.erkc.modules.mainscreen.nonauthedmainscreen.NonAuthedMainScreenFragment
import kotlinx.android.synthetic.main.activity_drawer.*
import org.jetbrains.anko.onClick
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

    enum class DrawerItem(@StringRes val titleId: Int, @IdRes val itemId: Int) {
        MAIN(R.string.drawer_main_screen_text, R.id.menuMainScreen),
        PAYMENT(R.string.drawer_payment_text, R.id.menuPayment),
        VALUES(R.string.drawer_send_values_text, R.id.menuSendValues),
        CARDS(R.string.drawer_my_cards_text, R.id.menuMyCards),
        HISTORY(R.string.drawer_history_text, R.id.menuHistory),
        AUTOPAY(R.string.drawer_auto_payment_text, R.id.menuAutoPayments),
        NOTIFY(R.string.drawer_notifications_text, R.id.menuNotifications),
        SETTINGS(R.string.drawer_settings_text, R.id.menuSettings),
        TUTORIAL(R.string.drawer_instruction_text, R.id.menuInstructions),
        CONTACTS(R.string.drawer_contacts_text, R.id.menuContacts),
        EXIT(R.string.drawer_exit_text, R.id.menuExit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        initViews()

        if (savedInstanceState != null) {
            selectedDrawerItem = DrawerItem.values()[savedInstanceState.getInt(Constants.KEY_SELECTED_DRAWER_ITEM, DrawerItem.MAIN.ordinal)]
        }

        drawerNavigationView.setCheckedItem(selectedDrawerItem.itemId)
        supportActionBar?.title = getString(selectedDrawerItem.titleId)
        navigateToModule(selectedDrawerItem)

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle?.syncState()
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
        if (drawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showUserInfo(user: User) {
        (drawerHeaderView.findViewById(R.id.drawerUserNameTextView) as TextView).text = user.name
        (drawerHeaderView.findViewById(R.id.drawerEmailTextView) as TextView).text = user.email
    }

    override fun clearUserInfo() {
        (drawerHeaderView.findViewById(R.id.drawerUserNameTextView) as TextView).text = ""
        (drawerHeaderView.findViewById(R.id.drawerEmailTextView) as TextView).text = ""
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

    private fun navigateToModule(drawerItem: DrawerItem) {
        val fragment = when (drawerItem) {
            DrawerItem.MAIN -> NonAuthedMainScreenFragment()
            DrawerItem.PAYMENT -> Fragment()
            DrawerItem.VALUES -> Fragment()
            DrawerItem.CARDS -> Fragment()
            DrawerItem.HISTORY -> Fragment()
            DrawerItem.AUTOPAY -> Fragment()
            DrawerItem.NOTIFY -> Fragment()
            DrawerItem.SETTINGS -> Fragment()
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

        drawerHeaderView = drawerNavigationView.getHeaderView(0)

        drawerHeaderView.onClick {
            drawerLayout.closeDrawers()
            //TODO Navigate To UserProfile
        }

        drawerNavigationView.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            isSelectedDrawerItemChanged = (id != selectedDrawerItem.itemId)
            if (isSelectedDrawerItemChanged) {
                when (id) {
                    R.id.menuMainScreen -> selectedDrawerItem = DrawerItem.MAIN
                    R.id.menuPayment -> selectedDrawerItem = DrawerItem.PAYMENT
                    R.id.menuSendValues -> selectedDrawerItem = DrawerItem.VALUES
                    R.id.menuMyCards -> selectedDrawerItem = DrawerItem.CARDS
                    R.id.menuHistory -> selectedDrawerItem = DrawerItem.HISTORY
                    R.id.menuAutoPayments -> selectedDrawerItem = DrawerItem.AUTOPAY
                    R.id.menuNotifications -> selectedDrawerItem = DrawerItem.NOTIFY
                    R.id.menuSettings -> selectedDrawerItem = DrawerItem.SETTINGS
                    R.id.menuInstructions -> selectedDrawerItem = DrawerItem.TUTORIAL
                    R.id.menuContacts -> selectedDrawerItem = DrawerItem.CONTACTS
                    R.id.menuExit -> selectedDrawerItem = DrawerItem.EXIT
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
    }
}
