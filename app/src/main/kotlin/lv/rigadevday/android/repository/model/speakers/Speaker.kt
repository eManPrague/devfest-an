package lv.rigadevday.android.repository.model.speakers

import com.google.firebase.database.IgnoreExtraProperties
import lv.rigadevday.android.repository.model.other.SocialAccount

@IgnoreExtraProperties
data class Speaker(
    var id: Int = -1,
    var featured: Boolean = false,

    var name: String = "",
    var shortBio: String = "",
    var bio: String = "",
    var photoUrl: String = "",

    var title: String = "",
    var company: String = "",
    var country: String = "",

    var badges: List<Badge> = emptyList(),
    var socials: List<SocialAccount> = emptyList(),
    var tags: List<String> = emptyList()
)
