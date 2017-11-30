package cz.eman.android.devfest.addon.game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.AppCompatActivityInjector
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseAuth
import cz.eman.android.devfest.addon.game.core.firebase.getCorrectDisplayName
import cz.eman.android.devfest.addon.game.core.firebase.isValid
import cz.eman.android.devfest.lib.app.funtions.user.IUserRepository
import kotlinx.android.synthetic.main.base_game_activity.*
import net.hockeyapp.android.CrashManager
import net.hockeyapp.android.CrashManagerListener
import org.jetbrains.anko.toast
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*


/**
 * This is a base activity (parent activity) for all activities which are handling something with the
 * game addon data.
 *
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 * @see AppCompatActivity
 */
abstract class BaseGameActivity : AppCompatActivity(), AppCompatActivityInjector {

    override val injector: KodeinInjector = KodeinInjector()

    abstract val contentLayout: Int

    private val userRepository: IUserRepository by instance<IUserRepository>()

    var screenTitleRes: Int = -1
        set(value) {
            headerText.text = getString(value)
        }

    var screenTitle: String = ""
        set(value) {
            headerText.text = value
        }

    companion object {
        const val RC_SIGN_IN: Int = 213
//        var addingCounter: Int = 1
    }

    protected open fun onLoginSuccess() {
        // do nothing here
    }

    override fun attachBaseContext(newBase: Context) {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/courier.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        CrashManager.register(this, "524fb50e7943402bb6d6679716f0ea4a", AppHaCrashManagerListener())
        initializeInjector()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_game_activity)

        container.addView(layoutInflater.inflate(contentLayout, null))
        navUp.setOnClickListener({ onBackPressed() })
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    override fun onDestroy() {
        destroyInjector()
        super.onDestroy()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) { // Check request code if is it SIGN IN process
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) { // check if user is signed
                    if (!user.isValid()) {
                        toast(getString(R.string.game_login_invalid))
                        finish()
                    } else {
                        val email = user.email ?: ""
                        val username = user.getCorrectDisplayName()
                        userRepository.saveUserIfNeeded(id = user.uid, name = username, email = email, photoUrl = user.photoUrl)
                        // Give a chance to a child activity handle state after logic success check
                        onLoginSuccess()
                    }
                } // TODO handle error state
            } else {
                finish()
            }
        }
    }

    private fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            onLoginSuccess()
        } else if (!checkNetworkConnection()) {
            toast(R.string.general_message_no_network)
            finish()
        } else {
            val intent = with(AuthUI.getInstance().createSignInIntentBuilder()) {
                setAvailableProviders(
                        Arrays.asList(
                                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                build()
            }
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    /**
     * Application CrashManagerListener implementation<br></br>
     * Direct usage of anonymous class instance of CrashManagerListener cause Activity leaks
     */
    private class AppHaCrashManagerListener : CrashManagerListener() {
        override fun shouldAutoUploadCrashes(): Boolean {
            return true
        }
    }
}
