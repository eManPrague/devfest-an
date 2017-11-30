package cz.eman.android.devfest.addon.game.function.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.core.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_game_finish_top_ten.*

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * GameFinishTopTenFragment is Fragment which is being showed when  player has finished the game
 * and has reached TOP 10 status
 */
class GameFinishTopTenFragment : BaseFragment() {

    val privateKeyRef = FirebaseDatabase.getInstance().getReference("cdhPrivateKey")

    var privateKeyListener: ValueEventListener? = null

    /**
     * Overriden function inflating this GameFinishTopTenFragment's View
     *
     * @param inflater Inflater which inflates this GameFinishTopTenFragment's View
     * @param container Container which holds this GameFinishTopTenFragments's inflated View
     * @param savedInstanceState Bundle holding saved data for this GameFinishTopTenFragment
     * @return This GameFinishTopTenFragments's inflated View
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_game_finish_top_ten, container, false)
    }

    /**
     * Overriden function starting this Fragment and showing private key
     */
    override fun onStart() {
        super.onStart()
        showPrivateKey()
    }

    /**
     * Overriden function stopping this Fragment and removing all listeners from this GameFinishTopTenFragment's
     * DatabaseReference
     */
    override fun onStop() {
        super.onStop()
        if (privateKeyListener != null)
            privateKeyRef.removeEventListener(privateKeyListener)
    }

    /**
     * Reads cdhPrivateKey from Firebase and shows it to player
     */
    private fun showPrivateKey() {
        privateKeyListener = privateKeyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot == null) return
                deactivationCodeText.text = dataSnapshot.value.toString()
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

}
