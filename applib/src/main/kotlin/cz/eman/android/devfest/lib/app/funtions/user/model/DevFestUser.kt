package cz.eman.android.devfest.lib.app.funtions.user.model

import com.google.firebase.database.ServerValue
import java.sql.Timestamp
import java.util.*

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 */
data class DevFestUser(var cdhScore: Int = 0,
                       var correctOrderServerTimestamp: Long = 0,
                       val email: String,
                       val name: String,
                       var photoUrl: String = "none")

data class CdhProgress(val privateKeyPart: String,
                       var serverTimestamp: Map<String, String> = ServerValue.TIMESTAMP)