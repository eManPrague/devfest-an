package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.AppCompatActivityInjector
import cz.eman.android.devfest.addon.game.BaseGameActivity
import cz.eman.android.devfest.addon.game.GameActivity
import cz.eman.android.devfest.addon.game.R
import kotlinx.android.synthetic.main.activity_key_unlocked.*

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * UnlockedKeyActivity is started after AddKeyActivity, it is used as notification about unlocked key
 */
class UnlockedKeyActivity : BaseGameActivity() {

    override val contentLayout = R.layout.activity_key_unlocked

    /**
     * Overriden function, sets View for this Activity and sets OnClickListener for Continue button
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(R.id.unlockedKeyPartContainer, UnlockedKeyFragment()).commit()

        continueButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@UnlockedKeyActivity, GameActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        })
    }

}