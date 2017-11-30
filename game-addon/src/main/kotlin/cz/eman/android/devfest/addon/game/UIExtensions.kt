package cz.eman.android.devfest.addon.game

import android.graphics.Color
import android.widget.Button
import org.jetbrains.anko.enabled
import org.jetbrains.anko.textColor

const val WHITE = "#ffffffff"
private val TRANSLUCENT_WHITE = "#77ffffff"

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 */
fun Button.textWhite(): Button {
    this.textColor = Color.parseColor(WHITE)

    return this
}

fun Button.textWhiteTranscluent(): Button {
    this.textColor = Color.parseColor(TRANSLUCENT_WHITE)

    return this
}

fun Button.disable(): Button {
    this.enabled = false

    return this
}

fun Button.enable(): Button {
    this.enabled = true

    return this
}