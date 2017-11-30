package lv.rigadevday.android.repository.model.schedule

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Schedule(
    val date: String = "",
    val dateReadable: String = "",
    val timeslots: List<Timeslot> = emptyList(),
    val tracks: List<Track> = emptyList()
)
