package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Menu;

/**
 * Created by Tuấn Sơn on 18/7/2017.
 */

public class MenuAdapter extends ArrayAdapter<Menu> {
    Context mContext;

    public MenuAdapter(@NonNull Context context) {
        super(context, -1);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.bindData(getItem(position));
        return convertView;
    }

    private class ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;

        public ViewHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tv_menu);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        }

        void bindData(Menu item) {
            tvTitle.setText(item.getTitle());
            Glide.with(mContext).load(item.getIcon()).into(ivIcon);
        }
    }
}
