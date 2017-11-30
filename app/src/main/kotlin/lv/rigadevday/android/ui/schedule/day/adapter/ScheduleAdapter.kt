package lv.rigadevday.android.ui.schedule.day.adapter

import android.view.ViewGroup
import cz.eman.android.devfest.R
import lv.rigadevday.android.repository.model.schedule.Timeslot
import lv.rigadevday.android.ui.schedule.day.DayScheduleContract
import lv.rigadevday.android.ui.schedule.day.adapter.holders.ScheduleHeaderViewHolder
import lv.rigadevday.android.ui.schedule.day.adapter.holders.ScheduleNonSessionViewHolder
import lv.rigadevday.android.ui.schedule.day.adapter.holders.ScheduleSessionViewHolder
import lv.rigadevday.android.utils.inflate
import org.zakariya.stickyheaders.SectioningAdapter

class ScheduleAdapter(private val contract: DayScheduleContract) : SectioningAdapter() {

    companion object {
        private const val TYPE_SESSION = 0
        private const val TYPE_NON_SESSION = 1
    }

    var data: List<Timeslot> = emptyList()
        set(value) {
            field = value
            notifyAllSectionsDataSetChanged()
        }

    override fun getNumberOfSections(): Int = data.size

    override fun getNumberOfItemsInSection(sectionIndex: Int): Int = data[sectionIndex].sessionObjects.size

    override fun doesSectionHaveHeader(sectionIndex: Int): Boolean = true

    override fun getSectionItemUserType(sectionIndex: Int, itemIndex: Int): Int = when {
        data[sectionIndex].sessionObjects[itemIndex].isSession -> TYPE_SESSION
        else -> TYPE_NON_SESSION
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_SESSION -> ScheduleSessionViewHolder(parent.inflate(R.layout.item_schedule_session))
        else -> ScheduleNonSessionViewHolder(parent.inflate(R.layout.item_schedule_non_session))
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup, headerUserType: Int): HeaderViewHolder =
            ScheduleHeaderViewHolder(parent.inflate(R.layout.item_schedule_header))


    override fun onBindItemViewHolder(viewHolder: ItemViewHolder, sectionIndex: Int, itemIndex: Int, itemUserType: Int) {
        val item = data[sectionIndex].sessionObjects[itemIndex]
        when (itemUserType) {
            TYPE_SESSION -> viewHolder.let { it as ScheduleSessionViewHolder }.bind(item, contract, showDivider(sectionIndex, itemIndex))
            else -> viewHolder.let { it as ScheduleNonSessionViewHolder }.bind(item)
        }
    }

    override fun onBindHeaderViewHolder(viewHolder: HeaderViewHolder, sectionIndex: Int, headerUserType: Int) {
        viewHolder.let { it as ScheduleHeaderViewHolder }.bind(data[sectionIndex])
    }

    private fun showDivider(sectionIndex: Int, itemIndex: Int): Boolean =
            data[sectionIndex].sessionObjects.lastIndex != itemIndex
}
