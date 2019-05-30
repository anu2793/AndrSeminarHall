package jp.co.hiropro.seminar_hall.view.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.DeviceObject;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.CircleImageView;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

/**
 * Created by dinhdv on 01/11/2017.
 */

public class ManagerDeviceAdapter extends HFRecyclerView<DeviceObject> {
    List<DeviceObject> mList = new ArrayList<>();
    private onActionManager mAction;

    public ManagerDeviceAdapter(List<DeviceObject> data, boolean withHeader, boolean withFooter) {
        super(data, withHeader, withFooter);
        mList.addAll(data);
    }

    @Override
    protected ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {

        return new ItemViewHolder(inflater.inflate(R.layout.item_manager_device, parent, false));
    }

    @Override
    protected ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    protected ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return new FooterViewHolder(inflater.inflate(R.layout.footer_copy_right, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.bindData(getItem(position), position);
        }
    }

    class ItemViewHolder extends ViewHolder {
        @BindView(R.id.ic_icon)
        CircleImageView mImvIcon;
        @BindView(R.id.tv_title)
        TextViewApp mTvTitle;
        @BindView(R.id.tv_description)
        TextViewApp mTvDescription;
        @BindView(R.id.iv_delete)
        ImageView mImvDelete;
        @BindView(R.id.tv_my_current)
        TextViewApp mTvCurrent;
        View view;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }

        void bindData(final DeviceObject item, final int position) {
            if (item.getType() == AppConstants.TYPE_DEVICE.ANDROID) {
                mImvIcon.setImageResource(R.mipmap.ic_android);
            } else {
                mImvIcon.setImageResource(R.mipmap.ic_ios);
            }
//            mImvDelete.setVisibility(item.isCurrentDevice() ? View.GONE : View.VISIBLE);
            mTvCurrent.setVisibility(item.isCurrentDevice() ? View.VISIBLE : View.GONE);
            mTvTitle.setText(item.getName() + "(" + item.getVersion() + ")");
            mTvDescription.setText(item.getDeviceId());
            mImvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAction != null)
                        mAction.onDeleteDevice(item, position);
                }
            });
        }
    }

    class FooterViewHolder extends ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setListener(onActionManager action) {
        mAction = action;
    }

    public interface onActionManager {
        void onDeleteDevice(DeviceObject item, int position);
    }
}
