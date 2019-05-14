package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import java.util.List;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.LevelItem;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 7/27/2017.
 */

public class LevelAdapter extends ArrayAdapter<LevelItem> {

    public LevelAdapter(@NonNull Context context, @NonNull List<LevelItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        viewHolder holder;
        LevelItem item = getItem(position);
        if (convertView == null) {
            holder = new viewHolder();
            convertView = View.inflate(getContext(), R.layout.item_level, null);
            holder.mTvName = (TextViewApp) convertView.findViewById(R.id.tv_name);
            holder.mSeekLevel = (SeekBar) convertView.findViewById(R.id.level);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        if (item != null) {
            holder.mTvName.setText(item.getName());
            holder.mSeekLevel.setProgress(item.getProgress());
            holder.mSeekLevel.setPadding(0, 0, 0, 0);
        }
        holder.mSeekLevel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return convertView;
    }

    class viewHolder {
        SeekBar mSeekLevel;
        TextViewApp mTvName;
    }
}
