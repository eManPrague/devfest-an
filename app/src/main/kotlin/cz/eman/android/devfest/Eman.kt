package cz.eman.android.devfest

import lv.rigadevday.android.repository.model.partners.Logo
import lv.rigadevday.android.ui.partners.adapter.PartnersItem

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 */

const val URL_EMAN_WEB_EN = "https://www.emanprague.com/en/"

const val MAP_NW_LATITUDE = 50.131874746778095
const val MAP_NW_LONGITUDE = 14.37673319131136
const val MAP_SE_LATITUDE = 50.12830372401131
const val MAP_SE_LONGITUDE = 14.372546598315239

fun createEmanPartnerGame() = PartnersItem.PartnerGame(Logo(url = URL_EMAN_WEB_EN))