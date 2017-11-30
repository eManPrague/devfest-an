package cz.eman.android.devfest.addon.game.core.firebase

import com.google.firebase.auth.FirebaseUser

/**
 * Check if authorized user contains email. In case of that there is no email -> user credentials
 * are invalid
 */
fun FirebaseUser.isValid(): Boolean {
    if (this.email.isNullOrEmpty()) {
        return false
    }

    return true
}

fun FirebaseUser.nameFromEmail() = this.email?.substringBefore("@") ?: ""

fun FirebaseUser.getCorrectDisplayName(): String = if (this.displayName.isNullOrBlank()) this.nameFromEmail() else this.displayName!!