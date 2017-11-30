package lv.rigadevday.android.repository.model.schedule

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Timeslot(
    val startTime: String = "",
    val endTime: String = "",
    val sessions: List<List<Int>> = emptyList()
) {
    val sessionIds: List<Int> get() = sessions.flatMap { it }

    val formattedStartTime: String get() = if (startTime.length < 5) "0$startTime" else startTime

    var sessionObjects = listOf<Session>()

}
