package cz.eman.android.devfest.addon.game.function.gameplay.service

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 *
 */
@Deprecated("Unused")
interface IAddKeyBlockingServiceListener {

    fun onCountDownStart()

    fun onTick(timeInSeconds: Long)

    fun onCountDownFinished()

}