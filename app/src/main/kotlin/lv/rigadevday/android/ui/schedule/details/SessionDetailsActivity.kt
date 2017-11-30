package lv.rigadevday.android.ui.schedule.details

import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.method.LinkMovementMethod
import cz.eman.android.devfest.R
import kotlinx.android.synthetic.main.activity_session_details.*
import lv.rigadevday.android.ui.EXTRA_SESSION_ID
import lv.rigadevday.android.ui.base.BaseActivity
import lv.rigadevday.android.ui.openRateSessionActivity
import lv.rigadevday.android.ui.openSpeakerActivity
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.auth.AuthStorage
import lv.rigadevday.android.utils.bindSchedulers
import lv.rigadevday.android.utils.darker
import lv.rigadevday.android.utils.fromHtml
import lv.rigadevday.android.utils.hide
import lv.rigadevday.android.utils.showMessage
import java.util.*
import javax.inject.Inject

class SessionDetailsActivity : BaseActivity() {

    companion object {
        val CONF_START = Date(1494828000000L)
        val CONF_END = Date(1495051200000L)
    }

    @Inject lateinit var authStorage: AuthStorage

    override val layoutId = R.layout.activity_session_details

    private var sessionId: Int = 0

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun refreshLoginState() {
        updateLoginRateButton()
        updateBookmarkIcon()
    }

    override fun viewReady() {
        sessionId = intent.extras.getInt(EXTRA_SESSION_ID)

        setupActionBar("")
        homeAsUp()

        updateLoginRateButton()

        dataFetchSubscription = repo.session(sessionId)
                .subscribe(
                        { session ->
                            setToolbarColor(session.color)
                            session_details_title.text = session.title

                            val speaker = session.speakerObjects.first()
                            session_details_speaker.text = speaker.name
                            session_details_speaker.setOnClickListener { it.context.openSpeakerActivity(speaker.id) }

                            session_details_tags.text = session.complexityAndTags
                            session_details_description.movementMethod = LinkMovementMethod.getInstance()
                            session_details_description.text = session.description.fromHtml()

                            updateBookmarkIcon()
                        },
                        {
                            if (it is NoSuchElementException) {
                                session_details_description.showMessage(R.string.session_cancelled)
                            } else {
                                session_details_description.showMessage(R.string.error_message)
                            }
                            finish()
                        }
                )
    }

    private fun setToolbarColor(color: Int) {
        toolbar.setBackgroundColor(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color.darker()
        }
    }

    private fun updateLoginRateButton() {
        if (!authStorage.hasLogin) {
            session_login_rate.setText(R.string.session_login_to_rate)
            session_login_rate.setOnClickListener {
                loginWrapper.logIn(this)
            }
        } else {
            session_login_rate.setText(R.string.session_rate)
            session_login_rate.setOnClickListener {

             //   val now = Date()
             //   if (now.after(CONF_START) && now.before(CONF_END)) {
                    openRateSessionActivity(sessionId)
             //   } else {
             //       showMessage(R.string.session_rate_disabled)
             //   }
            }
        }
    }

    private fun updateBookmarkIcon() {
        if (!authStorage.hasLogin) {
            session_details_bookmark.apply {
                setImageResource(R.drawable.vector_login)
                setOnClickListener {
                    loginWrapper.logIn(this@SessionDetailsActivity)
                    updateBookmarkIcon()
                    updateLoginRateButton()
                }
            }
        } else {
            repo.isSessionBookmarked(sessionId)
                    .bindSchedulers()
                    .subscribe(
                            { isBookmarked ->
                                session_details_bookmark.apply {
                                    if (isBookmarked) {
                                        setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_white_24dp))
                                        setOnClickListener {
                                            repo.removeBookmark(sessionId)
                                            updateBookmarkIcon()
                                        }
                                    } else {
                                        setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_star_border_white_24dp))
                                        setOnClickListener {
                                            repo.bookmarkSession(sessionId)
                                            updateBookmarkIcon()
                                        }
                                    }

                                }
                            },
                            { session_details_bookmark.hide() }
                    )
        }
    }

}
