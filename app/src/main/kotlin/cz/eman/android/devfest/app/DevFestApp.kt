package cz.eman.android.devfest.app

import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.android.androidModule
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import cz.eman.android.devfest.addon.game.GameAddonApp
import cz.eman.android.devfest.lib.app.AddonApplication


/**
 * The main application class for initialization of the DI, addons and others global modules and objects
 *
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 * @see[MultiDexApplication]
 * @see[KodeinAware]
 */
open class DevFestApp : MultiDexApplication(), KodeinAware {

    override val kodein = ConfigurableKodein()

    private val addons = mutableListOf<AddonApplication>()

    override fun onCreate() {
        super.onCreate()

        initStetho()

        kodein.addImport(androidModule)

        registerAddon(GameAddonApp(this, kodein))
    }

    private fun registerAddon(addonApplication: AddonApplication) {
        addonApplication.onCreate()
        addons.add(addonApplication)
    }

    private fun initStetho() {
        // Create an InitializerBuilder
        val initializerBuilder = Stetho.newInitializerBuilder(this)
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        )
        // Enable command line interface
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this)
        );
        // Use the InitializerBuilder to generate an Initializer
        val initializer = initializerBuilder.build()
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer)
    }
}