package lv.rigadevday.android.utils

import android.util.Log

val TAG = "RDD_TAG"

fun String?.logD() = Log.d(TAG, this)
fun Any?.logD() = Log.d(TAG, this.toString())

fun String?.logE() = Log.e(TAG, this)
fun Any?.logE() = Log.e(TAG, this.toString())

fun Throwable.printTrace(msg: String = "") = Log.e(TAG, msg,this)
