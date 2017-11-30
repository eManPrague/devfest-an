package lv.rigadevday.android.utils

import android.graphics.Color

const val DARK_FACTOR = 0.7f

fun Int.darker() = Color.rgb(
    Math.round(Color.red(this) * DARK_FACTOR),
    Math.round(Color.green(this) * DARK_FACTOR),
    Math.round(Color.blue(this) * DARK_FACTOR)
)
