package cz.eman.android.devfest.addon.game

import android.content.Context
import android.net.ConnectivityManager

 fun Context.checkNetworkConnection(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}