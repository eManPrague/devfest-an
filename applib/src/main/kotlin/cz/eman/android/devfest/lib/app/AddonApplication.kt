package cz.eman.android.devfest.lib.app

import android.content.Context
import android.content.ContextWrapper
import android.support.annotation.CallSuper
import com.github.salomonbrys.kodein.KodeinAware

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 * @see[ContextWrapper]
 */
abstract class AddonApplication(val context: Context) : ContextWrapper(context.applicationContext) {

    companion object {
        lateinit var instance: AddonApplication
    }

    @CallSuper
    open fun onCreate() {
        instance = this
    }

}