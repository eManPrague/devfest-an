package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.core.ui.BaseFragment
import kotlinx.android.synthetic.main.card_unlocked_key.*

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * UnlockedKeyFragment, showing info about unlocked key in UnlockedKeyActivity
 */
class UnlockedKeyFragment : BaseFragment() {

    /**
     * Overriden function inflating layout of this Fragment
     *
     * @param inflater Inflater inflating this Fragment
     * @param container Container which contains View of this fragment
     * @param savedInstanceState Bundle containing saved data
     * @return Inflated View
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.card_unlocked_key, container, false)
    }

    /**
     * Overriden function setting Views in this Fragment
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = activity.intent.extras
        keyCode.text = bundle.getString("keyPart")
        unlockedString.text = bundle.getString("controlWord")
    }
}