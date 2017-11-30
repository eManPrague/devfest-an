package cz.eman.android.devfest.addon.game.function.leaderboard.ui

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.function.repository.model.User

/**
 * @author pavel habzansky (pavel.habzansky@eman.cz)
 *
 * LeaderBoardAdapter for managing Views in its RecyclerView in LeaderBoardFragment
 *
 * @property context Context which holds this LeaderBoardAdapter's RecyclerView
 * @constructor Initializes this Adapter and sets its properties
 */
class LeaderBoardAdapter(@NonNull val context: Context) : RecyclerView.Adapter<LeaderBoardAdapter.LBViewHolder>() {

    private var players: List<User> = emptyList()

    /**
     * Inflates ViewHolder's View and returns it
     *
     * @param parent Container where ViewHolder's View should be inflated
     * @param viewType View type of the new View
     * @return Returns LBViewHolder with inflates View
     */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LBViewHolder {
        val view: View = LayoutInflater.from(context)
                .inflate(R.layout.item_profile, parent, false)

        return LBViewHolder(view, this.players)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder ViewHolder representing the contents of the item at the given position
     * @param position Position of the item within the Adapter's data ser
     */
    override fun onBindViewHolder(holder: LBViewHolder?, position: Int) {
        val player = players[position]
        holder?.positionTextLB?.text = "#${position + 1}"
        holder?.fullNameTextLB?.text = player.name
        holder?.pointsTextLB?.text = "${player.cdhScore} ${context.getString(R.string.user_text_points)}"
        Glide.with(this.context).load(player.photoUrl).into(holder?.profilePicImgLB)

    }

    fun setPlayers(@NonNull players: List<User>) {
        this.players = players
        notifyDataSetChanged()
    }

    /**
     * Returns size of this Adapter's data set
     *
     * @return Size of this Adapter's data set
     */
    override fun getItemCount() = players.size

    /**
     * ViewHolder which describes User data on LeaderBoardFragment
     *
     * @property itemView Inflated View of this ViewHolder
     * @constructor Initializes this LBViewHolder and its Views
     */
    class LBViewHolder : RecyclerView.ViewHolder {

        val players: List<User>
        val fullNameTextLB: TextView
        val profilePicImgLB: ImageView
        val positionTextLB: TextView
        val pointsTextLB: TextView

        constructor(itemView: View, players: List<User>) : super(itemView) {
            this.players = players
            this.fullNameTextLB = itemView.findViewById(R.id.fullNameText)
            this.profilePicImgLB = itemView.findViewById(R.id.profilePicImg)
            this.positionTextLB = itemView.findViewById(R.id.positionText)
            this.pointsTextLB = itemView.findViewById(R.id.pointsText)
        }
    }

}