package cz.eman.android.devfest.lib.app.funtions.user

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 */
interface IUserRepository {

    fun saveUserIfNeeded(name: String, email: String, photoUrl: Uri?, id: String)

}