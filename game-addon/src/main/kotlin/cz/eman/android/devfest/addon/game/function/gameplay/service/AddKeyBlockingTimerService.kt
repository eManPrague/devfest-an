package cz.eman.android.devfest.addon.game.function.gameplay.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import cz.eman.android.devfest.addon.game.GameAddonApp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.SECONDS


/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 */
@Deprecated("Unused")
class AddKeyBlockingTimerService : Service() {

    private var listeners = hashSetOf<IAddKeyBlockingServiceListener>()

    private var timerState: TimerState = TimerState.STOPPED

    private var binder: AddKeyBlockingServiceBinder? = null

    private var countDownTimer: CountDownTimer? = null
    private var timeToGo = 0L

    var isRunning = false

    private enum class TimerState {
        STOPPED,
        RUNNING
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTimer()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        binder = AddKeyBlockingServiceBinder()
        isRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()

        binder = null
        countDownTimer?.cancel()
        observer?.dispose()

        isRunning = false
    }

    fun startCountdown() {
        initTimer()
    }

    private var observer: Disposable? = null

    private fun startCountdownTimer() {
        timerState = TimerState.RUNNING
        timeToGo += 30
        observer = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map { timeToGo-- }
                .takeUntil{timeToGo == 0L}
                .observeOn(AndroidSchedulers.mainThread())
                .forEach {
                    if (!listeners.isEmpty()) {
                        listeners.forEach {
                            it.onTick(timeToGo)
                            if (timeToGo == 0L) {
                                timerState = TimerState.STOPPED
                                it.onTick(timeToGo)
                                it.onCountDownFinished()
                                stopSelf()
                            }
                        }
                    }
                }
    }

    private fun initTimer() {
        GameAddonApp.inputTimeOut *= 2
        timeToGo = GameAddonApp.inputTimeOut + 1
        if (timeToGo <= 0) {
            timerState = TimerState.STOPPED
        } else {
            if (!listeners.isEmpty()) {
                listeners.forEach { it.onCountDownStart() }
            }
            startCountdownTimer()
            timerState = TimerState.RUNNING
        }
    }

    inner class AddKeyBlockingServiceBinder : Binder() {

        fun getService() = this@AddKeyBlockingTimerService

        fun addListener(listener: IAddKeyBlockingServiceListener) {
            listeners.add(listener)
            if (TimerState.RUNNING == timerState) {
                listener.onCountDownStart()
            }
        }

        fun removeListener(listener: IAddKeyBlockingServiceListener) {
            listeners.remove(listener)
        }

    }

}