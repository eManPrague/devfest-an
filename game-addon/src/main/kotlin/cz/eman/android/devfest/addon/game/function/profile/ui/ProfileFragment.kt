package cz.eman.android.devfest.addon.game.function.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.core.ui.BaseFragment
import cz.eman.android.devfest.addon.game.function.repository.model.User
import kotlinx.android.synthetic.main.activity_add_key.*
import kotlinx.android.synthetic.main.fragment_game_finish_top_ten.*
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * @author eMan s.r.o.
 * @see[BaseFragment]
 *
 * ProfileFragment show player's stats like score, position etc
 */
class ProfileFragment : BaseFragment() {

    private val MAX_TOP_PLAYERS = 10

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private val query = dbRef.orderByChild("cdhScore")

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val currentUser = FirebaseAuth.getInstance().currentUser

    var queryListener: ValueEventListener? = null
    var profileStatsListener: ValueEventListener? = null
    var bottomContainerListener: ValueEventListener? = null
    var isCreated: Boolean = false;

    /**
     * Inflates this ProfileFragment's layout and returns its View
     *
     * @param inflater Inflater inflating ProfileFragment's layout
     * @param container ViewGroup which holds ProfileFragment's layout View
     * @param savedInstanceState Bundle which holds saved data
     * @return ProfileFragment's inflated View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    /**
     * Loads all necessery data and connects DatabaseReference to Firebase
     *
     * @param view View returned by onCreateView() function
     * @param savedInstanceState Bundle which holds saved data
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        getPlayerPosition()
        isCreated = true
        bottomContainerListener = dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot == null) return

                // serverTimestamp is not zero, player sorted his key parts
                    if ((dataSnapshot.child("$uid/correctOrderServerTimestamp").value as Long) != 0L){
                    val playerList: MutableList<User> = mutableListOf()
                    for (snapshot in dataSnapshot.children){
                        playerList.add(User(cdhScore = snapshot.child("cdhScore").value as Long,
                                email = snapshot.child("email").value.toString(),
                                name = snapshot.child("name").value.toString(),
                                photoUrl = snapshot.child("photoUrl").value.toString(),
                                uid = snapshot.key))
                    }
                    // getting all players, sorted by cdhScore
                    val sortedPlayers = playerList
                            .sortedWith(compareBy { it.cdhScore })
                            .reversed()
                    val player = User(cdhScore = dataSnapshot.child("$uid/cdhScore").value as Long,
                                    email = dataSnapshot.child("$uid/email").value.toString(),
                                    name = dataSnapshot.child("$uid/name").value.toString(),
                                    photoUrl = dataSnapshot.child("$uid/photoUrl").value.toString(),
                                    uid = "$uid")

                    // player is in top ten, attach GameFinishTopTenFragment
                    if (sortedPlayers.indexOf(player) < MAX_TOP_PLAYERS) {
                        if (activity.supportFragmentManager.findFragmentById(R.id.profileBottomContainer) !is GameFinishTopTenFragment || isCreated ) {
                            isCreated = false
                            activity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.profileBottomContainer, GameFinishTopTenFragment(), "gameFinishTopTenFragment")
                                    .commitAllowingStateLoss()
                        }
                    } else { // player is not in top ten, attach GameFinishFragment
                        if (activity.supportFragmentManager.findFragmentById(R.id.profileBottomContainer) !is GameFinishFragment || isCreated ) {
                            isCreated = false
                            activity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.profileBottomContainer, GameFinishFragment(), "gameFinishFragment")
                                    .commitAllowingStateLoss()
                        }
                    }
                } else if ((dataSnapshot.child("$uid/correctOrderServerTimestamp").value as Long) == 0L){
                        // serverTimestamp is 0 => player have not yet sorted his keys
                    if (activity.supportFragmentManager.findFragmentById(R.id.profileBottomContainer) !is ProfileKeysFragment || isCreated) {
                        isCreated = false
                        activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.profileBottomContainer, ProfileKeysFragment(), "playerKeysFragment")
                                .commitAllowingStateLoss()
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    /**
     * Stops this ProfileFragment and removes all listeners from DatabaseReferences
     */
    override fun onStop() {
        super.onStop()
//        if (listener != null) keyProgressRef.removeEventListener(listener)
        if (queryListener != null) query.removeEventListener(queryListener)
        if (profileStatsListener != null) dbRef.removeEventListener(profileStatsListener)
        if (bottomContainerListener != null) dbRef.removeEventListener(bottomContainerListener)
    }

    /**
     * Loads all necessary information about player
     */
    private fun loadProfile() {
        profileStatsListener = dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val userData = dataSnapshot?.child(uid)
                fullNameText.text = userData?.child("name")?.value.toString()
                pointsText.text = "${userData?.child("cdhScore")?.value.toString()} ${getString(R.string.user_text_points)}"
                Glide.with(activity).load(userData?.child("photoUrl")?.value.toString()).into(profilePicImg)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    /**
     * Connects to Firebase and calculates player's position
     */
    private fun getPlayerPosition() {
        queryListener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot != null) {
                    val total = dataSnapshot.childrenCount
                    var i = 0
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot != null && snapshot.key != null && snapshot.key == currentUser?.uid) {
                            positionText.text = "#${(total - i)}"
                            break
                        } else {
                            i++
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError?) {
                // TODO handle error case
            }
        })
    }

}