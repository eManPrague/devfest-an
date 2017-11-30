package lv.rigadevday.android.utils

import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import lv.rigadevday.android.ui.openWeb
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


fun String.toExtraKey() = "lv.rigadevday.android.extra.$this"

//fun String.toImageUrl() =
//    if (this.startsWith("http")) this
//    else "http://rigadevdays.lv${this.replace("..", "")}" // eMan comment - Not used for DevFest CZ - we have always full URL

@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned = this
    .replace("</br>", "<br/>")
    .let { Html.fromHtml(it) }
    .let(::SpannableString)
    .also { result ->
        Patterns.WEB_URL.toRegex()
            .findAll(result)
            .map { it.value.dropLastWhile { it == '.' } to it.range }
            .forEach { (url, range) ->
                result.setSpan(GoToURLSpan(url), range.start, range.endInclusive, 0)
            }
    }

private class GoToURLSpan(internal var url: String) : ClickableSpan() {
    override fun onClick(view: View) {
        view.context.openWeb(url)
    }
}

fun String.urlEncoded(): String {
    try {
        return URLEncoder.encode(this, "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        throw RuntimeException("URLEncoder.encode() failed for " + this)
    }
}

val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
