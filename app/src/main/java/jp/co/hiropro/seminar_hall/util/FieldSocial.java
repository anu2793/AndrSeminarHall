package jp.co.hiropro.seminar_hall.util;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by kien on 12/13/17.
 */

public class FieldSocial {
    @IntDef({YOUTUBE, GOOGLE, FACEBOOK, TWITTER, INSTAGRAM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public static final int YOUTUBE = 1;
    public static final int GOOGLE = 2;
    public static final int FACEBOOK = 3;
    public static final int TWITTER = 4;
    public static final int INSTAGRAM = 5;
}
