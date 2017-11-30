package lv.rigadevday.android.repository.model.team

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Team(
    val title: String = "",
    val members: List<Member> = emptyList()
)

