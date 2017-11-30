package lv.rigadevday.android.repository.model.other

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SocialAccount(
    var name: String = "",
    var icon: String = "",
    var link: String = ""
)
