package lv.rigadevday.android.repository

import android.graphics.Color

object ColorStorage {

    private fun String.asColor() = Color.parseColor(this)

    private val DEFAULT = "#006590".asColor()

    private val COLORS = mapOf(
        "general" to "#bdbdbd".asColor(),
        "blockchain" to "#9E9E9E".asColor(),
        "android" to "#78c257".asColor(),
        "internet-of-things" to "#00e065".asColor(),
        "mobile" to "#00d3bd".asColor(),
        "web" to "#2196f3".asColor(),
        "cloud" to "#3f51b5".asColor(),
        "community" to "#e91e63".asColor(),
        "it" to "#9E9E9E".asColor(),
        "best-practices" to "#9E9E9E".asColor(),
        "angular-js" to "#e0343d".asColor(),
        "protractor" to "#e0343d".asColor(),
        "java" to "#ff9800".asColor(),
        "performance" to "#0d47a1".asColor(),
        "serviceworkers" to "#311b92".asColor(),
        "akka" to "#7b1fa2".asColor(),
        "kubernetes" to "#326de6".asColor(),
        "spring" to "#4caf50".asColor(),
        "clojure" to "#1565c0".asColor(),
        "ci" to "#cddc39".asColor(),
        "bigdata" to "#607d8b".asColor(),
        "devops" to "#009688".asColor(),
        "sql" to "#673ab7".asColor(),
        "oracle" to "#e0343d".asColor(),
        "docker" to "#2196f3".asColor(),
        "react" to "#00b4aa".asColor(),
        "react-native" to "#00b4aa".asColor()
    )

    fun get(tag: String) = COLORS[tag.toLowerCase().replace(' ', '-')] ?: DEFAULT

}
