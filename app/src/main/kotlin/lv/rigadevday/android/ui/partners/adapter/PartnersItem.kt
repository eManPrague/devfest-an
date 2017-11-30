package lv.rigadevday.android.ui.partners.adapter

import lv.rigadevday.android.repository.model.partners.Logo

sealed class PartnersItem {

    class PartnerTitle(val title: String) : PartnersItem()

    class PartnerLogo(val partner: Logo) : PartnersItem()

    class PartnerGame(val partner: Logo) : PartnersItem()

}
