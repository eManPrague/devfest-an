package cz.eman.android.devfest.addon.game.core.ui

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.SupportFragmentInjector

/**
 * This fragment should be used by all child fragments which wants to use DI
 *
 * @author eMan s.r.o. (vaclav.souhrada@eman.cz)
 * @see[Fragment]
 * @see[SupportFragmentInjector]
 */
abstract class BaseFragment : Fragment(), SupportFragmentInjector {

    override val injector: KodeinInjector = KodeinInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeInjector()
    }

    override fun onDestroy() {
        initializeInjector()
        super.onDestroy()
    }

}