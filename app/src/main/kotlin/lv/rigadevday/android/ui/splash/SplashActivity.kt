package lv.rigadevday.android.ui.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import cz.eman.android.devfest.R
import cz.eman.android.devfest.addon.game.checkNetworkConnection
import cz.eman.android.devfest.lib.app.funtions.user.model.DevFestUser
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import lv.rigadevday.android.repository.Repository
import lv.rigadevday.android.repository.model.partners.Partners
import lv.rigadevday.android.repository.model.schedule.Schedule
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.repository.model.speakers.Speaker
import lv.rigadevday.android.ui.tabs.TabActivity
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.showMessage
import org.jetbrains.anko.alert
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {

    val TIME_TO_EXIT: Long = 2000

    companion object {
        const val SP_NAME_DEVFEST_APP = "devfestAppPref"
        const val SP_KEY_FIRST_RUN = "firstRun"
        const val NET_REQ_CODE = 201
    }

    @Inject lateinit var repo: Repository
    private var isHotfix: Boolean = true
    
    private val disposable = CompositeDisposable()

    private val sharedPref: SharedPreferences by lazy { getSharedPreferences(SP_NAME_DEVFEST_APP, Context.MODE_PRIVATE) }


    private var isFirstRun: Boolean = true
        set(value) {
            sharedPref.edit().putBoolean(SP_KEY_FIRST_RUN, value).commit()
            field = value
        }
        get() = sharedPref.getBoolean(SP_KEY_FIRST_RUN, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApp.graph.inject(this)

        updateDataCache(isFirstRun)
    }

    override fun onStop() {
        super.onStop()
        // clear all the subscription
        disposable.clear()
    }

    private fun exitApp() = Handler().postDelayed(
            { finish() },
            TIME_TO_EXIT
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (NET_REQ_CODE == requestCode) {
            when (resultCode) {
                Activity.RESULT_OK, Activity.RESULT_CANCELED -> updateDataCache(true)
                else -> finish()
            }
        }
    }

    private fun updateDataCache(checkNetwork: Boolean) {
        if (checkNetwork) {
            if (checkNetworkConnection()) {
                updateCache()
            } else {
                //showMessage(R.string.error_connection_message)
                //exitApp()
                alert(message = R.string.error_connection_message) {
                    yesButton { startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), NET_REQ_CODE) }
                    noButton { finish() }
                    cancellable(false)
                }.show()
            }
        } else {
            updateCache()
        }
    }

    private fun updateCache() {
        // Cache and enrich data into memory

        val database = repo.database
        val dataCache = repo.dataCache

        RxFirebaseDatabase.observeValueEvent(database.child("speakers"), DataSnapshotMapper.listOf(Speaker::class.java))
                .map { dataCache.updateSpeakers(it) }
                .subscribe({
                    RxFirebaseDatabase.observeValueEvent(database.child("resources"), DataSnapshotMapper.mapOf(String::class.java))
                            .map { dataCache.updateResources(it) }.subscribe({
                        RxFirebaseDatabase.observeValueEvent(database.child("sessions"), DataSnapshotMapper.mapOf(Session::class.java))
                                .map { dataCache.updateSessions(it) }.subscribe({
                            RxFirebaseDatabase.observeValueEvent(database.child("partners"), DataSnapshotMapper.listOf(Partners::class.java))
                                    .map { dataCache.updatePartners(it) }.subscribe({
                                RxFirebaseDatabase.observeValueEvent(database.child("schedule"), DataSnapshotMapper.listOf(Schedule::class.java))
                                        .map { dataCache.updateSchedules(it) }.subscribe({
                                    if (isFirstRun) {
                                        isFirstRun = false
                                    }
                                    if (isHotfix) {
                                        isHotfix = false
                                        Intent(this, TabActivity::class.java)
                                                .let { startActivity(it) }
                                                .also { finish() }
                                    }
                                },
                                        {
                                            // showMessage(R.string.error_connection_message)
                                            exitApp()
                                        }

                                )
                            }, {})
                        }, {})
                    }, {})
                }, {})

//        repo.updateCache()
//                .toCompletable()
//                .subscribe(
//                        {
//                            if (isFirstRun) {
//                                isFirstRun = false
//                            }
//
//                            Intent(this, TabActivity::class.java)
//                                    .let { startActivity(it) }
//                                    .also { finish() }
//                        },
//                        {
//                            showMessage(R.string.error_connection_message)
//                            exitApp()
//                        }
//                )
    }
}
