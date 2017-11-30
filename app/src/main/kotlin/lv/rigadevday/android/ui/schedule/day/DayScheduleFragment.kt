package lv.rigadevday.android.ui.schedule.day

import android.os.Bundle
import android.view.View
import cz.eman.android.devfest.R
import kotlinx.android.synthetic.main.fragment_day_schedule.view.*
import lv.rigadevday.android.ui.EXTRA_SESSION_DATA
import lv.rigadevday.android.ui.base.BaseFragment
import lv.rigadevday.android.ui.openSessionDetailsActivity
import lv.rigadevday.android.ui.openSpeakerActivity
import lv.rigadevday.android.ui.schedule.TimeslotData
import lv.rigadevday.android.ui.schedule.day.adapter.ScheduleAdapter
import lv.rigadevday.android.ui.schedule.toBundle
import lv.rigadevday.android.ui.schedule.toIntentData
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.showMessage
import org.zakariya.stickyheaders.StickyHeaderLayoutManager

class DayScheduleFragment : BaseFragment(), DayScheduleContract {

    companion object {
        fun newInstance(dateCode: String, readableDate: String) = DayScheduleFragment().apply {
            arguments = Bundle().apply {
                putBundle(EXTRA_SESSION_DATA, TimeslotData(readableDate, dateCode).toBundle())
            }
        }
    }

    override val layoutId = R.layout.fragment_day_schedule

    private val listAdapter = ScheduleAdapter(this)

    private lateinit var timeslotData: TimeslotData

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady(view: View) {
        timeslotData = arguments.getBundle(EXTRA_SESSION_DATA).toIntentData()

        with(view.schedule_recycler) {
            layoutManager = StickyHeaderLayoutManager()
            adapter = listAdapter
        }

        dataFetchSubscription = repo.scheduleDayTimeslots(timeslotData.dateCode)
                .toList()
                .subscribe(
                        { listAdapter.data = it },
                        { view.showMessage(R.string.error_message) }
                )
    }

    override fun openSession(sessionId: Int) {
        context.openSessionDetailsActivity(sessionId)
    }

    override fun openSpeaker(speakerId: Int) {
        context.openSpeakerActivity(speakerId)
    }

    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }

}

interface DayScheduleContract {
    fun openSession(sessionId: Int)
    fun openSpeaker(speakerId: Int)
}