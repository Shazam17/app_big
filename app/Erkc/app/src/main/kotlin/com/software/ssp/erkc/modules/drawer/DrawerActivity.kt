package com.software.ssp.erkc.modules.drawer

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.ErkcApplication
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.autopayments.AutoPaymentsTabFragment
import com.software.ssp.erkc.modules.card.cards.CardsFragment
import com.software.ssp.erkc.modules.contacts.ContactsFragment
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.KEY_PIN
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity.PREFERENCES
import com.software.ssp.erkc.modules.history.HistoryTabFragment
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import com.software.ssp.erkc.modules.instructions.InstructionsListFragment
import com.software.ssp.erkc.modules.longrunningupdate.LongRunningUpdateService
import com.software.ssp.erkc.modules.mainscreen.MainScreenFragment
import com.software.ssp.erkc.modules.notifications.notificationslist.NotificationsListFragment
import com.software.ssp.erkc.modules.paymentscreen.PaymentScreenFragment
import com.software.ssp.erkc.modules.photoservice.PhotoService
import com.software.ssp.erkc.modules.request.RequestTabFragment
import com.software.ssp.erkc.modules.request.nonauthedrequest.RequestNonAuthFragment
import com.software.ssp.erkc.modules.settings.SettingsFragment
import com.software.ssp.erkc.modules.signin.SignInActivity
import com.software.ssp.erkc.modules.signup.SignUpActivity
import com.software.ssp.erkc.modules.splash.SplashActivity
import com.software.ssp.erkc.modules.transaction.TransactionTabFragment
import com.software.ssp.erkc.modules.userprofile.UserProfileActivity
import com.software.ssp.erkc.modules.valuetransfer.ValueTransferFragment
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.layout_drawer_header.view.*
import org.jetbrains.anko.*
import javax.inject.Inject

class DrawerActivity : MvpActivity(), IDrawerView {

    @Inject
    lateinit var presenter: IDrawerPresenter

    @Inject lateinit var activeSession: ActiveSession

    private var nonAuthImitation = false
    private var navigateToLogin = false
    //private lateinit var fastAuthItem: MenuItem

    lateinit private var drawerToggle: ActionBarDrawerToggle
    lateinit private var drawerHeaderView: View

    var login = ""

    private var selectedDrawerItem: DrawerItem by extras(Constants.KEY_SELECTED_DRAWER_ITEM, defaultValue = DrawerItem.MAIN)
    private var isSelectedDrawerItemChanged = false

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

        nonAuthImitation = false
        navigateToLogin = false

        if (intent.extras != null) {
            nonAuthImitation = intent.extras.getBoolean("nonAuthImitation")
            navigateToLogin = intent.extras.getBoolean("navigateToLogin")
            intent.extras.clear()
        }

        initViews()

        if (selectedDrawerItem != DrawerItem.MAIN && savedInstanceState != null) {
            selectedDrawerItem = DrawerItem.values()[savedInstanceState.getInt(Constants.KEY_SELECTED_DRAWER_ITEM, DrawerItem.MAIN.ordinal)]
        }

        if (nonAuthImitation)
            presenter.setNonAuthImitation()

        presenter.onViewAttached()
        startService(Intent(this, PhotoService::class.java))
        startService(Intent(this, LongRunningUpdateService::class.java))
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        getDataProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val drawerItem = intent.getSerializableExtra(Constants.KEY_SELECTED_DRAWER_ITEM) as DrawerItem?

        drawerItem?.let {
            navigateToDrawerItem(it)
        }
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(Constants.KEY_SELECTED_DRAWER_ITEM, selectedDrawerItem.ordinal)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            searchView.isSearchOpen -> searchView.closeSearch()
            else -> presenter.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.isDrawerIndicatorEnabled &&
                drawerToggle.onOptionsItemSelected(item)) {
            return true
        } else if (item.itemId == android.R.id.home &&
                fragmentManager.popBackStackImmediate()) {
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            UserProfileActivity.USER_PROFILE_REQUEST_CODE -> presenter.onUserProfileUpdated()
            SignInActivity.SIGN_IN_REQUEST_CODE -> presenter.onUserProfileUpdated()
            SignUpActivity.SIGN_UP_REQUEST_CODE -> presenter.onUserProfileUpdated()
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

        menu.findItem(DrawerItem.PAYMENT.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.VALUES.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.CARDS.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.HISTORY.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.AUTOPAY.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.NOTIFY.itemId).isVisible = isVisible
        menu.findItem(DrawerItem.TRANSACTION.itemId).isVisible = isVisible
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
        startActivityForResult<UserProfileActivity>(UserProfileActivity.USER_PROFILE_REQUEST_CODE)
    }

    override fun navigateToSplashScreen() {
        startActivity<SplashActivity>()
        this.finish()
    }

    override fun navigateToHistory(receiptCode: String) {
        //Untypical navigation to history screen with args
        selectedDrawerItem = DrawerItem.HISTORY
        drawerNavigationView.setCheckedItem(selectedDrawerItem.itemId)
        supportActionBar?.title = getString(selectedDrawerItem.titleId)
        fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, HistoryTabFragment().withArguments("historyFilter" to HistoryFilterModel(barcode = receiptCode)))
                .commitAllowingStateLoss()
    }

    override fun setUserLogin(login: String) {
        this.login = login
        ErkcApplication.login = login
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun navigateBack() {
        System.exit(0)
        //super.onBackPressed()
    }

    private fun navigateToModule(drawerItem: DrawerItem) {
        val fragment: Fragment = when (drawerItem) {
            DrawerItem.MAIN -> {
                if (nonAuthImitation) {
                    setAuthedMenuVisible(false)
                    isSelectedDrawerItemChanged = false

                    clearUserInfo()

                    val fragment = MainScreenFragment()
                    val bundle = Bundle()
                    bundle.putBoolean("nonAuthImitation", true)
                    bundle.putBoolean("navigateToLogin", navigateToLogin)
                    fragment.arguments = bundle

                    fragmentManager.beginTransaction()
                            .replace(R.id.drawerFragmentContainer, fragment)
                            .commitAllowingStateLoss()
                    return
                } else {
                    MainScreenFragment()
                }
            }
            // TODO Test non auth request screen
            DrawerItem.REQUEST ->  {
                if (activeSession.accessToken == "") {
                    RequestNonAuthFragment()
                } else {
                    RequestTabFragment()
                }
            }
            DrawerItem.PAYMENT -> PaymentScreenFragment()
            DrawerItem.VALUES -> ValueTransferFragment()
            DrawerItem.CARDS -> CardsFragment()
            DrawerItem.HISTORY -> HistoryTabFragment()
            DrawerItem.AUTOPAY -> AutoPaymentsTabFragment()
            DrawerItem.TRANSACTION -> TransactionTabFragment()
            DrawerItem.NOTIFY -> NotificationsListFragment()
            DrawerItem.SETTINGS -> SettingsFragment()
            DrawerItem.TUTORIAL -> InstructionsListFragment()
            DrawerItem.CONTACTS -> ContactsFragment()
            DrawerItem.EXIT -> {
                presenter.onLogoutClick()
                return
            }
        }

        isSelectedDrawerItemChanged = false

        fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, fragment)
                .commitAllowingStateLoss()
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
                    DrawerItem.REQUEST.itemId -> selectedDrawerItem = DrawerItem.REQUEST
                    DrawerItem.VALUES.itemId -> selectedDrawerItem = DrawerItem.VALUES
                    DrawerItem.CARDS.itemId -> selectedDrawerItem = DrawerItem.CARDS
                    DrawerItem.HISTORY.itemId -> selectedDrawerItem = DrawerItem.HISTORY
                    DrawerItem.AUTOPAY.itemId -> selectedDrawerItem = DrawerItem.AUTOPAY
                    DrawerItem.NOTIFY.itemId -> selectedDrawerItem = DrawerItem.NOTIFY
                    DrawerItem.TRANSACTION.itemId -> selectedDrawerItem = DrawerItem.TRANSACTION
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

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        drawerNavigationView.setCheckedItem(selectedDrawerItem.itemId)
        supportActionBar?.title = getString(selectedDrawerItem.titleId)
        navigateToModule(selectedDrawerItem)

        fragmentManager.addOnBackStackChangedListener {
            drawerToggle.isDrawerIndicatorEnabled = fragmentManager.backStackEntryCount == 0
        }
    }

    override fun navigateToDrawerItem(drawerItem: DrawerItem) {
        selectedDrawerItem = drawerItem
        drawerNavigationView.setCheckedItem(selectedDrawerItem.itemId)
        supportActionBar?.title = getString(selectedDrawerItem.titleId)
        navigateToModule(selectedDrawerItem)
    }
}
