package jp.co.hiropro.seminar_hall.util;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import jp.co.hiropro.seminar_hall.BuildConfig;

/**
 * Created by Hss on 8/19/2015.
 */
public class AppConstants {
    /**
     * @Variable : isTestMode
     * Using with billing.
     * If True : Using code test purchase. Server : Dev.
     * If Fail : Using real purchase. Server : UAT
     */
    public static boolean isTestMode = BuildConfig.TEST_MODE;
    public static int CLIENT_ID = 1;
    public static int REQUEST_SUCCESS = 200;
    public static final String DEVELOPER_KEY = "903965347757";

    public static String mUrlResultQuestion = "";

    public static class TEST {
        public static String USER_NAME = "guest@guest.forest";
        public static String PASSWORD = "guestguest";
    }

    public static String getUrlResultQuestion() {
        return mUrlResultQuestion;
    }

    public static void setUrlResultQuestion(String mUrlResultQuestion) {
        AppConstants.mUrlResultQuestion = mUrlResultQuestion;
    }

    public static String getLinkAdmin() {
        //DEV
        return BuildConfig.BASE_URL;

//         Release
//        return "http://hiro-evergreen.video/service/";
    }

    public static class WebSocketLink {
        // DEV
//        public static String LINK_SEND = "ws://alexapp.ito.vn:9696/";
        // Release
//        public static String LINK_SEND = "ws://alexapp.uat.ito.vn:9595/";
        public static String LINK_SEND = BuildConfig.LINK_SEND;
    }


    public static class STATUS_REQUEST {
        public static int REQUEST_ERROR = 0;
        public static int EMAIL_HAS_REGISTED = 455;
        public static int SERVER_MAINTAIN = 503;
        public static int TOKEN_EXPIRED = 401;
        public static int LIMIT_DEVICE = 460;
        public static int CONTACT_HAS_EXISTS = 458;
        public static int EMAIL_HAS_REGISTER = 462;
        public static int REQUEST_REQUIRE_EMAIL = 461;
        public static int ACCOUNT_BLOCKED = 463;
        public static int NOT_ENOUGH_POINT = 506;
        public static int VIDEO_HAS_BUY = 507;
    }

    public static class KEY_SEND {
        public static final String KEY_ID_NEWS = "KEY_ID_NEWS";
        public static final String KEY_ID_CAMPAIGN = "KEY_ID_CAMPAIGN";
        public static String KEY_SEND_NEW_OBJECT = "KEY_SEND_NEW_OBJECT";
        public static String KEY_SEND_EMAIL = "KEY_SEND_EMAIL";
        public static String KEY_URL_CAMPAIN = "KEY_URL_CAMPAIN";
        public static String KEY_URL_HYPERLINK = "KEY_URL_HYPERLINK";
        public static String KEY_CHECK_OPEN_NEW_FROM_NOTIFICATION = "KEY_CHECK_OPEN_NEW_FROM_NOTIFICATION";
        public static String KEY_SEND_ID_PURCHASE = "KEY_SEND_ID_PURCHASE";
        public static String KEY_MSG_MAINTAIN = "KEY_MSG_MAINTAIN";
        public static String KEY_SEND_PASSWORD = "KEY_SEND_PASSWORD";
        public static String KEY_SEND_TAB = "KEY_SEND_TAB";
        public static String KEY_DATA = "KEY_DATA";
        public static String KEY_POSITION_TAB = "KEY_POSITION_TAB";
        public static String KEY_VIDEO = "KEY_VIDEO";
        public static String KEY_SHOW_ADVISE = "KEY_SHOW_ADVISE";
        public static String KEY_TEACH_NEWS = "KEY_TEACH_NEWS";
    }

    public static class TYPE_SEARCH {
        public static int ALL = 0;
        public static int DEFAULT = 1;
        public static int BOOK = 2;
        public static int VIDEO_FREE = 3;
        public static int VIDEO_BUY = 4;
        public static int FAVORITE_CONTENT = 5; //Favourite content
    }


    public static class TYPE_DEVICE {
        public static int ANDROID = 2;
    }

    public static class STATUS_NEW {
        public static final int READ = 1;
        public static final int UNREAD = 0;
    }

    public static final class STATUS_USER_PURCHASE {
        public static final int FREE = 1;
        public static final int BOUGHT = 2;
        public static final int UPDATE = 3;
    }

    public static final class STATUS_LIST_SEMINAR {
        public static final int NEW = 0;
        public static final int POPULAR = 1;
    }

    public static class STATUS_SUBCRIPTION {
        public static int ENABLE_RENEW = 1;
        public static int DISABLE_RENEW = 0;
    }

    public static class STATUS_USER {
        public static final int NORMAL = 0;
        public static final int REQUESTING = 1;
        public static final int DONE = 2;
        public static final int ERROR = 3;
    }

    public static class TYPE_UER {
        public static int NORMAL = 1;
        public static int TEACHER = 2;

    }


    public enum KEY_INTENT {
        URL_DIAGNOSIS("KEY_URL_DIAGNOSIS"),
        SEARCH_VALUES("SEARCH_VALUES"),
        ID_USER("ID_USER"),
        IS_REGISTER_USER("IS_REGISTER_USER"),
        SHOW_BACK("SHOW_BACK"),
        VALUE_CHANGE("value_change"),
        FORGOT_RESULT("forgot_result"),
        VALUE("value"),
        TYPE_FIELD("TypeField"),
        IS_SKIP_USER("IS_SKIP_USER"),
        LIST_CONTACT("list_contact"),
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        LIST_FREE_VIDEO("video/listfree");

        String link;

        KEY_INTENT(String values) {
            link = values;
        }

        @Override
        public String toString() {
            return link;
        }
    }

    public enum SERVER_PATH {
        LOGIN("user/login"),
        GET_CONTACT_LIST("user/getContactList"),
        REMOVE_CONTACT_LIST("user/removeContactList"),
        REGISTER("user/register"),
        CREATE_UPDATE_PROFILE("user/createUpdateProfile"),
        GET_MY_PROFILE("user/getMyProfile"),
        SPLASH("token"),
        FORGOT_PASSWORD("user/forgotPassword"),
        TOP("top"),
        PROFILE("user/profile"),
        CHANGE_EMAIL("user/updateProfile"),
        SUBCATEGORY("video/listSubCate"),
        SUBSUBCATEGORY("video/listBook"),
        SUBCATEGORYDETAIL("video/list"),
        NEWS("news"),
        CONTENT_DETAIL("video"),
        FAVORITE_SUBCATEGORY("user/setFavourite"),
        VIEWED_VIDEO("video/view"),
        LIST_FREE_VIDEO("video/listfree"),
        FREE_VIDEO_DETAIL("video/freedetail"),
        FAVORITE("user/favourite"),
        SEARCH_TAG("tag"),
        NEW_DETAIL("news/detail"),
        LOGOUT("user/logout"),
        LIST_PURCHASE("purchase/listPremium"),
        SEARCH("search"),
        HISTORY("video/history"),
        HISTORY_PURCHASE("video/mylibrary"),
        PURCHASE_VIDEO("purchase/setBoughtVideo"),
        UPDATE_STATUS_PURCHASE("purchase/updatePremium"),
        FAVORITE_BOOK("user/favouriteBook"),
        FAVORITE_VIDEOS("user/favouriteVideo"),
        CHECK_CONFIG_SKIP("user/checkLoginConfig"),
        LIST_DEVICE("user/listDevice"),
        REMOVE_DEVICE("user/removeDevice"),
        JACK_PROFILE("profile"),
        JACK_LIST_VIDEO("video/list"),
        GET_QR_CODE("user/genqrcode"),
        SHOP_TOP("shop"),
        UPDATE_PURCHASE("purchase/setpaidpoint"),
        CHECK_STATUS_PURCHASE(""),
        GET_CARD_QRCODE("user/getCardByQRCode"),
        GET_SHOP_SEMINAR("shop/seminarlist"),
        ADD_CARD_CONTACT("user/addContactList"),
        SHOP_CATE_LIST("shop/categorylist"),
        SHOP_TEACHER_LIST("shop/teacherlist"),
        LIST_SEMINAR("video/listSeminar"),
        LIST_SEMINAR_TOP("seminar/top"),
        LIST_VIDEO_CATEGORY_TOP("video/categoryTop"),
        GET_CONTENT_LIST("teacher/getContentList"),
        GET_POINT("purchase/listpackage"),
        SEND_NEWS("teacher/createNews"),
        DELETE_NEWS("teacher/deleteNews"),
        LIST_TEACH_NEWS("teacher/getListNews"),
        NEW_DETAIL_TEACH("teacher/getNewsDetail"),
        UPDATE_NEWS("teacher/updateNews"),
        BUY_MONTHLY_SUCCESS("purchase/setPaidPremium");

        String link;

        SERVER_PATH(String values) {
            link = values;
        }

        @Override
        public String toString() {
            return getLinkAdmin() + link;
        }
    }

    public enum KEY_PARAMS {
        DEVICE_ID("device_id"),
        TOKEN("token"),
        NAME("name"),
        DATA("data"),
        CLIENT_ID("client_id"),
        PASSWORD("password"),
        EMAIL("email"),
        ID_PURCHASE("id_purchase"),
        AUTH_TOKEN("auth_token"),
        TYPE("type"),
        DEVICE_NAME("device_name"),
        OS_VERSION("os_version"),
        STATUS("status"),
        USER_INFO("userInfo"),
        USER_CLIENT_ID("user_client_id"),
        PROGRESS_WATCHES("progress_watches"),
        TOTAL("total"),
        VIEWED("viewed"),
        PREMIUM_REMAIN("premium_remain"),
        TITLE("title"),
        OLD_PASSWORD("old_password"),
        CREATE_AT("created_at"),
        UPDATE_AT("updated_at"),
        PRICE("price"),
        PRICE_CODE("price_code"),
        DISCOUNT_POINT("discount_point"),
        REMAIN("remain"),
        ID("id"),
        DETAIL("detail"),
        PREMIUM_ID("premiumid"),
        DATE_FROM("datefrom"),
        DATE_TO("dateto"),
        MY_POINT("mypoint"),
        LIST("list"),
        MEMBER_ID("memberid"),
        PACKAGE_ID("packageid"),
        COST_PRICE("cost_price"),
        COST_POINT("cost_point"),
        PREMIUM_TO_DATE("premium_to_date"),
        MY_PREMIUM("mypremium"),
        PREMIUM_INFO("premiumInfo"),
        PRICE_ID("price_id"),
        MONTH("month"),
        CURRENT_PRICE("current_price"),
        CURRENT_END_DATE("current_enddate"),
        NEXT_PRICE("next_price"),
        NEXT_START_DATE("next_startdate"),
        MESSAGE("message"),
        PREMIUM_CODE("premium_code"),
        ORDER_NO("order_no"),
        COST("cost"),
        CURRENCY("currency"),
        PURCHASE_INFO("purchase_info"),
        PURCHASE_TOKEN("purchase_token"),
        NEW_APP("newapp"),
        OS_TYPE("ostype"),
        LIST_SUB_CATE("listSubCate"),
        HIDE_NEWS("hide_news"),
        LIST_NEWS("list_news"),
        REGISTER_REQUIRE("register_require"),
        DEVICE_OS("device_os"),
        IS_CURRENT_DEVICE("isCurrent"),
        INFO("info"),
        IMAGE("image"),
        BODY("body"),
        ORDER("order"),
        LINK("link"),
        LIST_CATEGORY("list_category"),
        LIST_BANNER("list_banner"),
        LIST_CAMPAIGN("list_campaign"),
        DESCRIPTION("description"),
        PRM_VIDEO_URL("promotion_video_url"),
        TOP("top"),
        ACCESS_TOKEN("access_token"),
        REGISTER_TYPE("register_type"),
        LOGIN_TYPE("login_type"),
        QR_CODE("qr_code"),
        LIST_SEMINAR_PAY("list_seminar_pay"),
        LIST_SEMINAR_FREE("list_seminar_free"),
        LIST_SEMINAR_POPULAR("list_seminar_popular"),
        LIST_TEACHER("list_teacher"),
        TEACHER_ID("teacher_id"),
        FULLNAME("fullname"),
        PROFILE("profile"),
        LIST_SEMINAR_GENERAL("list_seminar_general"),
        FB_ID("fbid"),
        GOOGLE("google_plus"),
        YOUTUBE("youtube"),
        FACEBOOK("facebook"),
        TWITTER("twitter"),
        INSTAGRAM("instagram"),
        AVATAR("avatar"),
        LIST_SEMINAR_NEWS("list_seminar_news"),
        LIST_SEMINAR_SUGGEST("list_seminar_suggest"),
        CONTACT_ID("contact_id"),
        NEXT_PAGE("next_page"),
        NAME_JP("name_jp"),
        PAGE("page"),
        CONTACT_LIST("contact_list"),
        CONTENT_LIST("content_list"),
        SORT_TYPE("sorttype"),
        TEACHER("teacher"),
        IS_CREATE("isCreate"),
        POINT("point"),
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        CHECK_UPDATE_PROFILE("check_update_profile"),
        NEW_PASSWORD("new_password"), COLOR("color");

        private String mName;

        KEY_PARAMS(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public enum KEY_PREFERENCE {
        AUTH_TOKEN("auth_token"),
        FB_ID("fb_id"),
        IS_SOCIAL("is_social"),
        ACCESS_TOKEN("access_token"),
        IS_FIRST_RUN("IS_FIRST_RUN"),
        IS_LOGIN("IS_LOGIN"),
        DATA_POINT("IS_LOGIN"),
        IS_REGISTER_SUCCESS("IS_REGISTER_SUCCESS"),
        KEY_DEFAULT_QUESTION("Nica_question_");

        private final String mName;

        KEY_PREFERENCE(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static class BROAD_CAST {
        public static String SEARCH = "BROAD_CAST_SEARCH";
        public static String REFRESH = "BROAD_CAST_REFRESH";
        public static String CHANGE_TAB = "BROAD_UPDATE_VIEWPAGER";
        public static String UPDATE_SEMINAR_LIST = "UPDATE_SEMINAR_LIST";
    }

    public static class StaticParam {//kien
        public static final String EMPTY_VALUE_STRING = "";
        public static final int TYPE_OF_FACEBOOK = 1;
        public static final int TYPE_OF_GOOGLE = 2;
        public static GoogleApiClient mGoogleApiClient = null;
        public static final int REQUEST_CODE = 1;
    }
}
