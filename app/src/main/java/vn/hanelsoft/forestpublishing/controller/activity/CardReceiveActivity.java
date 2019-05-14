package vn.hanelsoft.forestpublishing.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.service.socket.SocketListener;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.AppUtils;
import vn.hanelsoft.forestpublishing.util.CenterRecycleViewSnapHelper;
import vn.hanelsoft.forestpublishing.util.RequestDataUtils;
import vn.hanelsoft.forestpublishing.view.SwipeController;
import vn.hanelsoft.forestpublishing.view.TextViewApp;
import vn.hanelsoft.forestpublishing.view.adapter.CardContactAdapter;

/**
 * Created by dinhdv on 3/14/2018.
 */

public class CardReceiveActivity extends FragmentActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, OnRequestPermissionsResultCallback {
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private static final long TIME_UPDATE_CURRENT_LOCATION = 10000;
    @BindView(R.id.rcy_card)
    RecyclerView mRcyCard;
    @BindView(R.id.imv_down)
    ImageView mImvDown;
    @BindView(R.id.ll_footer)
    LinearLayout mLlFooter;
    @BindView(R.id.imv_loading)
    ImageView mImvLoading;
    @BindView(R.id.ll_loading)
    LinearLayout mLlLoading;
    @BindView(R.id.tv_title)
    TextViewApp mTvTitle;
    private CardContactAdapter mAdapter;
    private ArrayList<User> mListContact;
    private ArrayList<User> mListReject;
    private OkHttpClient mClient;
    private User mReciveUser = null;
    private boolean isSendRequest = false;
    private WebSocket mWs = null;
    private GoogleApiClient mGoogleApiClient;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private Timer mTimer = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_receive);
        ButterKnife.bind(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initView();
        setupRecycleView();
        if (mClient == null)
            mClient = new OkHttpClient();
//        if (checkPlayServices()) {
//            buildGoogleApiClient();
//        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLatitude = bundle.getDouble(AppConstants.KEY_INTENT.LATITUDE.toString(), 0);
            mLongitude = bundle.getDouble(AppConstants.KEY_INTENT.LONGITUDE.toString(), 0);
            initReceiver(mLatitude, mLongitude);
        }
        initTimer();
    }

    private void initTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // use runOnUiThread(Runnable action)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLocationCurrentRealTime();
                        }
                    });
                }
            }, TIME_UPDATE_CURRENT_LOCATION, TIME_UPDATE_CURRENT_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @OnClick({R.id.imv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_back:
                onBackPressed();
                break;
        }
    }

    private void getLocationCurrentRealTime() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (null != location && location.getLongitude() > 0) {
                            if (location.getLongitude() != mLongitude) {
//                                Toast.makeText(CardReceiveActivity.this, "UPDATE", Toast.LENGTH_SHORT).show();
                                mLongitude = location.getLongitude();
                                mLatitude = location.getLatitude();
                            }
                            initReceiver(mLatitude, mLongitude);
                        }
                    }
                });
    }

    private void initView() {
        Glide.with(CardReceiveActivity.this).asGif().load(R.drawable.delete_cr).into(mImvDown);
        Glide.with(CardReceiveActivity.this).asGif().load(R.drawable.waiting_cr).into(mImvLoading);
        mListContact = new ArrayList<>();
        mListReject = new ArrayList<>();
        int actionBarHeight = 1;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.value_25dp);
        int paddingBottom = getResources().getDimensionPixelSize(R.dimen.value_90dp);
        int heightOfRecycleView = AppUtils.getScreenHigh() - paddingTop - paddingBottom - actionBarHeight;
        mAdapter = new CardContactAdapter(CardReceiveActivity.this, mListContact, heightOfRecycleView, new CardContactAdapter.onActionSendRequest() {
            @Override
            public void onSend(User user, int position) {
                try {
                    mListContact.get(position).setStatus(AppConstants.STATUS_USER.REQUESTING);
                    mAdapter.notifyItemChanged(position);
//                mAdapter.notifyDataSetChanged();
                    sendRequestAddContact(user, position);
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }

            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRcyCard.setLayoutManager(lm);
        mRcyCard.setHasFixedSize(true);
        mRcyCard.setAdapter(mAdapter);
    }

    private void initReceiver(double latitude, double longitude) {
        Request request = new Request.Builder().url(AppConstants.WebSocketLink
                .LINK_SEND).build();
        SocketListener listener = new SocketListener(latitude, longitude);
//        if (mWs == null)
        mWs = mClient.newWebSocket(request, listener);
        listener.setActionRequest(new SocketListener.onActionRequest() {
            @Override
            public void onStart() {
                Log.e("Socket", "start");
            }

            @Override
            public void onDone(String result) {
                Log.e("Socket", "done");
                try {
                    JSONObject person = (new JSONObject(result)).getJSONObject(AppConstants
                            .KEY_PARAMS.DATA.toString());
                    mReciveUser = User.parse(person);
                    if (mReciveUser != null) {
                        if (checkCanAddItem(mReciveUser)) {
                            mListContact.add(mReciveUser);

                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            changeUiView();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                Log.e("Socket", "fail");
            }

            @Override
            public void onClose() {
                Log.e("Socket", "close");
            }
        });
    }

    private boolean checkCanAddItem(User user) {
        if (mListContact.size() == 0)
            return true;
        for (int i = 0; i < mListContact.size(); i++) {
            User check = mListContact.get(i);
            if (check.getId() == user.getId()) {
                return false;
            }
        }
        for (int i = 0; i < mListReject.size(); i++) {
            User check = mListReject.get(i);
            if (check.getId() == user.getId()) {
                return false;
            }
        }
        return true;
    }

    private void setupRecycleView() {
        SwipeController swipeController = new SwipeController(new SwipeController.onActionSwipe() {
            @Override
            public void onDelete(final int position) {
                if (isSendRequest)
                    return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListReject.add(mListContact.get(position));
                        removeItemCard(position);
                    }
                });
            }

            @Override
            public void onReject() {

            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRcyCard);
        CenterRecycleViewSnapHelper snap = new CenterRecycleViewSnapHelper();
        snap.attachToRecyclerView(mRcyCard);
    }

    private void sendRequestAddContact(User user, final int position) {
        mLlFooter.setVisibility(View.INVISIBLE);
        isSendRequest = true;
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CONTACT_ID.toString(), String.valueOf(user.getId()));
        RequestDataUtils.requestData(com.android.volley.Request.Method.POST, CardReceiveActivity.this,
                AppConstants.SERVER_PATH.ADD_CARD_CONTACT.toString(),
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mListContact.get(position).setStatus(AppConstants.STATUS_USER.DONE);
                                    mAdapter.notifyItemChanged(position);
//                                mListContact.get(position).setStatus(AppConstants.STATUS_USER.DONE);
//                                mAdapter.notifyItemChanged(position);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            removeItemCard(position);
                                        }
                                    }, 1200);
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFail(int error) {
                        Toast.makeText(CardReceiveActivity.this, getString(R.string.msg_request_error_try_again), Toast.LENGTH_SHORT).show();
                        mListContact.get(position).setStatus(AppConstants.STATUS_USER.NORMAL);
                        mAdapter.notifyItemChanged(position);
                        isSendRequest = false;
                    }
                });
    }

    private void removeItemCard(int index) {
        int size = mListContact.size();
        if (size > 0) {
            try {
                mListContact.remove(index);
                mAdapter.removeItemAtIndex(index);
                if (mListContact.size() == 0)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2);
            }
            changeUiView();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2);
        }
    }

    private void changeUiView() {
        int totalContact = mListContact.size();
        mLlFooter.setVisibility(totalContact > 0 ? View.VISIBLE : View.INVISIBLE);
        mLlLoading.setVisibility(totalContact > 0 ? View.INVISIBLE : View.VISIBLE);
        mTvTitle.setText(totalContact > 0 ? String.valueOf(totalContact) + "件の受信依頼が来ています。" : "");
        isSendRequest = false;
    }

    @Override
    protected void onDestroy() {
        if (mClient != null)
            mClient.dispatcher().executorService().shutdown();
        if (mWs != null)
            mWs.cancel();
        if (mTimer != null)
            mTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isSendRequest)
            return;
        super.onBackPressed();
    }

    /**
     * Method to verify google play services on the device
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Creating google api client object
     */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(CardReceiveActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void getLocation() {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                if (mLongitude > 0) {
//                    initReceiver();
                    getAddress();
                }
                Log.e("Location", "Values : " + mLatitude + "--" + mLongitude);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void getAddress() {
        Address locationAddress = getAddressDouble(mLatitude, mLongitude);
        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();
            String currentLocation;
            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "\n" + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "\n" + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "\n" + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "\n" + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "\n" + country;
                Log.e("Location", "Address : " + currentLocation);
            }
        }
    }

    public Address getAddressDouble(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
