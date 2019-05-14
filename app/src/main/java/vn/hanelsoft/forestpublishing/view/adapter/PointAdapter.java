package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.Point;
import vn.hanelsoft.forestpublishing.util.Utils;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by DinhDV on 22/01/2018.
 */

public class PointAdapter extends HFRecyclerView<Point> {
    private Context mContext;

    public PointAdapter(List<Point> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        mContext = parent.getContext();
        return new ViewHolder(inflater.inflate(R.layout.item_point, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new PointAdapter.FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindData(getItem(position));
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextViewApp mTvPoint, mTvBonus, mTvMoney;

        public ViewHolder(View view) {
            super(view);
            mTvPoint = view.findViewById(R.id.tv_point);
            mTvMoney = view.findViewById(R.id.tv_money);
            mTvBonus = view.findViewById(R.id.tv_bonus);
        }

        void bindData(final Point item) {
            mTvPoint.setText(Utils.formatNormalPrice(item.getPoint()));
            mTvMoney.setText(String.format("%s%s", Utils.formatNormalPrice(item.getMoney()), item.getCurrency()));
            mTvBonus.setVisibility(item.getBonus() > 0 ? View.VISIBLE : View.GONE);
            mTvBonus.setText(mContext.getString(R.string.tv_discount, Utils.formatNormalPrice(item.getBonus())));
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
