package cz.eman.android.devfest.addon.game.function.profile.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cz.eman.android.devfest.addon.game.R
import cz.eman.android.devfest.addon.game.function.repository.GameRepository
import cz.eman.android.devfest.addon.game.function.repository.model.KeyPart

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * This ProfileAdapter is supposed to show keys that remain to be collected by player
 *
 * @property remainingKeysItems List of KeyParts that remain to be collected by player
 * @property context Context which holds this Adapter's RecyclerView
 * @constructor Initializes this ProfileAdapter
 */
class ProfileAdapter(private val remainingKeysItems: List<KeyPart>,
                     private val context: Context) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    /**
     * Overridden function inflates ProfileViewHolder's View
     *
     * @param parent ViewGroup which holds inflates View
     * @param viewType View type of the new View
     * @return ProfileViewHolder with inflates View
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_remaining_key, parent, false)
        return ProfileViewHolder(view, this.remainingKeysItems)
    }

    /**
     * Overriden function, displays data at the specified position.
     *
     * @param holder ViewHolder representing the contents of the item at the given
     *          position in the data set
     * @param position Position of the item in the Adapter's data ser
     */
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val remainingKeyItem: KeyPart = remainingKeysItems[position]
        holder.companyStandName.text = "${remainingKeyItem.location}"

    }

    /**
     * Returns size of this Adapter's data set
     *
     * @return This Adapter's data set's size
     */
    override fun getItemCount(): Int {
        return remainingKeysItems.size
    }

    /**
     * ProfileViewHolder describes remaining keys to be collected
     *
     * @property itemView Inflated View of this ViewHolder
     * @constructor Initializes this ProfileViewHolder and sets its View
     */
    class ProfileViewHolder : RecyclerView.ViewHolder {

        val companyStandName: TextView
        val companyStands: List<KeyPart>

        constructor(itemView: View, companyStandItems: List<KeyPart>) : super(itemView){
            this.companyStandName = itemView.findViewById(R.id.companyStandName)
            this.companyStands = companyStandItems
        }

    }
}