package jp.co.hiropro.seminar_hall.controller.fragment;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android.volley.Request;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.TeacherProfileCardActivity;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.TeacherGridAdapter;

/**
 * Created by dinhdv on 1/26/2018.
 */

public class TeacherListFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.rcy_teacher)
    RecyclerView mRcyTeacher;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    private DividerItemDecoration mDivider, mHorizontalDecoration = null;
    int mType = 0;
    private int page = 1;
    private boolean isLoading;
    private TeacherGridAdapter mAdapter;
    private ArrayList<User> mListUser = new ArrayList<>();

    public TeacherListFragment() {
    }

    public static TeacherListFragment newInstance(int type) {
        TeacherListFragment fragment = new TeacherListFragment();
        fragment.mType = type;
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teacher_list;
    }

    @Override
    protected void inflateView() {
        super.inflateView();

    }

    @Override
    protected void inflateData() {
        super.inflateData();
        initList();
        Paginate.with(mRcyTeacher, this)
                .setLoadingTriggerThreshold(0)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
    }

    private void initList() {
        mAdapter = new TeacherGridAdapter(mListUser, false, false, getActivity());
        mRcyTeacher.setAdapter(mAdapter);
        mRcyTeacher.setLayoutManager(new GridLayoutManager(activity, 2));
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.line_divider_horizontal)));
            mRcyTeacher.addItemDecoration(mDivider);
        }
        if (mHorizontalDecoration == null) {
            mHorizontalDecoration = new DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL);
            mHorizontalDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.line_divider_horizontal)));
            mRcyTeacher.addItemDecoration(mHorizontalDecoration);
        }
        mRcyTeacher.addOnItemTouchListener(new RecyclerItemClickListener(activity, mRcyTeacher, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User teacher = mListUser.get(position);
                if (teacher != null)
                    startActivity(new Intent(getActivity(), TeacherProfileCardActivity.class).putExtra(AppConstants.KEY_SEND.KEY_DATA, teacher));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mRcyTeacher.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        // height screen - action height - 2 tab height.
                        int heightScreen = (int) (AppUtils.getScreenHigh() - (4 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar)));
                        showCopyRight(mRcyTeacher.getHeight() < heightScreen);
                    }
                });
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
        final HashMap<String, String> params = new HashMap<>();
        Log.e("Params", "Values : " + mType);
        params.put(AppConstants.KEY_PARAMS.SORT_TYPE.toString(), String.valueOf(mType));
        params.put(AppConstants.KEY_PARAMS.PAGE.toString(), String.valueOf(page));
        RequestDataUtils.requestDataWithAuthen(Request.Method.GET, getActivity(), AppConstants.SERVER_PATH.SHOP_TEACHER_LIST.toString(), params, new RequestDataUtils.onResult() {
            @Override
            public void onSuccess(JSONObject object, String msg) {
                if (page == 1)
                    mListUser.clear();
                JSONArray arraySeminar = object.optJSONArray(AppConstants.KEY_PARAMS.LIST.toString());
                if (arraySeminar.length() > 0) {
                    for (int i = 0; i < arraySeminar.length(); i++) {
                        try {
                            User cate = User.parse(arraySeminar.getJSONObject(i));
                            mListUser.add(cate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                page = object.optInt(AppConstants.KEY_PARAMS.NEXT_PAGE.toString(), 0);
                isLoading = false;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(int error) {
                isLoading = false;
                page = 0;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Show copy right in bottom with 2 case :
     * 1 >  empty list.
     * 2 > item not full screen.
     */
    private void showCopyRight(boolean showBottom) {
        mTvCopyRightBottom.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        mTvCopyRight.setVisibility(showBottom ? View.GONE : View.VISIBLE);
    }
}
