package lv.rigadevday.android.ui.speakers

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_speaker.view.*
import cz.eman.android.devfest.R
import lv.rigadevday.android.repository.model.speakers.Speaker
import lv.rigadevday.android.utils.hide
import lv.rigadevday.android.utils.inflate
import lv.rigadevday.android.utils.loadSquareAvatar
import lv.rigadevday.android.utils.show

class SpeakersAdapter(val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<SpeakerViewHolder>() {

    var data: List<Speaker> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = SpeakerViewHolder(parent.inflate(R.layout.item_speaker))

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.bind(data[position], onItemClick)
    }

    override fun getItemCount(): Int = data.size

}

class SpeakerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(speaker: Speaker, onClick: (Int) -> Unit): Unit = with(itemView) {
        setOnClickListener { onClick(speaker.id) }

        speakers_item_name.text = speaker.name
        speakers_item_company.text = speaker.company

        speakers_item_image.loadSquareAvatar(speaker.photoUrl)

        speakers_item_bagde.apply {
            if (speaker.badges.isEmpty()) hide()
            else {
                show()
                text = speaker.badges[0].name
            }
        }
    }

}
