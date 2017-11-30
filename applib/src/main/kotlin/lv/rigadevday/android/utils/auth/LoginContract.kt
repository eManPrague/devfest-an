package lv.rigadevday.android.utils.auth

import android.content.Intent
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LoginContract {

    fun loginSuccess()

    fun loginFailed()

    fun logoutSuccess()

    fun firebaseAuthWithGoogle(acc: GoogleSignInAccount)

    fun firebaseAuth()
}
