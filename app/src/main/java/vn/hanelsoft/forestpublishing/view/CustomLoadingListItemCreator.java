package vn.hanelsoft.forestpublishing.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paginate.recycler.LoadingListItemCreator;

import vn.hanelsoft.forestpublishing.R;


/**
 * Created by Tuấn Sơn on 27/9/2016.
 */

public class CustomLoadingListItemCreator implements LoadingListItemCreator {
    Context mContext;

    public CustomLoadingListItemCreator(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_loading, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
        }
    }
}
