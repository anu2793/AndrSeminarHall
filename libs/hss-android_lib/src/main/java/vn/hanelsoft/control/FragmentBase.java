package vn.hanelsoft.control;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by macmobiles on 3/18/2016.
 */
public abstract class FragmentBase extends Fragment {
    protected View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutId() == 0)
            throw new NullPointerException("Please register layout first");
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            inflateView();
            registerEvent();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateData();
    }

    public View findViewById(int resId) {
        return rootView.findViewById(resId);
    }

    protected abstract int getLayoutId();

    protected abstract void inflateView();

    protected abstract void registerEvent();

    protected void inflateData(){};
}
