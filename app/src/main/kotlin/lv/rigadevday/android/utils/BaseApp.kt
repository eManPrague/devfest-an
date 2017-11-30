package lv.rigadevday.android.utils

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import cz.eman.android.devfest.app.DevFestApp
import lv.rigadevday.android.utils.di.AppGraph
import lv.rigadevday.android.utils.di.AppModule
import lv.rigadevday.android.utils.di.DaggerAppGraph


open class BaseApp : DevFestApp() {

    companion object {
        lateinit var graph: AppGraph
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        graph = DaggerAppGraph.builder()
            .appModule(AppModule(this))
            .build()
        graph.inject(this)
    }
}
