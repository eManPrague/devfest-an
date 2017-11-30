package cz.eman.android.devfest.lib.app.funtions.user

import android.net.Uri
import com.google.firebase.database.*
import cz.eman.android.devfest.lib.app.funtions.user.model.DevFestUser
import javax.inject.Inject

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 * @see[IUserRepository]
 */
class UserRepository : IUserRepository {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.apply { keepSynced(true) }
    }

    override fun saveUserIfNeeded(name: String, email: String, photoUrl: Uri?, id: String) {
        users().child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    users().child(id).setValue(DevFestUser(name = name, email = email,
                            photoUrl = photoUrl?.toString() ?: "null"))

                    cdhProgress().child(id).setValue("dummy progress")
                } else if (name != snapshot.child("name").toString()) {
                    users().child(id).updateChildren(mutableMapOf<String, Any>(Pair("name", name)))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun users() = database.child("users")

    private fun cdhProgress() = database.child("cdhProgress")
}