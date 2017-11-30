package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cz.eman.android.devfest.addon.game.R
import java.util.*

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * KeySortAdapter class manages ViewHolders for sorting
 *
 * @property keysToSort List of items to be viewed in this Adapter's RecyclerView
 * @property context Context where this Adapter is to be set
 * @constructor Creates KeySortAdapter
 */
class KeySortAdapter(private val keysToSort: List<Pair<String, String>>,
                     private val context: Context,
                     private val dragStartDragListener: IOnStartDragListener)

    : RecyclerView.Adapter<KeySortAdapter.KeySortVH>() {

    /**
     * Returns this Adapter's keysToSort property
     *
     * @return List of Pair<String, String>
     */
    fun getKeysToSort(): List<Pair<String, String>> {
        return this.keysToSort
    }

    /**
     * Function manages situation of moving Views in this Adapter's RecyclerView
     *
     * @param fromPosition Position from which View is moved
     * @param toPosition Position to which View is moved
     */
    fun onItemMove(fromPosition: Int?, toPosition: Int?) {
        if (fromPosition == null || toPosition == null) return
        Collections.swap(keysToSort, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Overriden function inflates View for ViewHolder
     *
     * @return ViewHolder with inflated View
     */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): KeySortVH {
        val view: View = LayoutInflater.from(parent?.context)
                .inflate(R.layout.card_sort_key, parent, false)

        return KeySortVH(view, this.keysToSort)
    }

    /**
     * Overriden function setting Views inside ViewHolder
     */
    override fun onBindViewHolder(holder: KeySortVH, position: Int) {
        holder.keyPart?.text = keysToSort[position].first
        holder.controlWord?.text = keysToSort[position].second
        holder.keySortImage.setOnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                dragStartDragListener.onStartDrag(holder)
            }

            return@setOnTouchListener false;
        }
    }

    /**
     * Overriden function returning count of items in this Adapter
     *
     * @return count of items in this Adapter
     */
    override fun getItemCount() = keysToSort.size

    /**
     * KeySortVH is managing View to be showed
     *
     * @property itemView Inflated View of this ViewHolder
     * @property keysToSort List of items holding data for this ViewHolder's Views
     * @constructor Initializes this ViewHolder and its Views
     */
    class KeySortVH : RecyclerView.ViewHolder {

        private val keysToSort: List<Pair<String, String>>
        val keyPart: TextView
        val controlWord: TextView
        val keySortImage: ImageView

        constructor(itemView: View, keysToSort: List<Pair<String, String>>) : super(itemView) {
            this.keysToSort = keysToSort

            this.keyPart = itemView.findViewById(R.id.keyCodeSort)
            this.controlWord = itemView.findViewById(R.id.unlockedStringSort)
            this.keySortImage = itemView.findViewById(R.id.keySortImg)
        }
    }
}