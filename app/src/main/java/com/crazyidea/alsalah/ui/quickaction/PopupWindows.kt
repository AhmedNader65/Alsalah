package com.crazyidea.alsalah.ui.quickaction

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.PopupWindow


open class PopupWindows(context: Context) {
    protected var mContext: Context
    protected var mWindow: PopupWindow
    protected open lateinit var mRootView: View
    protected var mBackground: Drawable? = null
    protected var mWindowManager: WindowManager

    /**
     * On dismiss
     */
    protected open fun onDismiss() {}

    /**
     * On show
     */
    protected fun onShow() {}

    /**
     * On pre show
     */
    protected fun preShow() {
        checkNotNull(mRootView) { "setContentView was not called with a view to display." }
        onShow()
        if (mBackground == null) mWindow.setBackgroundDrawable(BitmapDrawable()) else mWindow.setBackgroundDrawable(
            mBackground
        )
        mWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        mWindow.isTouchable = true
        mWindow.isFocusable = true
        mWindow.isOutsideTouchable = true
        mWindow.contentView = mRootView
    }

    /**
     * Set background drawable.
     *
     * @param background Background drawable
     */
    fun setBackgroundDrawable(background: Drawable?) {
        mBackground = background
    }

    /**
     * Set content view.
     *
     * @param root Root view
     */
    fun setContentView(root: View) {
        mRootView = root
        mWindow.contentView = root
    }

    /**
     * Set content view.
     *
     * @param layoutResID Resource id
     */
    fun setContentView(layoutResID: Int) {
        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setContentView(inflator.inflate(layoutResID, null))
    }

    /**
     * Set listener on window dismissed.
     *
     * @param listener
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener?) {
        mWindow.setOnDismissListener(listener)
    }

    /**
     * Dismiss the popup window.
     */
    fun dismiss() {
        mWindow.dismiss()
    }

    /**
     * Constructor.
     *
     * @param context Context
     */
    init {
        mContext = context
        mWindow = PopupWindow(context)
        mWindow.setTouchInterceptor(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                mWindow.dismiss()
                return@OnTouchListener true
            }
            false
        })
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}