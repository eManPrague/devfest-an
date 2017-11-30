package lv.rigadevday.android.ui.partners.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_partners_logo.view.*
import kotlinx.android.synthetic.main.item_partners_title.view.*
import cz.eman.android.devfest.R
import lv.rigadevday.android.repository.model.partners.Logo
import lv.rigadevday.android.ui.partners.adapter.PartnersItem.PartnerLogo
import lv.rigadevday.android.ui.partners.adapter.PartnersItem.PartnerTitle
import lv.rigadevday.android.ui.partners.adapter.PartnersItem.PartnerGame
import lv.rigadevday.android.utils.hide
import lv.rigadevday.android.utils.loadLogo

class PartnersAdapter(val openOnClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<PartnersItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = when (data[position]) {
        is PartnerLogo -> R.layout.item_partners_logo
        is PartnerTitle -> R.layout.item_partners_title
        is PartnerGame -> R.layout.item_partners_eman_logo
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_partners_logo -> LogoHolder(view)
            R.layout.item_partners_eman_logo -> GamePartnerHolder(view);
            else -> TitleHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (item) {
            is PartnerLogo -> (holder as LogoHolder).bind(item.partner, openOnClick)
            is PartnerTitle -> (holder as TitleHolder).bind(item.title)
            is PartnerGame -> (holder as GamePartnerHolder).bind(item.partner, openOnClick)
        }
    }


    private class LogoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(logo: Logo, openOnClick: (String) -> Unit) = with(itemView) {
            partners_logo_item_image.loadLogo(logo.logoUrl) { partners_logo_item_name.hide() }
            partners_logo_item_name.text = logo.name
            setOnClickListener { openOnClick(logo.url) }
        }
    }

    private class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) = with(itemView) {
            partners_logo_item_title.text = title
        }
    }

    private class GamePartnerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(logo: Logo, openOnClick: (String) -> Unit) = with(itemView) {
            setOnClickListener { openOnClick(logo.url) }
        }

    }

}
