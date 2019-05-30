package jp.co.hiropro.seminar_hall.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dinhdv on 3/6/2018.
 */

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {
    private static final int MAX_LINES = 5;
    private int currentMaxLines = Integer.MAX_VALUE;
    private onChangeStatus mAction;
    private boolean mIsShow = false;

    public ExpandableTextView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        /* If text longer than MAX_LINES set DrawableBottom - I'm using '...' icon */
        post(new Runnable() {
            public void run() {
                mIsShow = getLineCount() > MAX_LINES;
                if (getLineCount() > MAX_LINES) {
                    if (mAction != null)
                        mAction.onShow();
                } else {
                    if (mAction != null)
                        mAction.onHidden();
                }
//                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_more_text);
//                else
//                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                setMaxLines(MAX_LINES);
            }
        });
    }


    @Override
    public void setMaxLines(int maxLines) {
        currentMaxLines = maxLines;
        setEllipsize(TextUtils.TruncateAt.END);
        super.setMaxLines(maxLines);
    }

    public boolean getStatus() {
        return mIsShow;
    }

    /* Custom method because standard getMaxLines() requires API > 16 */
    public int getMyMaxLines() {
        return currentMaxLines;
    }

    @Override
    public void onClick(View v) {
        if (mAction != null)
            mAction.onClickContent();
        /* Toggle between expanded collapsed states */
//        if (getMyMaxLines() == Integer.MAX_VALUE)
//            setMaxLines(MAX_LINES);
//        else
//            setMaxLines(Integer.MAX_VALUE);
    }

    public void setListener(onChangeStatus action) {
        mAction = action;
    }

    public interface onChangeStatus {
        void onShow();

        void onHidden();

        void onClickContent();
    }

}