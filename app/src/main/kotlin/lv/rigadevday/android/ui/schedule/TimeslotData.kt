package lv.rigadevday.android.ui.schedule

import android.os.Bundle
import lv.rigadevday.android.utils.toExtraKey
import java.util.*

data class TimeslotData(
    val readableDate: String,
    val dateCode: String,
    val time: String = "",
    val ids: List<Int> = emptyList()
)


val EXTRA_SESSION_TIMESLOT = "session_timeslot".toExtraKey()
val EXTRA_SESSION_IDS = "session_ids".toExtraKey()

fun TimeslotData.toBundle(): Bundle {
    return Bundle().apply {
        putStringArrayList(EXTRA_SESSION_TIMESLOT, ArrayList(listOf(dateCode, readableDate, time)))
        putIntegerArrayList(EXTRA_SESSION_IDS, ArrayList(ids))
    }
}

fun Bundle.toIntentData(): TimeslotData {
    val strings = getStringArrayList(EXTRA_SESSION_TIMESLOT)
    val ids = getIntegerArrayList(EXTRA_SESSION_IDS)

    return TimeslotData(
        dateCode = strings[0],
        readableDate = strings[1],
        time = strings[2],
        ids = ids
    )
}
