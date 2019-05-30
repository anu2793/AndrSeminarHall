package jp.co.hiropro.seminar_hall.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by macmobiles on 7/21/2016.
 */
public class FontUtils {
    public static final String FONT_HIRAMARUW6 = "fonts/hiragino.otf";
    public static final String FONT_HIRAMINPRO = "fonts/hiraminpro-w6.otf";
    public static final String FONT_BOOKMAN = "fonts/BOOKOS.TTF";
    public static final String FONT_BASKERVILLE = "fonts/Baskerville SemiBold.ttf";
    public static final String FONT_HIRAMARUW3 = "fonts/hiramaruw3.otf";
    public static final String FONT_HIRAGINOSANW6 = "fonts/HiraginoSansGBW6.otf";
    public static final String FONT_KozGoPr6N_M = "fonts/KozGoPr6N_Medium.otf";
    public static final String FONT_KozGoPr6N_R = "fonts/KozGoPr6N_R.otf";
    public static final String FONT_KozGoPr6N_B = "fonts/KozGoPr6N_B.otf";
    public static final String FONT_HIRAGINOKAKU_GOTHICW3 = "fonts/hirakakuprow3.otf";
    public static final String FONT_BASK_VILL = "fonts/BASKVILL.TTF";

    private static HashMap<String, Typeface> fonts = new HashMap<>();

    public static Typeface getFont(Context context, String fontName) {
        if (!fonts.containsKey(fontName)) {
            fonts.put(fontName, Typeface.createFromAsset(context.getAssets(), fontName));
        }
        return fonts.get(fontName);
    }
}
