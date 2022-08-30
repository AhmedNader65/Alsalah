package com.crazyidea.alsalah.ui.quickaction

import android.graphics.Bitmap

import android.graphics.drawable.Drawable


class ActionItem @JvmOverloads constructor(
    actionId: Int = -1,
    /**
     * Set action title
     *
     * @param title action title
     */
    var title: String? = null,
    /**
     * Set action icon
     *
     * @param icon [Drawable] action icon
     */
    var icon: Drawable? = null, select: Int = -1
) {
    /**
     * Get action icon
     * @return  [Drawable] action icon
     */
    /**
     * Get thumb image
     *
     * @return Thumb image
     */
    /**
     * Set thumb
     *
     * @param thumb Thumb image
     */
    var thumb: Bitmap? = null
    /**
     * Get action title
     *
     * @return action title
     */
    /**
     * @return  Our action id
     */
    /**
     * Set action id
     *
     * @param selectid  Action id for this action
     */
    var selectIdx: Int
    /**
     * @return  Our action id
     */
    /**
     * Set action id
     *
     * @param actionId  Action id for this action
     */
    var actionId = -1
    /**
     * Check if item is selected
     *
     * @return true or false
     */
    /**
     * Set selected flag;
     *
     * @param selected Flag to indicate the item is selected
     */
    var isSelected = false
    /**
     * @return  true if button is sticky, menu stays visible after press
     */
    /**
     * Set sticky status of button
     *
     * @param sticky  true for sticky, pop up sends event but does not disappear
     */
    var isSticky = false

    /**
     * Constructor
     *
     * @param icon [Drawable] action icon
     */
    constructor(icon: Drawable?) : this(-1, null, icon, -1)

    /**
     * Constructor
     *
     * @param actionId  Action ID of item
     * @param icon      [Drawable] action icon
     */
    constructor(actionId: Int, icon: Drawable?) : this(actionId, null, icon, -1)
    /**
     * Constructor
     *
     * @param actionId  Action id for case statements
     * @param title     Title
     * @param icon      Icon to use
     */
    /**
     * Constructor
     */
    /**
     * Constructor
     *
     * @param actionId  Action id of the item
     * @param title     Text to show for the item
     */
    init {
        icon = icon
        this.actionId = actionId
        selectIdx = select
    }
}