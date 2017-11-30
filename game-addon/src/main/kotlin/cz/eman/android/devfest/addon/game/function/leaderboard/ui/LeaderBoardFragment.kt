package cz.eman.android.devfest.addon.game.function.leaderboard.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.core.ui.BaseFragment
import cz.eman.android.devfest.addon.game.function.repository.model.User
import kotlinx.android.synthetic.main.fragment_leaderboard.*

/**
 * @author eMan s.r.o. (vaclav.souhrada@eman.cz)
 *
 * LeaderBoardFragment showing Leaderboard stats of the game and User stanfing in the game
 */
class LeaderBoardFragment : BaseFragment() {

    lateinit var adapter: LeaderBoardAdapter
    val dbRef = FirebaseDatabase.getInstance().getReference("users")
    lateinit var listener: ValueEventListener

    /**
     * Overriden function, inflates layout of this Fragment
     *
     * @param inflater Inflater inflating this Fragment's View
     * @param container Container into which this Fragment's View is to be inflates
     * @param savedInstanceState Bundle with saved data
     * @return This Fragment's inflated View
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_leaderboard, container, false)
    }

    /**
     * Overriden function, creates List od Users and initializes adapter for this Fragment's RecyclerView
     *
     * @param savedInstanceState Bundle with saved data
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        leaderboardStatsRV.layoutManager = LinearLayoutManager(context.applicationContext)
        adapter = LeaderBoardAdapter(activity)
        leaderboardStatsRV.adapter = adapter

        listener = dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot == null) return
                var players: MutableList<User> = mutableListOf()
                // Add all Users into List
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.child("cdhScore") == null || snapshot.child("cdhScore").value == null) {
                        // TODO - this is a workaround for situation that in db are users signed from web browser
                        continue
                    }
                    val score = snapshot.child("cdhScore").value as Long
                    val email = snapshot.child("email").value.toString()
                    val name = snapshot.child("name").value.toString()
                    val photoUrl = snapshot.child("photoUrl").value.toString()
                    val uid = snapshot.key
                    players.add(User(cdhScore = score,
                            email = email,
                            name = name,
                            photoUrl = photoUrl,
                            uid = uid))
                }
                // Players are sorted by cdhScore descending and order is reversed afterwards
                val sortedPlayers = players
                        .sortedWith(compareBy({ it.cdhScore }))
                        .reversed()

                adapter.setPlayers(sortedPlayers)
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    /**
     * Overriden function, destroying this Fragment's View and removing listeners
     * from this Fragment's DatabaseReferences
     */
    override fun onDestroyView() {
        super.onDestroyView()
        dbRef.removeEventListener(listener)
    }
}