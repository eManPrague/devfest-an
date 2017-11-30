package cz.eman.android.devfest.addon.game.function.profile.ui

import android.content.Intent
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
import cz.eman.android.devfest.addon.game.function.repository.GameRepository
import cz.eman.android.devfest.addon.game.function.repository.model.KeyPart
import kotlinx.android.synthetic.main.fragment_profile_keys.*

import com.github.salomonbrys.kodein.*
import com.google.firebase.auth.FirebaseAuth
import cz.eman.android.devfest.addon.game.function.gameplay.ui.AddKeyActivity

/**
 * ProfileKeysFragment is shown when player doesn't have all keys yet
 *
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 * @see[BaseFragment]
 */
class ProfileKeysFragment : BaseFragment(), View.OnClickListener {

    private val gameRepository: GameRepository by instance<GameRepository>()

    lateinit var adapter: ProfileAdapter
    lateinit var listener: ValueEventListener
    var keyPartsRefListener: ValueEventListener? = null

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val playerProgressRef = FirebaseDatabase.getInstance().getReference("cdhProgress/$uid")
    private val cdhPrivateKeyPartsRef = FirebaseDatabase.getInstance().getReference("cdhPrivateKeyParts")

    /**
     * Inflates this ProfileKeysFragment's layout and returns it as View
     *
     * @param inflater Inflater inflating this ProfileKeysFragment's layout
     * @param container ViewGroup which holds inflated layout as View
     * @param savedInstanceState Bundle holding saved data
     * @return View inflated by inflater
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_profile_keys, container, false)
    }

    /**
     * Sets this ProfileKeysFragment's Views and their function, connects to Firebase and
     * creates RecyclerView with remaining keys
     *
     * @param savedInstanceState Bundle which holds saved data
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addKeyButton.setOnClickListener(this)
        remainingRecyclerView.layoutManager = LinearLayoutManager(activity)

        listener = playerProgressRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(playerProgressSnapshot: DataSnapshot?) {
                if (playerProgressSnapshot == null) return
                var ownedKeyParts = 0
                keyPartsRefListener = cdhPrivateKeyPartsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(cdhPrivateKeyPartsSnapshot: DataSnapshot?) {
                        if (cdhPrivateKeyPartsSnapshot == null) return
                        for (snapshot in playerProgressSnapshot.children) {
                            if (cdhPrivateKeyPartsSnapshot.hasChild(snapshot.child("privateKeyPart").value.toString())) {
                                ownedKeyParts++
                            }
                        }
                        var totalKeyParts = cdhPrivateKeyPartsSnapshot.children.count()
                        keyCountText.text = getString(R.string.profile_text_keys) + " $ownedKeyParts/$totalKeyParts"
                        if (ownedKeyParts == totalKeyParts) {
                            if (activity.supportFragmentManager.findFragmentById(R.id.profileBottomContainer) !is KeysCompletedFragment) {
                                activity.supportFragmentManager.beginTransaction()
                                        .replace(R.id.profileBottomContainer, KeysCompletedFragment(), "keysCompletedFragment")
                                        .commitAllowingStateLoss()
                            }
                        }
                        // ============================================================================
                        val keyParts: MutableList<KeyPart> = mutableListOf()
                        val progressList: MutableList<String> = mutableListOf()
                        val cdhPrivateKeysList: MutableList<String> = mutableListOf()
                        for (snapshot in cdhPrivateKeyPartsSnapshot.children)
                            cdhPrivateKeysList.add(snapshot.key)
                        for (snapshot in playerProgressSnapshot.children)
                            progressList.add(snapshot.child("privateKeyPart").value.toString())

                        val remainingKeys = cdhPrivateKeysList.minus(progressList)
                        for (snapshot in cdhPrivateKeyPartsSnapshot.children) {
                            if (remainingKeys.contains(snapshot.key)) {
                                val location = snapshot.child("location").value.toString()
                                val controlWord = snapshot.child("controlWord").value.toString()
                                val keyPart = KeyPart(controlWord = if (controlWord != null) controlWord else "null"
                                        , location = if (location != null) location else "null")

                                keyParts.add(keyPart)
                            }
                        }
                        if (activity == null) return
                        adapter = ProfileAdapter(keyParts, activity)
                        remainingRecyclerView.adapter = adapter
                    }
                    override fun onCancelled(p0: DatabaseError?) {
                        return
                    }
                })
            }
            override fun onCancelled(p0: DatabaseError?) {
                return
            }
        })
    }

    /**
     * Destroys this ProfileKeysFragment's View and removes EventListener from DatabaseReference
     */
    override fun onDestroyView() {
        super.onDestroyView()
        if (keyPartsRefListener != null) {
            cdhPrivateKeyPartsRef.removeEventListener(keyPartsRefListener)
        }
        playerProgressRef.removeEventListener(listener)
    }

    /**
     * Event handling for button
     *
     * @param view View as source of click event
     */
    override fun onClick(view: View?) {
        addKey()
    }

    /**
     * Starts AddKeyActivity (@see [AddKeyActivity])
     */
    private fun addKey() {
        val intent = Intent(context, AddKeyActivity::class.java)
        startActivity(intent)
    }
}