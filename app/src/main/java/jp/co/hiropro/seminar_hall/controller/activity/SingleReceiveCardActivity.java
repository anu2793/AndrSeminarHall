package jp.co.hiropro.seminar_hall.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.dialog.HSSDialog;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.SwipeController;
import jp.co.hiropro.seminar_hall.view.adapter.CardContactAdapter;

/**
 * Created by dinhdv on 2/5/2018.
 */

public class SingleReceiveCardActivity extends FragmentActivity {
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
    private CardContactAdapter mAdapter;
    private SwipeController swipeController = null;
    private ArrayList<User> mListContact;
    private User mReciveUser = null;
    private boolean isSendRequest = false;
    private String mIdUser = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_receive_card);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIdUser = bundle.getString(AppConstants.KEY_SEND.KEY_DATA);
        }
        initView();
        setupRecycleView();
        getDataCard(mIdUser);
    }


    @OnClick({R.id.imv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_back:
                onBackPressed();
                break;
        }
    }

    private void initView() {
        Glide.with(SingleReceiveCardActivity.this).asGif().load(R.drawable.arrow_up).into(mImvDown);
        Glide.with(SingleReceiveCardActivity.this).asGif().load(R.drawable.gif_recieving).into(mImvLoading);
        mListContact = new ArrayList<>();
        int actionBarHeight = 1;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.value_25dp);
        int paddingBottom = getResources().getDimensionPixelSize(R.dimen.value_90dp);
        int heightOfRecycleView = AppUtils.getScreenHigh() - paddingTop - paddingBottom - actionBarHeight;
        mAdapter = new CardContactAdapter(SingleReceiveCardActivity.this, mListContact, heightOfRecycleView, new CardContactAdapter.onActionSendRequest() {
            @Override
            public void onSend(User user, int position) {
                mListContact.get(position).setStatus(AppConstants.STATUS_USER.REQUESTING);
                mAdapter.notifyItemChanged(position);
                sendRequestAddContact(user, position);
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRcyCard.setLayoutManager(lm);
        mRcyCard.setHasFixedSize(true);
        mRcyCard.setAdapter(mAdapter);
    }

    private void setupRecycleView() {
        swipeController = new SwipeController(new SwipeController.onActionSwipe() {
            @Override
            public void onDelete(final int position) {
                if (isSendRequest)
                    return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
    }

    private void sendRequestAddContact(User user, final int position) {
        mLlFooter.setVisibility(View.INVISIBLE);
        isSendRequest = true;
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CONTACT_ID.toString(), String.valueOf(user.getId()));
        RequestDataUtils.requestData(com.android.volley.Request.Method.POST, SingleReceiveCardActivity.this,
                AppConstants.SERVER_PATH.ADD_CARD_CONTACT.toString(),
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListContact.get(position).setStatus(AppConstants.STATUS_USER.DONE);
                                mAdapter.notifyItemChanged(position);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        removeItemCard(position);
                                        isSendRequest = false;
                                    }
                                }, 2000);
                            }
                        });
                    }

                    @Override
                    public void onFail(int error) {
                        Toast.makeText(SingleReceiveCardActivity.this, getString(R.string.msg_request_error_try_again), Toast.LENGTH_SHORT).show();
                        mListContact.get(position).setStatus(AppConstants.STATUS_USER.NORMAL);
                        mAdapter.notifyItemChanged(position);
                        isSendRequest = false;
                    }
                });
    }

    private void removeItemCard(int index) {
        if (mListContact.size() == 0)
            return;
        mListContact.remove(index);
        mAdapter.notifyItemRemoved(index);
//        changeUiView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }, 300);
    }

    private void changeUiView() {
        int totalContact = mListContact.size();
        mLlFooter.setVisibility(totalContact > 0 ? View.VISIBLE : View.INVISIBLE);
        mLlLoading.setVisibility(totalContact > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isSendRequest)
            return;
        super.onBackPressed();
    }

    private void getDataCard(String contents) {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.ID.toString(), contents);
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, SingleReceiveCardActivity.this, AppConstants.SERVER_PATH.GET_CARD_QRCODE.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                JSONObject info = object.optJSONObject(AppConstants.KEY_PARAMS.INFO.toString());
                if (info.length() > 0) {
                    mReciveUser = User.parse(info);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mReciveUser.getId() == User.getInstance().getId()) {
                                HSSDialog.show(SingleReceiveCardActivity.this, "自分のパーソナルカードを追加する事ができません。", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        HSSDialog.dismissDialog();
                                        finish();
                                    }
                                }).show();
                            } else {
                                mListContact.add(mReciveUser);
                                mAdapter.notifyDataSetChanged();
                                changeUiView();
                            }
                        }
                    }, 1500);
                } else {
                    HSSDialog.show(SingleReceiveCardActivity.this, "このQRコードは無効です。もう一度やり直してください。", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HSSDialog.dismissDialog();
                            finish();
                        }
                    }).show();
                }
            }

            @Override
            public void onFail(int error) {
                if (error == AppConstants.STATUS_REQUEST.CONTACT_HAS_EXISTS) {
                    HSSDialog.show(SingleReceiveCardActivity.this, getString(R.string.msg_error_contact_has_exits_in_list), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HSSDialog.dismissDialog();
                            finish();
                        }
                    }).show();
                }
            }
        });
    }
}
