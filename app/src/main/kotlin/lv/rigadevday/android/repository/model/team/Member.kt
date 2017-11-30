package lv.rigadevday.android.repository.model.team

import com.google.firebase.database.IgnoreExtraProperties
import lv.rigadevday.android.repository.model.other.SocialAccount

@IgnoreExtraProperties
data class Member(
    val name: String = "",
    val photoUrl: String = "",
    val socials: List<SocialAccount> = emptyList()
)
