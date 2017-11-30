package lv.rigadevday.android.ui.schedule.sessions

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import cz.eman.android.devfest.R
import kotlinx.android.synthetic.main.item_session.view.*
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.utils.hide
import lv.rigadevday.android.utils.inflate
import lv.rigadevday.android.utils.loadCircleAvatar
import lv.rigadevday.android.utils.show

class SessionsAdapter(private val contract: SessionContract) : RecyclerView.Adapter<SessionsHolder>() {

    var data: List<Session> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsHolder {
        return SessionsHolder(parent.inflate(R.layout.item_session))
    }

    override fun onBindViewHolder(holder: SessionsHolder, position: Int) {
        holder.bind(data[position], contract, data.lastIndex != position)
    }
}

class SessionsHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(session: Session, contract: SessionContract, showDivider: Boolean) {
        itemView.run {
            session_item_title.text = session.title
            session_item_tags.text = session.complexityAndTags

            if (showDivider) session_item_divider.show() else session_item_divider.hide()

            session.speakerObjects.firstOrNull()?.let { (id, _, name, _, _, photoUrl) ->
                session_item_speaker.text = name

                session_item_photo.loadCircleAvatar(photoUrl)
                session_item_photo.setOnClickListener { contract.openSpeaker(id) }
            }

            session_item_card.setOnClickListener { contract.openSession(session.id) }
        }
    }

}
