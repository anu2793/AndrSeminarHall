package vn.hanelsoft.forestpublishing.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by dinhdv on 7/21/2017.
 */

public class AuthenticationService extends Service {
    public AuthenticationService() {
        // TODO Auto-generated constructor stub
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ForestAuthentication(this).getIBinder();
    }
}
