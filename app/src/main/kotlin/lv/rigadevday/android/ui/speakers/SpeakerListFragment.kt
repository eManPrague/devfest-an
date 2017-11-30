package lv.rigadevday.android.ui.speakers

import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_list.*
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.ui.openSpeakerActivity
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.showMessage
import cz.eman.android.devfest.R

class SpeakerListFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_list

    private val listAdapter: SpeakersAdapter = SpeakersAdapter {
        context.openSpeakerActivity(it)
    }

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady(view: View) {
        setupActionBar(R.string.tab_speakers)

        with(list_fragment_recycler) {
            layoutManager = GridLayoutManager(view.context, 2)
            adapter = listAdapter
        }

        repo.speakers().toList().subscribe(
            { list -> listAdapter.data = list },
            { view.showMessage(R.string.error_message) }
        )
    }

}
