package cz.eman.android.devfest.addon.game

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import com.github.salomonbrys.kodein.singleton
import cz.eman.android.devfest.addon.game.function.repository.GameRepository
import cz.eman.android.devfest.lib.app.AddonApplication
import cz.eman.android.devfest.lib.app.funtions.user.IUserRepository
import cz.eman.android.devfest.lib.app.funtions.user.UserRepository

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 * @see AddonApplication
 */
class GameAddonApp(context: Context, val kodein: ConfigurableKodein) : AddonApplication(context) {

//    companion object {
//        lateinit var di: Kodein
//    }

    companion object {
        var inputTimeOut = 1L
    }

    override fun onCreate() {
        //Kodein.global.addImport(gameModule)
        kodein.addImport(gameModule)

//        di = Kodein {
//            extend(appKodein())
//            //bind<GameRepository>() with singleton { GameRepository() }
//            import(gameModule)
//        }

        super.onCreate()

    }

    private val gameModule = Kodein.Module {

        bind<GameRepository>() with singleton { GameRepository() }
        bind<IUserRepository>() with singleton { UserRepository() }
    }

}