package lv.rigadevday.android.ui.schedule

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import cz.eman.android.devfest.R
import kotlinx.android.synthetic.main.fragment_my_schedule.view.*
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.ui.base.ViewPagerAdapter
import lv.rigadevday.android.ui.openSessionsActivity
import lv.rigadevday.android.ui.schedule.day.DayScheduleFragment
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.DATE_FORMAT
import lv.rigadevday.android.utils.showMessage
import java.util.*

class MyScheduleFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_my_schedule
    override val ignoreUiUpdates = true

    private lateinit var pageAdapter: ViewPagerAdapter

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun viewReady(view: View) {
        setupActionBar(R.string.schedule_title)

        val currentDate = DATE_FORMAT.format(Date())
        pageAdapter = ViewPagerAdapter(childFragmentManager)

        dataFetchSubscription = repo.schedule().toList().subscribe(
            { days ->
                days.forEach {
                    pageAdapter.addFragment(DayScheduleFragment.newInstance(it.date, it.dateReadable), it.dateReadable)
                }

                with(view.schedule_pager) {
                    offscreenPageLimit = 2
                    adapter = pageAdapter
                    currentItem = days.map { it.date }.indexOf(currentDate)
                    view.schedule_tabs.setupWithViewPager(this)
                }
            },
            { view.showMessage(R.string.error_message) }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_my_schedule, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_all_sessions -> {
                context.openSessionsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
