package cz.eman.android.devfest.addon.game.function.profile.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.core.ui.BaseFragment
import cz.eman.android.devfest.addon.game.function.gameplay.ui.KeySortingActivity
import kotlinx.android.synthetic.main.fragment_keys_completed.*

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * This Fragment is supposed to show when player collected all keys and needs to sort them
 */
class KeysCompletedFragment : BaseFragment() {

    /**
     * Inflates View for this KeysCompletedFragment
     *
     * @param inflater Inflater inflating View for this KeysCompletedFragment
     * @param container ViewGroup which holds inflated View
     * @param savedInstanceState Bundle which holds saved data
     * @return This Fragment's inflated View
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_keys_completed, container, false)
    }

    /**
     * Sets this Fragment's Views
     * Sets OnClickListener for button
     *
     * @param view View returned by onCreateView()
     * @param savedInstanceState Bundle which holds saved data
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keysCompletedButton.setOnClickListener { startActivity(Intent(activity, KeySortingActivity::class.java)) }

    }
}