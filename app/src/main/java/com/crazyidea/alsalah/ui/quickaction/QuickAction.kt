package com.crazyidea.alsalah.ui.quickaction

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import android.widget.PopupWindow.OnDismissListener
import com.crazyidea.alsalah.R


class QuickAction @JvmOverloads constructor(
    context: Context,
) :
    PopupWindows(context), OnDismissListener {
    override lateinit var mRootView: View
    private val mInflater: LayoutInflater
    private var mTrack: ViewGroup? = null
    private var mScroller: ScrollView? = null
    private var mItemClickListener: OnActionItemClickListener? = null
    private var mDismissListener: OnDismissListener? = null
    private val actionItems: MutableList<ActionItem> = ArrayList()
    private var mDidAction = false
    private var mChildPos: Int
    private var mInsertPos = 0
    private var mAnimStyle: Int
    private var rootWidth = 0

    /**
     * Get action item at an index
     *
     * @param index  Index of item (position from callback)
     *
     * @return  Action Item at the position
     */
    fun getActionItem(index: Int): ActionItem {
        return actionItems[index]
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    fun setRootViewId(id: Int) {
        mRootView = mInflater.inflate(id, null) as ViewGroup
        mTrack = mRootView.findViewById<View>(R.id.tracks) as ViewGroup
        mScroller = mRootView.findViewById<View>(R.id.scroller) as ScrollView

        //This was previously defined on show() method, moved here to prevent force close that occured
        //when tapping fastly on a view to show quickaction dialog.
        //Thanx to zammbi (github.com/zammbi)
        mRootView.layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        setContentView(mRootView)
    }

    /**
     * Set animation style
     *
     * @param mAnimStyle animation style, default is set to ANIM_AUTO
     */
    fun setAnimStyle(mAnimStyle: Int) {
        this.mAnimStyle = mAnimStyle
    }

    /**
     * Set listener for action item clicked.
     *
     * @param listener Listener
     */
    fun setOnActionItemClickListener(listener: OnActionItemClickListener?) {
        mItemClickListener = listener
    }

    fun onClear() {
        if (actionItems.isNotEmpty()) {
            actionItems.clear()
            mTrack!!.removeAllViews()
            mChildPos = 0
            mInsertPos = 0
        }
    }

    /**
     * Add action item
     *
     * @param action  [ActionItem]
     */
    fun addActionItem(action: ActionItem) {
        actionItems.add(action)
        val title = action.title
        val icon = action.icon
        val container: View = mInflater.inflate(R.layout.action_item_horizontal, null)

        val img = container.findViewById<View>(R.id.iv_icon) as ImageView
        if (icon != null) {
            img.setImageDrawable(icon)
        } else {
            img.visibility = View.GONE
        }
        val pos = mChildPos
        val actionId = action.actionId
        val selectIdx = action.selectIdx
        container.setOnClickListener {
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(this@QuickAction, pos, actionId, selectIdx)
            }
            if (!getActionItem(pos).isSticky) {
                mDidAction = true
                dismiss()
            }
        }
        container.isFocusable = true
        container.isClickable = true
        if ( mChildPos != 0) {
            val separator: View = mInflater.inflate(R.layout.horiz_separator, null)
            val params =
                RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT)
            separator.layoutParams = params
            separator.setPadding(5, 0, 5, 0)
            mTrack!!.addView(separator, mInsertPos)
            mInsertPos++
        }
        mTrack!!.addView(container, mInsertPos)
        mChildPos++
        mInsertPos++
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     *
     */
    fun show(anchor: View, x: Float, y: Int) {
        preShow()
        var xPos: Int
        mDidAction = false
        val anchorRect = Rect(
            x.toInt(), y, (x + anchor.width).toInt(), (y
                    + anchor.height)
        )

        //mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val rootHeight = mRootView.measuredHeight
        if (rootWidth == 0) {
            rootWidth = mRootView.measuredWidth
        }
        val screenWidth: Int = mWindowManager.getDefaultDisplay().getWidth()
        val screenHeight: Int = mWindowManager.getDefaultDisplay().getHeight()

        //automatically get X coord of popup (top left)
        if (anchorRect.left + rootWidth > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.width)
            xPos = if (xPos < 0) 0 else xPos
        } else {
            xPos = if (anchor.width > rootWidth) {
                anchorRect.centerX() - rootWidth / 2
            } else {
                anchorRect.left
            }
        }
        setAnimationStyle(screenWidth, anchorRect.centerX(), true)
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, y)
    }

    /**
     * Set animation style
     *
     * @param screenWidth screen width
     * @param requestedX distance from left edge
     * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
     * and vice versa
     */
    private fun setAnimationStyle(screenWidth: Int, requestedX: Int, onTop: Boolean) {

        /*switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
		break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			}

			break;
		}*/
    }


    /**
     * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
     * by clicking outside the dialog or clicking on sticky item.
     */
    fun setOnDismissListener(listener: OnDismissListener?) {
        setOnDismissListener(this)
        mDismissListener = listener
    }

    override fun onDismiss() {
        if (!mDidAction && mDismissListener != null) {
            mDismissListener!!.onDismiss()
        }
    }

    /**
     * Listener for item click
     *
     */
    interface OnActionItemClickListener {
        fun onItemClick(source: QuickAction, pos: Int, actionId: Int, selectIdx: Int)
    }

    /**
     * Listener for window dismiss
     *
     */
    interface OnDismissListener {
        fun onDismiss()
    }

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        const val ANIM_GROW_FROM_LEFT = 1
        const val ANIM_GROW_FROM_RIGHT = 2
        const val ANIM_GROW_FROM_CENTER = 3
        const val ANIM_REFLECT = 4
        const val ANIM_AUTO = 5
    }
    /**
     * Constructor allowing orientation override
     *
     * @param context    Context
     * @param mOrientation Layout orientation, can be vartical or horizontal
     */
    /**
     * Constructor for default vertical layout
     *
     * @param context  Context
     */
    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setRootViewId(R.layout.popup_horizontal)
        mAnimStyle = ANIM_AUTO
        mChildPos = 0
    }
}