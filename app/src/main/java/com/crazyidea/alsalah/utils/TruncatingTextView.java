package com.crazyidea.alsalah.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.crazyidea.alsalah.R;

public class TruncatingTextView extends AppCompatTextView {
    private static final String TWO_SPACES = "  ";

    private int truncateAfter = Integer.MAX_VALUE;

    private String suffix;
    private final RelativeSizeSpan truncateTextSpan = new RelativeSizeSpan(0.75f);
    private ForegroundColorSpan viewMoreTextSpan;
    private final String moreString = getContext().getString(R.string.more);

    private final String ellipsis = getContext().getString(R.string.ellipsis);

    public TruncatingTextView(Context context) {
        super(context);
        init();
    }

    public TruncatingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TruncatingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewMoreTextSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.green));
    }

    public void setText(CharSequence fullText, @Nullable CharSequence afterTruncation, int truncateAfterLineCount) {
        this.suffix = TWO_SPACES + moreString;

        if (!TextUtils.isEmpty(afterTruncation)) {
            suffix += TWO_SPACES + afterTruncation;
        }

        if (this.truncateAfter != truncateAfterLineCount) {
            this.truncateAfter = truncateAfterLineCount;
            setMaxLines(truncateAfter);
        }

        setText(fullText);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getLayout() != null && getLayout().getLineCount() > truncateAfter) {
            int lastCharToShowOfFullTextAfterTruncation = getLayout().getLineVisibleEnd(truncateAfter - 1) - suffix.length() - ellipsis.length();

            int startIndexOfMoreString = lastCharToShowOfFullTextAfterTruncation + TWO_SPACES.length() + 1;

            SpannableString truncatedSpannableString = new SpannableString(getText().subSequence(0, lastCharToShowOfFullTextAfterTruncation) + ellipsis + suffix);
            truncatedSpannableString.setSpan(truncateTextSpan, startIndexOfMoreString, truncatedSpannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            truncatedSpannableString.setSpan(viewMoreTextSpan, startIndexOfMoreString, startIndexOfMoreString + moreString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(truncatedSpannableString);
        }
    }
}