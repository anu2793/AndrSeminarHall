package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.service.socket.SocketListener;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.RecyclerItemTouchHelper;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.ButtonApp;
import jp.co.hiropro.seminar_hall.view.KienInterface;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.ReceivedItemAdapter;

/**
 * Created by dinhdv on 1/24/2018.
 */

public class ReceiveScreenActivity extends AppCompatActivity implements KienInterface.View,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        ReceivedItemAdapter.OnItemClickListener {
    private static final String BUNDLE_LIST_PIXELS = "allPixels";
    private RecyclerView rv;
    private ReceivedItemAdapter adapter;
    private int widthScreen;
    private int allPixels = 0;
    private int flagSize = 0;
    private int destinationSize;
    private int bonusSize = 0;
    private int positionFocus;
    List<User> list;
    private OkHttpClient mClient;
    private boolean isDx;
    Handler handler;
    User useQRCode;
    @BindView(R.id.imv_down)
    ImageView imv_down;
    @BindView(R.id.tv_note)
    TextViewApp tv_note;
    @BindView(R.id.tvCount)
    TextViewApp tvCount;
    @BindView(R.id.imv_animReceived)
    ImageView imv_animReceived;
    @BindView(R.id.tv_image)
    TextViewApp tv_image;
    private boolean isClick;
    private List<User> listExitsContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_screen);
        ButterKnife.bind(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScreen = displayMetrics.widthPixels;
        bonusSize = widthScreen - (widthScreen / 8);
        initData();
        initViewData();
        initEven();
        if (useQRCode == null) {
            tv_image.setText("送信中");
            animReceived();
            listExitsContact = getIntent().getParcelableArrayListExtra(AppConstants.KEY_INTENT.LIST_CONTACT.toString());
        }
    }

    private void addUser(User user) {
        if (list.size() <= 2) {
            list.clear();
        }
        if (list.size() == 0) {
            list.add(new User());
            list.add(user);
            list.add(new User());
        } else if (list.size() > 0) {
            list.remove(list.size() - 1);
            list.add(user);
            list.add(new User());
        }
    }

    private void addSingleUser(User user) {
        if (list.size() <= 2) {
            list.clear();
        }
        if (list.size() == 0) {
            list.add(new User());
            list.add(user);
            list.add(new User());
        } else if (list.size() > 0) {
            list.remove(list.size() - 1);
            list.add(user);
            list.add(new User());
        }
        adapter.notifyDataSetChanged();
    }

    private boolean distinctUser(User user) {
        if (listExitsContact != null && listExitsContact.size() > 0) {
            for (User u :
                    listExitsContact) {
                if (u.getFullname().equals(user.getFullname())) {
                    return true;
                }
            }
        }
        for (User u :
                list) {
            if (u.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private void startAnimation() {
        okhttp3.Request request = new okhttp3.Request.Builder().url(AppConstants.WebSocketLink
                .LINK_SEND).build();
        SocketListener listener = new SocketListener(0, 0);
        WebSocket ws = mClient.newWebSocket(request, listener);
        listener.setActionRequest(new SocketListener.onActionRequest() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDone(String result) {
                try {
                    JSONObject person = (new JSONObject(result)).getJSONObject(AppConstants
                            .KEY_PARAMS.DATA.toString());
                    User user = User.parse(person);
                    if (!distinctUser(user)) {
                        addUser(user);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTextCount();
                                tvCount.setVisibility(View.VISIBLE);
                                imv_animReceived.setVisibility(View.GONE);
                                tv_image.setVisibility(View.GONE);
                                imv_down.setVisibility(View.VISIBLE);
                                tv_note.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {

            }

            @Override
            public void onClose() {

            }

        });
    }

    private void initEven() {
        ItemTouchHelper.SimpleCallback
                itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.DOWN,
                this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                synchronized (this) {
                    if (list.size() > 2 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (allPixels != flagSize)
                            if (widthScreen * list.size() < allPixels) {
                                allPixels = 0;
                            }
                        calculatePositionAndScroll();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                allPixels += dx;
            }
        });
    }


    private void calculatePositionAndScroll() {
        destinationSize = getPosition();
        scrollListToPosition(destinationSize);
    }

    private int getPosition() {
        if (list.size() <= 3) {
            flagSize = 0;
            return -allPixels;
        } else {
            for (int i = 1; i <= list.size() - 2; i++) {
                if (bonusSize * i > allPixels) {
                    positionFocus = i;
                    break;
                }
            }
            if ((positionFocus * bonusSize) - (bonusSize / 2) < allPixels) {
                flagSize = ((positionFocus) * bonusSize);
                return ((positionFocus) * bonusSize) - allPixels;
            } else {
                flagSize = ((positionFocus - 1) * bonusSize);
                return ((positionFocus - 1) * bonusSize) - allPixels;
            }
        }
    }

    private void scrollListToPosition(int expectedPosition) {
        rv.smoothScrollBy(expectedPosition, 0);
    }

    @Override
    public void initData() {
        isClick = true;
        allPixels = 0;
        handler = new Handler();
        list = new ArrayList<>();
        if (getIntent().getExtras() != null)
            useQRCode = getIntent().getExtras().getParcelable(AppConstants.KEY_SEND.KEY_DATA);
        if (useQRCode != null) {
            tv_image.setText("待機中");
            tvCount.setVisibility(View.GONE);
            animQrCode();
        }
        setTextCount();
    }

    private void animQrCode() {
        Glide.with(ReceiveScreenActivity.this).asGif().load(R.drawable.gif_waitting).into(imv_animReceived);
        Runnable csRunnable = new Runnable()//sau khi call service
        {
            @Override
            public void run() {
                imv_animReceived.setVisibility(View.GONE);
                tv_image.setVisibility(View.GONE);
                addSingleUser(useQRCode);
                imv_down.setVisibility(View.VISIBLE);
                tv_note.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(csRunnable, 2000);
    }

    private void animReceived() {
        Glide.with(ReceiveScreenActivity.this).asGif().load(R.drawable.gif_recieving).into(imv_animReceived);
        Runnable csRunnable = new Runnable()//sau khi call service
        {
            @Override
            public void run() {
                if (mClient == null)
                    mClient = new OkHttpClient();
                startAnimation();
            }
        };
        handler.postDelayed(csRunnable, 2000);
    }

    @Override
    public void initViewData() {
        Glide.with(ReceiveScreenActivity.this).asGif().load(R.drawable.arrow_up).into(imv_down);
        imv_down.setRotation(180);
        rv = findViewById(R.id.item_list);
        LinearLayoutManager shopItemslayoutManager = new LinearLayoutManager
                (getApplicationContext());
        shopItemslayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(shopItemslayoutManager);
        adapter = new ReceivedItemAdapter(list, this, this);
        rv.setAdapter(adapter);
        rv.hasFixedSize();
    }

    @Override
    public <T> void updateUI(T object) {

    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int
            position) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int statusBarHeight = getStatusBarHeight(this);
        final View view = viewHolder.itemView;
        view.animate().y(height - statusBarHeight).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                list.remove(position);
                adapter.notifyDataSetChanged();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setY(0);
                    }
                });
                if (useQRCode != null) {
                    finish();
                } else {
                    setTextCount();
                }
                checkShowAnim();
            }
        }).start();
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId > 0) ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    @Override
    public void onItemClick(int position, ButtonApp btn) {
        imv_down.setVisibility(View.GONE);
        tv_note.setVisibility(View.GONE);
        if (isClick) {
            isClick = false;
            if (useQRCode == null) {
                addCard(list.get(position).getId(), position, btn);
            } else {
                addCardQrCode(useQRCode.getId(), btn);
            }
        }
    }


    private void addCardQrCode(int id, final ButtonApp btn) {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CONTACT_ID.toString(), String.valueOf(id));//TEST
        RequestDataUtils.requestData(Request.Method.POST, ReceiveScreenActivity.this,
                AppConstants.SERVER_PATH.ADD_CARD_CONTACT.toString(),
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        btn.setText("受信完了");
                        Runnable csRunnable = new Runnable()//sau khi call service
                        {
                            @Override
                            public void run() {
                                isClick = true;
                                Intent intent = new Intent(ReceiveScreenActivity.this,
                                        TeacherProfileCardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        };
                        handler.postDelayed(csRunnable, 2000);
                    }

                    @Override
                    public void onFail(int error) {
                        isClick = true;
                    }
                });
    }

    private void addCard(int id, final int position, final ButtonApp btn) {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CONTACT_ID.toString(), String.valueOf(id));//TEST
        RequestDataUtils.requestData(Request.Method.POST, ReceiveScreenActivity.this,
                AppConstants.SERVER_PATH.ADD_CARD_CONTACT.toString(),
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        btn.setText("受信完了");
                        Runnable csRunnable = new Runnable()//sau khi call service
                        {
                            @Override
                            public void run() {
                                listExitsContact.add(list.get(position));
                                list.remove(position);
                                adapter.onItemDismiss(position);
                                isActiveClick();
                                checkShowAnim();
                            }
                        };
                        handler.postDelayed(csRunnable, 2000);
                    }

                    @Override
                    public void onFail(int error) {
                        isClick = true;
                    }
                });
    }

    private void setTextCount() {
        if (useQRCode != null)
            tvCount.setVisibility(View.GONE);
        else {
            if (list.size() <= 2) {
                tvCount.setText("0件の受信依頼が来ています。");
            } else {
                tvCount.setText(list.size() - 2 + "件の受信依頼が来ています。");
            }
        }
    }

    private void checkShowAnim() {
        if (list.size() > 2) {
            imv_down.setVisibility(View.VISIBLE);
            tv_note.setVisibility(View.VISIBLE);
            setTextCount();
        } else {
            imv_down.setVisibility(View.GONE);
            tv_note.setVisibility(View.GONE);
            imv_animReceived.setVisibility(View.VISIBLE);
            tv_image.setVisibility(View.VISIBLE);
            tvCount.setVisibility(View.GONE);
        }
    }

    private void isActiveClick() {
        Runnable csRunnable = new Runnable()//sau khi call service
        {
            @Override
            public void run() {
                isClick = true;
            }
        };
        handler.postDelayed(csRunnable, 1000);
    }

    @OnClick(R.id.imvBack)
    public void back() {
        onBackPressed();
    }
}
