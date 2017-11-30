package lv.rigadevday.android.ui.schedule.day.adapter.holders

import android.view.View
import kotlinx.android.synthetic.main.item_schedule_non_session.view.*
import lv.rigadevday.android.repository.model.schedule.Session
import org.zakariya.stickyheaders.SectioningAdapter

class ScheduleNonSessionViewHolder(itemView: View) : SectioningAdapter.ItemViewHolder(itemView) {

    fun bind(item: Session) = with(itemView) {
        schedule_item_non_session_title.text = item.title
        schedule_item_non_session_subtitle.text = item.location
    }

}
