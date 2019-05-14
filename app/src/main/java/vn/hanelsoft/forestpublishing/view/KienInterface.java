package vn.hanelsoft.forestpublishing.view;

import android.app.Activity;

/**
 * Created by kien on 12/4/17.
 */

public interface KienInterface {

    interface View{
        void initData();
        void initViewData();
        <T>void updateUI(T object);
    }
    interface Server{
        void sendRequest();
    }
    interface Validate{
        boolean checkErrorValidate();
    }
    Activity thisActivity();
}
