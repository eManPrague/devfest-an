package cz.eman.android.devfest.addon.game

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import cz.eman.android.devfest.addon.game.adapter.GamePagerAdapter
import cz.eman.android.devfest.addon.game.core.firebase.getCorrectDisplayName
import cz.eman.android.devfest.addon.game.function.leaderboard.ui.LeaderBoardFragment
import cz.eman.android.devfest.addon.game.function.profile.ui.ProfileFragment
import cz.eman.android.devfest.lib.app.funtions.user.model.DevFestUser
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.toast
import uk.co.chrisjenx.calligraphy.CalligraphyUtils


/**
 * @author eMan s.r.o. (vaclav.souhrada@eman.cz)
 */
class GameActivity : BaseGameActivity() {

    companion object {
        private const val PROFILE_VIEW_POSITION = 0
        private const val LEADERBOARD_VIEW_POSITION = 1
        private const val SAVED_ARG_SELECTED_TAB = "selectedTab"
    }

    override val contentLayout = R.layout.activity_game

    private var selectedTab = 0

    // private lateinit var component: DaggerGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(SAVED_ARG_SELECTED_TAB, PROFILE_VIEW_POSITION)
        }
    }

    override fun onLoginSuccess() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val dbref = FirebaseDatabase.getInstance().getReference("users")
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot?) {
                    if (snapshot?.hasChild(currentUser.uid)!!) {
                        showGameView()
                        dbref.removeEventListener(this)
                    } else {
                        val email = currentUser.email ?: ""
                        val name = currentUser.getCorrectDisplayName()
                        val photoUrl = currentUser.photoUrl.toString()

                        if (name.isNotEmpty() || email.isNotEmpty()) {
                            saveUserIfNeeded(name, email, photoUrl, currentUser.uid)
                        } else {
                            toast(getString(R.string.game_login_invalid))
                            finish()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError?) {
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedTab = tabLayout.selectedTabPosition
        outState.putInt(SAVED_ARG_SELECTED_TAB, selectedTab)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        selectedTab = savedInstanceState.getInt(SAVED_ARG_SELECTED_TAB, PROFILE_VIEW_POSITION);
    }

    private fun saveUserIfNeeded(name: String, email: String, photoUrl: String?, id: String) {
        FirebaseDatabase.getInstance().getReference("users/$id")
                .setValue(DevFestUser(name = name, email = email, photoUrl = photoUrl ?: "null"))
    }

    private fun showGameView() {
        setupViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(selectedTab)?.select()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                updateScreenTitle(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

        })

        changeFontInViewGroup(tabLayout, "fonts/courier.ttf")
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = GamePagerAdapter(supportFragmentManager)
        adapter.addFragment(ProfileFragment(), getString(R.string.tabs_button_profile))
        adapter.addFragment(LeaderBoardFragment(), getString(R.string.leaderboard_text_header))

        updateScreenTitle(PROFILE_VIEW_POSITION);

        viewPager.adapter = adapter
    }

    fun changeFontInViewGroup(viewGroup: ViewGroup, fontPath: String) {
        for (i in 0..viewGroup.getChildCount() - 1) {
            val child = viewGroup.getChildAt(i)
            if (TextView::class.java.isAssignableFrom(child.javaClass)) {
                CalligraphyUtils.applyFontToTextView(child.getContext(), child as TextView, fontPath)
            } else if (ViewGroup::class.java.isAssignableFrom(child.javaClass)) {
                changeFontInViewGroup(viewGroup.getChildAt(i) as ViewGroup, fontPath)
            }
        }
    }

    private fun updateScreenTitle(position: Int) {
        when (position) {
            PROFILE_VIEW_POSITION -> screenTitleRes = R.string.profile_text_header
            LEADERBOARD_VIEW_POSITION -> screenTitleRes = R.string.leaderboard_text_header
            else -> screenTitle = ""
        }
    }
}



