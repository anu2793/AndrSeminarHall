package vn.hanelsoft.forestpublishing.controller.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.hanelsoft.dialog.HSSDialog;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.DeviceObject;
import vn.hanelsoft.forestpublishing.model.User;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.view.adapter.ManagerDeviceAdapter;
import vn.hanelsoft.utils.NetworkUtils;

/**
 * Created by dinhdv on 11/1/2017.
 */

public class ManagerDeviceActivity extends BaseActivity implements Paginate.Callbacks {
    @BindView(R.id.lv_manager)
    RecyclerView mRcyManager;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mRefreshView;

    private ArrayList<DeviceObject> mListDevice = new ArrayList<>();
    private ManagerDeviceAdapter mAdapter;
    private boolean isLoading = true;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.title_screen_manager_device));
        btnBack.setVisibility(View.GONE);
        mAdapter = new ManagerDeviceAdapter(mListDevice, false, true);
        mRcyManager.setLayoutManager(new LinearLayoutManager(activity));
        DividerItemDecoration divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        mRcyManager.addItemDecoration(divider);
        mRcyManager.setAdapter(mAdapter);
        onLoadMore();
        mAdapter.setListener(new ManagerDeviceAdapter.onActionManager() {
            @Override
            public void onDeleteDevice(final DeviceObject item, final int position) {
                HSSDialog.show(ManagerDeviceActivity.this, getString(R.string.msg_delete_device), getString(R.string.txt_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HSSDialog.dismissDialog();
                        deleteDevice(item, position);
                    }
                }, getString(R.string.txt_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HSSDialog.dismissDialog();
                    }
                });
            }
        });

        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListDevice.clear();
                page = 1;
                getData();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager_device;
    }

    @Override
    public void onLoadMore() {
        getData();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return page == 0;
    }

    private void getData() {
        isLoading = true;
        final StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.LIST_DEVICE.toString(), new HashMap<String, Object>()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        isLoading = false;
                        mRefreshView.setRefreshing(false);
                        page = 0;
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONArray arrayData = response.getJSONArray(AppConstants.KEY_PARAMS.DATA.toString());
                                for (int i = 0; i < arrayData.length(); i++) {
                                    mListDevice.add(DeviceObject.parserData(arrayData.getJSONObject(i)));
                                }
                                mAdapter.notifyDataSetChanged();
                            } else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
                                goMaintainScreen(activity, mess);
                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
                               sessionExpire();
                            } else {
                                HSSDialog.show(activity, getString(R.string.error_io_exception), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(strResponse);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                page = 0;
                isLoading = false;
                mRefreshView.setRefreshing(false);
                NetworkUtils.showDialogError(activity, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    public void deleteDevice(final DeviceObject device, final int position) {
        showLoading();
        Map<String, Integer> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), device.getId());
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.REMOVE_DEVICE.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                Toast.makeText(ManagerDeviceActivity.this, getString(R.string.msg_delete_device_success), Toast.LENGTH_SHORT).show();
                                mListDevice.remove(position);
                                mAdapter.notifyItemRemoved(position);
                                if (device.isCurrentDevice()) {
                                    User.getInstance().logout(activity);
                                }
                            } else {
                                dismissLoading();
                                HSSDialog.show(ManagerDeviceActivity.this, getString(R.string.msg_request_error_try_again));
                            }
                        }
                        dismissLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(ManagerDeviceActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }
}
