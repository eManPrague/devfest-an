package lv.rigadevday.android.utils.auth

import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import cz.eman.android.devfest.R
import lv.rigadevday.android.ui.base.BaseActivity
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthWrapper @Inject constructor(
        private val authStorage: AuthStorage
) : GoogleApiClient.OnConnectionFailedListener {

    companion object {
        const val RC_SIGN_IN: Int = 213
    }

    private lateinit var googleClient: GoogleApiClient
    var contract: LoginContract? = null

    fun bind(activity: BaseActivity, loginContract: LoginContract) {
        contract = loginContract

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleClient = GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        contract?.loginFailed()
    }

    fun logIn(activity: BaseActivity) {
        // Start sign in/sign up activity
        val intent = with(AuthUI.getInstance().createSignInIntentBuilder()) {
            setAvailableProviders(
                    Arrays.asList(
                            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                    ))
            build()
        }

        activity.startActivityForResult(intent, RC_SIGN_IN)
    }

    fun handleLoginResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN && data != null) {
            val response = IdpResponse.fromResultIntent(data)
            if (response != null && resultCode == Activity.RESULT_OK) {
                contract?.firebaseAuth()
            } else {
                contract?.loginFailed()
            }
        }
    }

    fun logOut(baseActivity: BaseActivity) {
        AuthUI.getInstance().signOut(baseActivity)
                .addOnCompleteListener {
                    logoutFromClient()
                }
    }

    private fun logoutFromClient() {
        contract?.logoutSuccess()
        // }
    }
}
