package com.crazyidea.alsalah.utils

import android.graphics.Color
import android.text.style.ClickableSpan
import android.text.TextPaint
import android.view.View

class MySpannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = true

    /**
     * Constructor
     */
    init {
        this.isUnderline = isUnderline
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#1b76d3")
    }

    override fun onClick(widget: View) {}
}