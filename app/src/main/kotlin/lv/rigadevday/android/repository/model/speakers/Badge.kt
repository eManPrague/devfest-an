package lv.rigadevday.android.repository.model.speakers

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Badge(
    var name: String = "",
    var description: String = "",
    var link: String = ""
)
