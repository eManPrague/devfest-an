package cz.eman.android.devfest.addon.game.function.gameplay.ui

import android.support.v7.widget.RecyclerView

/**
 * @author vsouhrada (vaclav.souhrada@eman.cz)
 */
interface IOnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

}