package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.dialog.LoadingDialog;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ContactAdapter;

public class ContactListActivity extends BaseActivity implements ContactAdapter.ItemClickListener {
    @BindView(R.id.ryvContactList)
    RecyclerView rvContactList;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    ContactAdapter adapter;
    List<User> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen("パーソナルカードリスト");
        initViewData();
        sendRequestData(AppConstants.SERVER_PATH.GET_CONTACT_LIST.toString());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_list;
    }

    public void initViewData() {
        listData = new ArrayList<>();
        adapter = new ContactAdapter(listData, this);
        adapter.setClickListener(this);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(thisActivity()));

        rvContactList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int heightScreen = (int) (AppUtils.getScreenHigh() - getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar));
                        showCopyRight(rvContactList.getHeight() < heightScreen);
                        Log.e("Height", "Values : " + rvContactList.getHeight() + "-- Height Screen :" + heightScreen);
                    }
                });
    }

    public Activity thisActivity() {
        return ContactListActivity.this;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(thisActivity(), TeacherProfileCardActivity.class);
        intent.putExtra(AppConstants.KEY_SEND.KEY_DATA, listData.get(position));
        startActivity(intent);
    }

    @Override
    public void onRemoveClick(int position) {
        try {
            sendRemoveRequestData(AppConstants.SERVER_PATH.REMOVE_CONTACT_LIST.toString(), position, Integer.valueOf(listData.get(position).getUserId()));
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    private void sendRemoveRequestData(String url, final int position, int id) {
        LoadingDialog.getDialog(ContactListActivity.this).show();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), String.valueOf(id));//TEST
        RequestDataUtils.requestData(Request.Method.POST, ContactListActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        listData.remove(position);
                        adapter.notificationItemRemove(position);
                        LoadingDialog.getDialog(ContactListActivity.this).dismiss();
                        //sendRequestData(AppConstants.SERVER_PATH.GET_CONTACT_LIST.toString());
                    }

                    @Override
                    public void onFail(int error) {
                        LoadingDialog.getDialog(ContactListActivity.this).dismiss();
                    }
                });
    }

    private void sendRequestData(String url) {
        LoadingDialog.getDialog(this).show();
        Map<String, String> params = new HashMap<>();
        RequestDataUtils.requestData(Request.Method.GET, ContactListActivity.this, url,
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        try {
                            JSONArray array = object.getJSONArray(AppConstants.KEY_PARAMS.CONTACT_LIST.toString());
                            if (array.length() <= 0) {
                                rvContactList.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jObj = array.getJSONObject(i);
                                    User contact = User.parseContact(jObj);
                                    if (contact != null) {
                                        listData.add(contact);
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvContactList.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LoadingDialog.getDialog(ContactListActivity.this).dismiss();
                    }

                    @Override
                    public void onFail(int error) {
                        LoadingDialog.getDialog(ContactListActivity.this).dismiss();
                    }
                });
    }

    /**
     * Show copy right in bottom with 2 case :
     * 1 >  empty list.
     * 2 > item not full screen.
     *
     * @param showBottom
     */
    private void showCopyRight(boolean showBottom) {
        mTvCopyRightBottom.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        mTvCopyRight.setVisibility(showBottom ? View.GONE : View.VISIBLE);
    }
}
