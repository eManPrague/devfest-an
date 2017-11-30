package cz.eman.android.devfest.addon.game.function.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.core.ui.BaseFragment

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * GameFinishFragment is a Fragment to be shown when player has finished the game but hasn't
 * reached TOP 10 status
 */
class GameFinishFragment : BaseFragment() {

    /**
     * Overriden function inflating this Fragment's View
     *
     * @param inflater Inflater inflating this Fragment's View
     * @param container Container which holds this Fragment's View
     * @param savedInstanceState Bundle containing saved data
     * @return This Fragment's inflated View
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_game_finish, container, false)
    }
}