package jp.co.hiropro.seminar_hall.controller.activity;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.service.socket.EchoWebSocketListener;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.CardAdapter;
import jp.co.hiropro.seminar_hall.view.flingswitch.SwipeCenterFlingAdapterView;

/**
 * Created by dinhdv on 2/5/2018.
 */

public class SwipeCardActivity extends FragmentActivity implements SwipeCenterFlingAdapterView.onFlingListener,
        SwipeCenterFlingAdapterView.OnItemClickListener, ConnectionCallbacks,
        OnConnectionFailedListener, OnRequestPermissionsResultCallback {
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    @BindView(R.id.swipe_view)
    SwipeCenterFlingAdapterView mSwipeView;
    @BindView(R.id.rl_loading)
    LinearLayout mRlLoading;
    @BindView(R.id.tv_progress)
    TextViewApp mTvProgress;
    @BindView(R.id.imv_animation)
    ImageView mImvAnimation;
    @BindView(R.id.tv_description)
    TextViewApp mTvDescription;
    @BindView(R.id.imv_loading_sending)
    ImageView mImvLoading;
    @BindView(R.id.btn_back)
    ImageView mImvBack;
    @BindView(R.id.imv_mobile)
    ImageView mImvMobile;
    private ArrayList<User> card = new ArrayList<>();
    private CardAdapter mAdapter;
    private User mUser;
    //    private TranslateAnimation mAnimation;
    private OkHttpClient mClient;
    private String TAG = SwipeCardActivity.class.getName();
    private Handler mOneHandler, mTwoHandler, mThreeHandler;
    private Runnable mRunOne, mRunTwo, mRunThree;
    private Location mLastLocation = null;
    private GoogleApiClient mGoogleApiClient;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_card);
        ButterKnife.bind(this);
        mUser = User.getInstance().getCurrentUser();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        card.add(mUser);
        mSwipeView.setIsNeedSwipe(true);
        mSwipeView.setFlingListener(this);
        mSwipeView.setOnItemClickListener(this);
        mAdapter = new CardAdapter(SwipeCardActivity.this, card, false);
        mSwipeView.setAdapter(mAdapter);
        mSwipeView.setSwipeUpToDelete(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLatitude = bundle.getDouble(AppConstants.KEY_INTENT.LATITUDE.toString(), 0);
            mLongitude = bundle.getDouble(AppConstants.KEY_INTENT.LONGITUDE.toString(), 0);
        }
        mAdapter.setListener(new CardAdapter.onActionSendRequest() {
            @Override
            public void onSend(int position) {

            }
        });

        Glide.with(SwipeCardActivity.this).asGif().load(R.drawable.swipe_action_cr).into(mImvAnimation);
        // Config websocket.
        if (mClient == null)
            mClient = new OkHttpClient();
        mOneHandler = new Handler();
        mTwoHandler = new Handler();
        mThreeHandler = new Handler();
        mRunOne = new Runnable() {
            @Override
            public void run() {
                showLoadingByStep(1);
                mTwoHandler.postDelayed(mRunTwo, 1500);
            }
        };
        mRunTwo = new Runnable() {
            @Override
            public void run() {
                showLoadingByStep(2);
                mThreeHandler.postDelayed(mRunThree, 2000);
            }
        };
        mRunThree = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SwipeCardActivity.this, TeacherProfileCardActivity.class));
                finish();
            }
        };

        // check availability of play services
//        if (checkPlayServices()) {
        // Building the GoogleApi client
//            buildGoogleApiClient();
//        }
    }

    private void getLocationCurrentRealTime(final User user) {
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
                                Log.e("Location", "VAlues :" + "Long : " + location.getLongitude() + "--lat :" + location.getLatitude());
                                mLongitude = location.getLongitude();
                                mLatitude = location.getLatitude();
                            }

                        }
                        startAnimation(user);
                    }
                });
    }


    private void startAnimation(final User user) {
        Request request = new Request.Builder().url(AppConstants.WebSocketLink.LINK_SEND).build();
        EchoWebSocketListener listener = new EchoWebSocketListener(user, mLatitude, mLongitude);
        WebSocket ws = mClient.newWebSocket(request, listener);
        listener.setActionRequest(new EchoWebSocketListener.onActionRequest() {
            @Override
            public void onStart() {
                Log.e(TAG, "START");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoadingProgress();
                    }
                });
            }

            @Override
            public void onDone(String result) {
                Log.e(TAG, "DONE : " + result);
            }

            @Override
            public void onFail() {
                Log.e(TAG, "FAIL");
            }

            @Override
            public void onClose() {
                Log.e(TAG, "CLOSE");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        if (mClient != null)
            mClient.dispatcher().executorService().shutdown();
        mOneHandler.removeCallbacks(mRunOne);
        mTwoHandler.removeCallbacks(mRunTwo);
        mThreeHandler.removeCallbacks(mRunThree);
        super.onDestroy();
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
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

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
                            status.startResolutionForResult(SwipeCardActivity.this, REQUEST_CHECK_SETTINGS);

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
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
//                mLatitude = mLastLocation.getLatitude();
//                mLongitude = mLastLocation.getLongitude();
                if (mLongitude > 0)
                    getAddress();
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
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
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


    private void showLoadingProgress() {
        showLoadingByStep(0);
//        startHiddenAnimation();
        mImvAnimation.setVisibility(View.INVISIBLE);
        mTvDescription.setVisibility(View.INVISIBLE);
        mRlLoading.setVisibility(View.VISIBLE);
        mOneHandler.postDelayed(mRunOne, 1500);
    }

    private void startHiddenAnimation() {
        TranslateAnimation animation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.ABSOLUTE, -200f);
        animation.setDuration(100);
        animation.setRepeatCount(0);
        mImvAnimation.startAnimation(animation);
    }

    @OnClick({R.id.btn_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClicked(MotionEvent event, View v, Object dataObject) {

    }

    @Override
    public void removeFirstObjectInAdapter() {
        mAdapter.remove(0);
    }

    @Override
    public void onLeftCardExit(Object dataObject) {

    }

    @Override
    public void onRightCardExit(Object dataObject) {

    }

    @Override
    public void onDeleteCard(Object dataObject) {
        if (mLatitude > 0) {
            getLocationCurrentRealTime((User) dataObject);
        }
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {

    }

    @Override
    public void onScroll(float progress, float scrollXProgress) {

    }

    /**
     * Create loading animation by step.
     *
     * @param step
     */
    public void showLoadingByStep(int step) {
        switch (step) {
            ///Start call service.
            case 0:
                mTvProgress.setText("待機中");
                Glide.with(getApplicationContext()).asGif().load(R.drawable.waiting_cr).into(mImvMobile);
//                mImvMobile.setImageResource(R.mipmap.ic_watting);
                break;
            // Send service.
            case 1:
                mTvProgress.setText("送信中");
                Glide.with(getApplicationContext()).asGif().load(R.drawable.waiting_cr).into(mImvMobile);
//                mImvMobile.setImageResource(R.mipmap.ic_sending);
                break;
            // Complete.
            case 2:
                mTvProgress.setText("送信完了");
                Glide.with(getApplicationContext()).asGif().load(R.drawable.complete_cr).into(mImvMobile);
//                mImvMobile.setImageResource(R.mipmap.ic_complete);
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
