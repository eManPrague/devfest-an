package lv.rigadevday.android.repository.model.partners

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Logo(
    val name: String = "",
    val url: String = "",
    val logoUrl: String = ""
)
