package jp.co.hiropro.seminar_hall.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.util.FontUtils;


/**
 * Clone Dinhdv from Tuấn Sơn on 29/4/2016.
 */
public class ButtonApp extends android.support.v7.widget.AppCompatButton {

    private final int DEFAULT_FONT_STYLE = 0;
    private final int DEFAULT_TEXT_FONT = 0;

    int textStyle = DEFAULT_FONT_STYLE;
    int textFont = DEFAULT_TEXT_FONT;
    String font = FontUtils.FONT_HIRAMARUW3;

    public ButtonApp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TextViewApp, defStyleAttr, 0);
        textStyle = typedArray.getInt(R.styleable.TextViewApp_textStyle, DEFAULT_FONT_STYLE);
        textFont = typedArray.getInt(R.styleable.TextViewApp_fontFamilyApp, DEFAULT_TEXT_FONT);
        switch (textFont) {
            case 0://font hiramaru
                font = FontUtils.FONT_HIRAMARUW3;
                break;
            case 1://font makinas
                font = FontUtils.FONT_HIRAMINPRO;
                break;
            case 2://font bookman
                font = FontUtils.FONT_BOOKMAN;
                break;
            case 3:
                font = FontUtils.FONT_BASKERVILLE;
                break;
            case 4:
                font = FontUtils.FONT_HIRAMARUW6;
                break;
            case 6:
                font = FontUtils.FONT_KozGoPr6N_M;
                break;

        }
        init();
    }

    public ButtonApp(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public ButtonApp(Context context) {
        super(context);
        init();
    }

    public void init() {
//        if (!isInEditMode())
        setTypeface(FontUtils.getFont(getContext(), font), textStyle);
    }
}
