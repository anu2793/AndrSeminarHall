package vn.hanelsoft.forestpublishing.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

import butterknife.BindView;
import vn.hanelsoft.forestpublishing.ForestApplication;
import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.controller.activity.ContentDetailActivity;
import vn.hanelsoft.forestpublishing.controller.activity.RecommendFreeContentActivity;
import vn.hanelsoft.forestpublishing.model.VideoDetail;
import vn.hanelsoft.forestpublishing.util.AppConstants;
import vn.hanelsoft.forestpublishing.util.KeyParser;
import vn.hanelsoft.forestpublishing.view.CustomLoadingListItemCreator;
import vn.hanelsoft.forestpublishing.view.adapter.RecommendedContentAdapter;
import vn.hanelsoft.utils.NetworkUtils;

public class RecommendFreeContentFragment extends FragmentBase implements Paginate.Callbacks {
    @BindView(R.id.lv_free_content)
    RecyclerView lvFreeContent;
    private RecommendedContentAdapter adapter;
    private List<VideoDetail> freeVideoList = new ArrayList<>();
    private boolean isLoading;
    private int page = 1;
    private int mType = AppConstants.TYPE_SEARCH.DEFAULT;

    public RecommendFreeContentFragment() {
    }

    public static RecommendFreeContentFragment newInstance(int type) {
        RecommendFreeContentFragment fragment = new RecommendFreeContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void inflateData() {
        super.inflateData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt("type");
            initListFreeContent();
            page = 1;
            freeVideoList.clear();
            onLoadMore();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragnet_recommend_free_content;
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

    private void initListFreeContent() {
        adapter = new RecommendedContentAdapter(freeVideoList, false, true);
        lvFreeContent.setLayoutManager(new LinearLayoutManager(activity));
        lvFreeContent.setAdapter(adapter);
        DividerItemDecoration divider = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(activity, R.drawable.line_divider));
        lvFreeContent.addItemDecoration(divider);
        adapter.setOnItemClickListener(new RecommendedContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoDetail item) {
                startActivity(new Intent(activity, ContentDetailActivity.class).
                        putExtra(KeyParser.KEY.DATA.toString(), item));
            }
        });

        Paginate.with(lvFreeContent, this)
                .setLoadingTriggerThreshold(2)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(activity))
                .addLoadingListItem(true)
                .build();
    }

    private void getData() {
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        if (page == 1)
            freeVideoList.clear();
        params.put("page", page);
        params.put("sorttype", mType);
        StringRequest request = new StringRequest(NetworkUtils.formatUrl(AppConstants.SERVER_PATH.LIST_FREE_VIDEO.toString(), params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String strResponse) {
                        try {
                            JSONObject response = new JSONObject(strResponse);
                            int status = response.getInt(KeyParser.KEY.STATUS.toString());
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                JSONObject data = response.getJSONObject(KeyParser.KEY.DATA.toString());
                                String urlBanner = data.optString(KeyParser.KEY.BANNER_URL.toString());
                                ((RecommendFreeContentActivity) getActivity()).setDataHeader(urlBanner);
                                JSONArray freeList = data.getJSONArray(KeyParser.KEY.LIST.toString());
                                if (freeList.length() > 0) {
                                    for (int i = 0; i < freeList.length(); i++) {
                                        freeVideoList.add(VideoDetail.parse(freeList.getJSONObject(i)));
                                    }
                                    adapter.setHeaderView(urlBanner);
                                    adapter.notifyDataSetChanged();
                                }
                                isLoading = false;
                                page = data.getInt("next_page");
                            } else {
                                isLoading = false;
                                page = 0;
                                adapter.notifyDataSetChanged();
                            }
//                            else if (status == AppConstants.STATUS_REQUEST.SERVER_MAINTAIN) {
//                                String mess = response.optString(KeyParser.KEY.MESSAGE.toString());
//                                goMaintainScreen(activity, mess);
//                            } else if (status == AppConstants.STATUS_REQUEST.TOKEN_EXPIRED) {
//                                HSSDialog.show(activity, getString(R.string.msg_session_expire), "Ok", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        User.getInstance().logout(activity);
//                                    }
//                                });
//                            } else {
//                                HSSDialog.show(activity, getString(R.string.error_io_exception));
//                            }
                        } catch (JSONException e) {
                            isLoading = false;
                            page = 0;
                            adapter.notifyDataSetChanged();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        NetworkUtils.showDialogError(activity, error);
                    }
                });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

}
