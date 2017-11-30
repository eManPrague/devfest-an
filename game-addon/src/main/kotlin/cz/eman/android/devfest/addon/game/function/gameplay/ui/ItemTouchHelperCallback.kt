package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * @author PavelHabzansky (pavel.habzansky@eman.cz)
 *
 * ItemTouchHelperCallback class manages moving items and their position in RecyclerView
 *
 * @property Adapter with Views to be moved
 * @constructor creates Callback with RecyclerView.Adapter
 */
class ItemTouchHelperCallback(val adapter: KeySortAdapter) : ItemTouchHelper.Callback() {

    /**
     * Overriden function enabling swiping Views inside Adapter
     * Swiping is disabled in this instance
     *
     * @return Boolean value, if true, swiping is enabled, if false, swiping is disabled
     */
    override fun isItemViewSwipeEnabled() = false

    /**
     * Overriden function enabling draging Views inside Adapted
     * Draging is enabled in this instance
     *
     * @return Boolean value, if true, draging is enabled, if false, swiping is disabled
     */
    override fun isLongPressDragEnabled() = true

    /**
     * Overriden function managing moving Views in Adapter
     *
     * @param recyclerView parent RecyclerView of moved View
     * @param source source View to be moved
     * @param target target View to be switched with
     */
    override fun onMove(recyclerView: RecyclerView?, source: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        adapter.onItemMove(source?.adapterPosition, target?.adapterPosition)
        return false
    }

    /**
     * Overriden function managing swipping event
     * Swipping is disabled in this instance
     *
     * @param viewHolder View to be swiped
     * @param direction direction of swipping
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        // swiping not needed
    }

    /**
     * Overriden function setting draging/swipping capabilities of ItemTouchHelperCallback
     *
     * @param recyclerView to which ItemTouchHelper is assigned
     * @param viewHolder for which the movement information is necessary
     *
     * @return flags specifying which movements are allowed on this ViewHolder
     */
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeMovementFlags(dragFlags, swipeFlags)
    }
}