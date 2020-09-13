package jp.co.hiropro.seminar_hall.controller.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.controller.activity.BaseActivity;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.HSSPreference;

/**
 * Created by Tuấn Sơn on 19/7/2017.
 */

public abstract class FragmentBase extends Fragment {
    protected BaseActivity activity;
    protected View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutId() == 0)
            throw new NullPointerException("Please register layout first");
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, mRootView);
            inflateView();
//            registerEvent();
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateData();
    }

    protected abstract int getLayoutId();

    protected void inflateView() {
        ButterKnife.bind(this, mRootView);
    }

    //    protected abstract void registerEvent();
    protected HashMap<String, String> getAuthHeader() {
        HashMap<String, String> header = new HashMap<>();
        String auth = HSSPreference.getInstance().getString(AppConstants.KEY_PREFERENCE.AUTH_TOKEN.toString(), "");
        header.put("Authorization", auth);
        return header;
    }

    protected void inflateData() {
    }
}
