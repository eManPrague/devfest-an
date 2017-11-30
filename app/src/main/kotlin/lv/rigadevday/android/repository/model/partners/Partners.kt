package lv.rigadevday.android.repository.model.partners

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Partners(
    val title: String = "",
    val logos: List<Logo> = emptyList()
) {
    var actualTitle: String = ""
}

