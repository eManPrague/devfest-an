package lv.rigadevday.android.ui.schedule.day.adapter.holders

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.item_schedule_header.view.*
import lv.rigadevday.android.repository.model.schedule.Timeslot
import org.zakariya.stickyheaders.SectioningAdapter

class ScheduleHeaderViewHolder(itemView: View) : SectioningAdapter.HeaderViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun bind(item: Timeslot) = with(itemView) {
        schedule_header_time.text = item.formattedStartTime
    }
}
