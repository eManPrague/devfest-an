package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import cz.eman.android.devfest.addon.game.*
import cz.eman.android.devfest.lib.app.funtions.user.model.CdhProgress
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_key.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Activity containing views for adding private key parts and managing
 * key adding logic
 *
 * @author Pavel Habzansky (pavel.habzansky@eman.cz)
 * @see[BaseGameActivity]
 */
class AddKeyActivity : BaseGameActivity(), View.OnClickListener {

    companion object {
        const val SP_TIMEOUT = "currentTimeout"
        const val SP_START_TIME = "startTime"
        const val SP_FIRST_RUN_TIME = "firstRun"
        const val TIME = "timer"
    }

    private val sharedPref: SharedPreferences by lazy { getSharedPreferences(Companion.TIME, Context.MODE_PRIVATE) }

    override val contentLayout = R.layout.activity_add_key

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    var bool: Boolean = false

    val playerProgressRef = FirebaseDatabase.getInstance().getReference("cdhProgress/$uid")
    val keyPartsRef = FirebaseDatabase.getInstance().getReference("cdhPrivateKeyParts")

    private var timeout: Long = 1
        set(value) {
            sharedPref.edit().putLong(SP_TIMEOUT, value).commit()
            field = value
        }
        get() = sharedPref.getLong(SP_TIMEOUT, 1)

    private var startTimeInMillis: Long
        set(value) {
            sharedPref.edit().putLong(SP_START_TIME, value).commit()
        }
        get() = sharedPref.getLong(SP_START_TIME, System.currentTimeMillis())

    private var isFirstRun: Boolean = true
        set(value) {
            sharedPref.edit().putBoolean(SP_FIRST_RUN_TIME, value).commit()
            field = value
        }
        get() = sharedPref.getBoolean(SP_FIRST_RUN_TIME, true)


    /**
     * Loads keyboard fragment and sets listening to Firebase
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenTitleRes = R.string.key_part_add_text_header

        keyInputLeftPart.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (keyInputLeftPart.text.isEmpty()) return
                setColorChange()
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }
        })

        keyInputRightPart.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (keyInputRightPart.text.isEmpty()) return
                setColorChange()

                val input = "${keyInputLeftPart.text}${keyInputRightPart.text}"

                playerProgressRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(playerProgressSnapshot: DataSnapshot?) {
                        if (playerProgressSnapshot == null) return
                        for (snapshot in playerProgressSnapshot.children) {
                            if (snapshot.child("privateKeyPart")
                                    .value
                                    .toString()
                                    .equals(input)) {
                                infoText.text = getString(R.string.key_part_add_text_already_unlocked)

                                clear()
                                return
                            }
                        }
                        if (input.isEmpty()) return
                        keyPartsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(keyPartsSnapshot: DataSnapshot?) {
                                // Private key part is not obtained yet, add it to cdhProgress and start new activity
                                if (keyPartsSnapshot == null) return

                                if (keyPartsSnapshot.hasChild(input)) {
                                    resetTimeout()

                                    val controlWord = keyPartsSnapshot.child(input)
                                            .child("controlWord").value.toString()
                                    val cdhProgress = CdhProgress(privateKeyPart = input)
                                    val key = playerProgressRef.root.child("cdhProgress").child(uid)
                                            .push().key
                                    playerProgressRef.root.child("cdhProgress").child(uid)
                                            .child(key).setValue(cdhProgress)

                                    val intent = Intent(this@AddKeyActivity, UnlockedKeyActivity::class.java)
                                    intent.putExtra("keyPart", input)
                                    intent.putExtra("controlWord", controlWord)
                                    startActivity(intent)

                                    finish()
                                } else { // Private key input is not correct
                                    startCountDown()
                                    return
                                }
                            }
                            override fun onCancelled(p0: DatabaseError?) {

                            }
                        })

                    }
                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        keyAButton.setOnClickListener(this)
        keyBButton.setOnClickListener(this)
        keyCButton.setOnClickListener(this)
        keyDButton.setOnClickListener(this)
        keyEButton.setOnClickListener(this)
        keyFButton.setOnClickListener(this)
        keyGButton.setOnClickListener(this)
        keyHButton.setOnClickListener(this)
        keyIButton.setOnClickListener(this)
        keyJButton.setOnClickListener(this)
        key1Button.setOnClickListener(this)
        key2Button.setOnClickListener(this)
        key3Button.setOnClickListener(this)
        key4Button.setOnClickListener(this)
        key5Button.setOnClickListener(this)
        key6Button.setOnClickListener(this)
        key7Button.setOnClickListener(this)
        key8Button.setOnClickListener(this)
        key9Button.setOnClickListener(this)
        key0Button.setOnClickListener(this)
        clearButton.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        infoText?.text = ""
        reactMessageText?.text = ""
        checkTimer()
    }

    override fun onClick(view: View) {
        if (R.id.clearButton == view.id) {
            clear()
        } else {
            inputClick(view)
        }
        infoText?.text = ""
    }

    /**
     * Clears key input TextViews, this occurs when Clear button is pressed
     */
    fun clear() {
        resetColors()
        keyInputLeftPart.text = ""
        keyInputRightPart.text = ""
    }

    /**
     * Inputs keys in TextViews
     */
    private fun inputClick(view: View) {
        if (keyInputLeftPart.text.toString().isEmpty()) {
            keyInputLeftPart.text = (view as Button).text
        } else if (keyInputRightPart.text.toString().isEmpty()) {
            keyInputRightPart.text = (view as Button).text
        }
    }

    private fun resetColors() {
        keyInputLeftPart.backgroundColor = Color.GREEN
        keyInputRightPart.backgroundColor = Color.parseColor("#9900FF00")
    }


    /**
     * Function sets color change based on input
     */
    fun setColorChange() {
        if (keyInputLeftPart.text.isEmpty() && keyInputRightPart.text.isEmpty()) {
            keyInputLeftPart.backgroundColor = Color.GREEN
            keyInputRightPart.backgroundColor = Color.parseColor("#9900FF00")
        } else if ((!keyInputLeftPart.text.isEmpty()) && keyInputRightPart.text.isEmpty()) {
            keyInputLeftPart.backgroundColor = Color.parseColor("#9900FF00")
            keyInputRightPart.backgroundColor = Color.GREEN
        } else if (!keyInputRightPart.text.isEmpty() && !keyInputLeftPart.text.isEmpty()) {
            keyInputLeftPart.backgroundColor = Color.parseColor("#9900FF00")
            keyInputRightPart.backgroundColor = Color.parseColor("#9900FF00")
        }
    }

    /**
     * Switches visibility for inputView and timerText. One is always VISIBLE other is GONE
     */
    private fun visibilitySwitch(isGameBlocked: Boolean) {
        if (isGameBlocked) {
            inputView.visibility = View.GONE
            reactMessageText.visibility = View.VISIBLE
        } else {
            inputView.visibility = View.VISIBLE
            reactMessageText.visibility = View.GONE
        }
    }

    private fun checkTimer() {
        if (!isFirstRun) {
            val lastStartTime = startTimeInMillis
            if (lastStartTime != -1L) {
                val time = timeout - ((getNow() - lastStartTime) / 1000)
                if (time > 0) {
                    runCountDown(time)
                }
            }
        }
    }

    private fun startCountDown() {
        if (!isFirstRun) {
            timeout *= 2
        } else {
            timeout = 1
        }

        startTimeInMillis = getNow()
        runCountDown(timeout)

        if (isFirstRun) {
            isFirstRun = false
        }
    }

    private fun runCountDown(time: Long) {
        onCountDownStart()
        var timeToGo = time + 1
        Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map { timeToGo-- }
                .takeUntil { timeToGo == 0L }
                .observeOn(AndroidSchedulers.mainThread())
                .forEach {
                    onTick(timeToGo)

                    if (timeToGo == 0L) {
                        onTick(timeToGo)
                        onCountDownFinished()
                        startTimeInMillis = -1L
                    }
                }
    }

    private fun resetTimeout() {
        startTimeInMillis = -1L
        timeout = 1L
    }

    private fun getNow() = Calendar.getInstance().timeInMillis

    private fun onCountDownStart() {
        visibilitySwitch(true)
        disableKeyboard()
        clear()
    }

    private fun onTick(timeInSeconds: Long) {
        runOnUiThread {
            reactMessageText.text = resources.getString(R.string.key_part_add_timed_out, timeInSeconds)
        }
    }

    private fun onCountDownFinished() {
        runOnUiThread {
            visibilitySwitch(false)
            enableKeyboard()
        }
    }

    private fun enableKeyboard() {
        key0Button.enable().textWhite()
        key1Button.enable().textWhite()
        key2Button.enable().textWhite()
        key3Button.enable().textWhite()
        key4Button.enable().textWhite()
        key5Button.enable().textWhite()
        key6Button.enable().textWhite()
        key7Button.enable().textWhite()
        key8Button.enable().textWhite()
        key9Button.enable().textWhite()
        keyAButton.enable().textWhite()
        keyBButton.enable().textWhite()
        keyCButton.enable().textWhite()
        keyDButton.enable().textWhite()
        keyEButton.enable().textWhite()
        keyFButton.enable().textWhite()
        keyGButton.enable().textWhite()
        keyHButton.enable().textWhite()
        keyIButton.enable().textWhite()
        keyJButton.enable().textWhite()
        clearButton.enable().textWhite()
    }

    private fun disableKeyboard() {
        key0Button.disable().textWhiteTranscluent()
        key1Button.disable().textWhiteTranscluent()
        key2Button.disable().textWhiteTranscluent()
        key3Button.disable().textWhiteTranscluent()
        key4Button.disable().textWhiteTranscluent()
        key5Button.disable().textWhiteTranscluent()
        key6Button.disable().textWhiteTranscluent()
        key7Button.disable().textWhiteTranscluent()
        key8Button.disable().textWhiteTranscluent()
        key9Button.disable().textWhiteTranscluent()
        keyAButton.disable().textWhiteTranscluent()
        keyBButton.disable().textWhiteTranscluent()
        keyCButton.disable().textWhiteTranscluent()
        keyDButton.disable().textWhiteTranscluent()
        keyEButton.disable().textWhiteTranscluent()
        keyFButton.disable().textWhiteTranscluent()
        keyGButton.disable().textWhiteTranscluent()
        keyHButton.disable().textWhiteTranscluent()
        keyIButton.disable().textWhiteTranscluent()
        keyJButton.disable().textWhiteTranscluent()
        clearButton.disable().textWhiteTranscluent()
    }


}