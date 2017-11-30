package lv.rigadevday.android.utils.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(context: Context) {

    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun linkOpened(link: String) = analytics.logEvent(
        Events.LINK_CLICKED,
        withParams { putString(Params.OPENED_LINK, link) }
    )

    fun logSessionBookmarked(time: String, date: String, sessionId: Int) = analytics.logEvent(
        Events.SESSION_BOOKMARKED,
        withParams {
            putInt(Params.SESSION_ID, sessionId)
            putString(Params.SESSION_TIME, time)
            putString(Params.SESSION_DATE, date)
        }
    )


    fun logSessionBookmarkRemoved(time: String, date: String, sessionId: Int) = analytics.logEvent(
        Events.SESSION_BOOKMARK_REMOVED,
        withParams {
            putInt(Params.SESSION_ID, sessionId)
            putString(Params.SESSION_TIME, time)
            putString(Params.SESSION_DATE, date)
        }
    )

    /**
     * I'm curious if people ever open this page at all.
     */
    fun aboutOpened() = analytics.logEvent(Events.ABOUT_SCREEN_OPENED, withParams { })

    private inline fun withParams(block: Bundle.() -> Unit) = Bundle().apply(block)

}

object Events {
    val LINK_CLICKED = "opened_link"
    val SESSION_BOOKMARKED = "session_bookmarked"
    val SESSION_BOOKMARK_REMOVED = "session_bookmark_removed"

    val ABOUT_SCREEN_OPENED = "about_screen_opened"
}

object Params {
    val OPENED_LINK = "opened_link"
    val SESSION_TIME = "session_start_time"
    val SESSION_DATE = "session_date"
    val SESSION_ID = "session_id"
}

