package jp.co.hiropro.seminar_hall.controller.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.paginate.Paginate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.controller.activity.ContentDetailActivity;
import jp.co.hiropro.seminar_hall.model.SearchResult;
import jp.co.hiropro.seminar_hall.model.VideoDetail;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.KeyParser;
import jp.co.hiropro.seminar_hall.util.RecyclerItemClickListener;
import jp.co.hiropro.seminar_hall.view.CustomLoadingListItemCreator;
import jp.co.hiropro.seminar_hall.view.TextViewApp;
import jp.co.hiropro.seminar_hall.view.adapter.HistoryAdapter;
import jp.co.hiropro.utils.NetworkUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherContentListFragment extends FragmentBase implements Paginate.Callbacks {
    boolean isLoading = false;
    int page = 1;
    @BindView(R.id.rv_teacher_content)
    RecyclerView rv_teacher_content;
    @BindView(R.id.tv_copyright)
    TextViewApp mTvCopyRight;
    @BindView(R.id.tv_copyright_bottom)
    TextViewApp mTvCopyRightBottom;
    HistoryAdapter adapter;
    List<SearchResult> teacherList = new ArrayList<>();
    int type;
    int idUser;
    private DividerItemDecoration mDivider = null;

    public TeacherContentListFragment() {
    }

    public static TeacherContentListFragment newInstance(int type, int id) {
        TeacherContentListFragment myFragment = new TeacherContentListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("idUser", id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teacher_content_list;
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        type = getArguments().getInt("type", 0);
        idUser = getArguments().getInt("idUser", 0);
        onLoadMore();
        initListTeacherContent();

        rv_teacher_content.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Get height here
                        int heightScreen = (int) (AppUtils.getScreenHigh() - (2 * getResources().getDimensionPixelOffset(R.dimen.value_height_action_bar)));
                        showCopyRight(rv_teacher_content.getHeight() < heightScreen);
                        Log.e("Height", "Values : " + rv_teacher_content.getHeight() + "-- Height Screen :" + heightScreen);
                    }
                });
    }

    private void initListTeacherContent() {
        adapter = new HistoryAdapter(teacherList, false, false);
        rv_teacher_content.setAdapter(adapter);
        rv_teacher_content.setLayoutManager(new LinearLayoutManager(activity));
        if (mDivider == null) {
            mDivider = new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL);
            mDivider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.drawable.line_divider_gray)));
            rv_teacher_content.addItemDecoration(mDivider);
        }
        Paginate.with(rv_teacher_content, TeacherContentListFragment.this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
        rv_teacher_content.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_teacher_content, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isLoading) {
                    SearchResult item = teacherList.get(rv_teacher_content.getChildAdapterPosition(view));
                    if (item != null) {
                        VideoDetail videoDetail = new VideoDetail();
                        videoDetail.setTitle(item.getTitle());
                        videoDetail.setId(item.getId());
                        startActivity(new Intent(activity, ContentDetailActivity.class)
                                .putExtra(KeyParser.KEY.DATA.toString(), videoDetail));
                    }
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
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
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("teacherid", idUser);
        params.put("sorttype", type);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.GET_CONTENT_LIST.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                JSONArray list = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        teacherList.add(SearchResult.parse(list.getJSONObject(i)));
                                    }
                                }
                                isLoading = false;
                                page = data.getInt("next_page");
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            page = 0;
                            isLoading = false;
                            adapter.notifyDataSetChanged();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkUtils.showDialogError(activity, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
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
