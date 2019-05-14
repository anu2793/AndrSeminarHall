package vn.hanelsoft.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidateUtils {

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPhoneValid(String phone) {
	    return Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isWebsiteValid(String website) {
        return Patterns.WEB_URL.matcher(website).matches();
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

}
