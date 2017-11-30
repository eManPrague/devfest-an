package lv.rigadevday.android.ui.partners

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cz.eman.android.devfest.R
import cz.eman.android.devfest.createEmanPartnerGame
import kotlinx.android.synthetic.main.fragment_list.*
import lv.rigadevday.android.repository.model.partners.Logo
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.ui.openWeb
import lv.rigadevday.android.ui.partners.adapter.PartnersAdapter
import lv.rigadevday.android.ui.partners.adapter.PartnersItem
import lv.rigadevday.android.ui.partners.adapter.PartnersItem.PartnerLogo
import lv.rigadevday.android.ui.partners.adapter.PartnersItem.PartnerTitle
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.asFlowable
import lv.rigadevday.android.utils.showMessage

class PartnersFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_list

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    private val listAdapter = PartnersAdapter {
        context.openWeb(it)
    }

    override fun viewReady(view: View) {
        setupActionBar(R.string.partners_title)

        with(list_fragment_recycler) {
            layoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = spanLookup
            }
            adapter = listAdapter
        }

        dataFetchSubscription = repo.partners()
            .flatMap {
                PartnerTitle(it.actualTitle)
                    .asFlowable<PartnersItem>()
                    .concatWith(it.logos.map(::PartnerLogo).asFlowable())
            }
            .toList()
            .subscribe(
                {
                    it.add(0, createEmanPartnerGame())
                    listAdapter.data = it
                },
                { view.showMessage(R.string.error_message) }
            )
    }

    val spanLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int) = when (listAdapter.getItemViewType(position)) {
            R.layout.item_partners_logo -> 1
            else -> 3
        }
    }

}

