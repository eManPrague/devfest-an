package lv.rigadevday.android.repository.model.schedule

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Rating(
    val comment: String = "",
    val qualityOfContent: Int = 0,
    val speakerPerformance: Int = 0
)
