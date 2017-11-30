package lv.rigadevday.android.utils.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStorage @Inject constructor() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val hasLogin: Boolean
        get() = auth.currentUser != null

    val uId: String?
        get() = auth.currentUser?.uid

    fun signOut() {
        auth.signOut()
    }

}
