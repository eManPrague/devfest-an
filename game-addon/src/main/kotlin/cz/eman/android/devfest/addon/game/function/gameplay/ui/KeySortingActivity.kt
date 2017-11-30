package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import cz.eman.android.devfest.addon.game.BaseGameActivity
import cz.eman.android.devfest.addon.game.GameActivity
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.function.gameplay.ui.ItemTouchHelperCallback
import cz.eman.android.devfest.addon.game.function.gameplay.ui.KeySortAdapter
import cz.eman.android.devfest.addon.game.function.repository.model.User
import kotlinx.android.synthetic.main.activity_add_key.*
import kotlinx.android.synthetic.main.activity_key_sorting.*
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * KeySortingActivity manages key sorting
 */
class KeySortingActivity : BaseGameActivity(), IOnStartDragListener {

    override val contentLayout = R.layout.activity_key_sorting

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val keyPartsRef = FirebaseDatabase.getInstance().getReference("cdhPrivateKeyParts")
    val validSortRef = FirebaseDatabase.getInstance().getReference("cdhPrivateKey")
    val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
    val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

    var valueListener: ValueEventListener? = null
    var validSortListener: ValueEventListener? = null
    var connectionListener: ValueEventListener? = null

    lateinit var adapter: KeySortAdapter

    lateinit var itemTouchHelper: ItemTouchHelper

    /**
     * Overriden function sets View for this Activity, adds listener for listening
     * to Firebase, sets OnClickListener for Submit button
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenTitleRes = R.string.create_key_text_header

        completedKeysRV.layoutManager = LinearLayoutManager(this)

        valueListener = keyPartsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot == null) return
                val keysToSort: MutableList<Pair<String, String>> = mutableListOf()

                for (snapshot in dataSnapshot.children)
                    keysToSort.add(Pair(snapshot.key, snapshot.child("controlWord").value.toString()))

                adapter = KeySortAdapter(keysToSort, this@KeySortingActivity, this@KeySortingActivity)

                itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter)).apply {
                    attachToRecyclerView(completedKeysRV)
                }

                completedKeysRV.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })

        submitButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                checkKeySorting(completedKeysRV)
            }
        })
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    /**
     * Function checks if keys are correctly sorted by comparing sorted order with Firebase
     *
     * @param recyclerView RecyclerView with sorted items
     */
    fun checkKeySorting(recyclerView: RecyclerView?) {
        if (recyclerView == null) return
        val keySortAdapter = recyclerView.adapter as KeySortAdapter
        val dbRef = FirebaseDatabase.getInstance().reference
        validSortListener = validSortRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot == null) return
//                checkConnection()
                val list = keySortAdapter.getKeysToSort() as MutableList
                val privateKey = StringBuilder("")
                for (i in list.iterator())
                    privateKey.append(i.first)
                // Sorted keys are not in correct order
                if (!privateKey.toString().equals(dataSnapshot.value)) {
                    screenTitle = getString(R.string.create_key_text_header_wrong)
                    keySortingBody.text = getString(R.string.create_key_text_subheader_wrong)
                } else { // Sorted keys are in correct order
                    // Add ValueEventListener
                    connectionListener = connectedRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot?) {
                            if (snapshot == null) return
                            var connected = snapshot.value as Boolean
                            // Check connection to Firebase
                            if (connected) {
                                sendUserTimestamp()
                            } else {
                                Toast.makeText(applicationContext, R.string.network_error_no_connection_to_firebase, Toast.LENGTH_SHORT).show()
                                dbRef.removeEventListener(validSortListener)
                                connectedRef.removeEventListener(connectionListener)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })

                }

            }

            override fun onCancelled(p0: DatabaseError?) {

            }

        })
    }

    /**
     * Overriden function used to clear all listeners from DatabaseReferences
     */
    override fun onStop() {
        super.onStop()
        clearListeners()
    }

    /**
     * Function sending ServerValue timestamp  to Firebase and starting next Activity
     */
    fun sendUserTimestamp() {
        connectedRef.removeEventListener(connectionListener)
        validSortRef.removeEventListener(validSortListener)
        userRef.child("correctOrderServerTimestamp").setValue(ServerValue.TIMESTAMP)

        val intent = Intent(this@KeySortingActivity, GameActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    /**
     * Function removes listeners from DatabaseReferences
     */
    private fun clearListeners() {
        if (valueListener != null)
            keyPartsRef.removeEventListener(valueListener)
        if (validSortListener != null)
            validSortRef.removeEventListener(validSortListener)
        if (connectionListener != null)
            connectedRef.removeEventListener(connectionListener)
    }
}