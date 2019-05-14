package vn.hanelsoft.forestpublishing.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.model.LevelItem;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 8/30/2017.
 */

public class LevelExpandAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<LevelItem> mHeaders = new ArrayList<>(); // header titles
    // child data in format of header title, child title
    private HashMap<LevelItem, ArrayList<LevelItem>> mChildrens = new HashMap<>();

    public LevelExpandAdapter(Context context, ArrayList<LevelItem> listDataHeader,
                              HashMap<LevelItem, ArrayList<LevelItem>> listChildData) {
        this.mContext = context;
        this.mHeaders = listDataHeader;
        this.mChildrens = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mChildrens.get(this.mHeaders.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final LevelItem item = (LevelItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_children_level, null);
        }
        TextViewApp tvName = (TextViewApp) convertView
                .findViewById(R.id.tv_name);
        SeekBar mSeek = (SeekBar) convertView.findViewById(R.id.level);
        mSeek.setPadding(0, 0, 0, 0);
        mSeek.setProgress(item.getProgress());
        mSeek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        tvName.setText(item.getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildrens.get(this.mHeaders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LevelItem item = (LevelItem) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_level, null);
        }
//        ImageView mImv = (ImageView) convertView.findViewById(R.id.imv_arrow);
//        mImv.animate().rotation(isExpanded ? 90 : 0).start();
//        mImv.setVisibility(getChildrenCount(groupPosition) == 0 ? View.INVISIBLE : View.VISIBLE);
        TextViewApp tvName = (TextViewApp) convertView
                .findViewById(R.id.tv_name);
        tvName.setText(item.getName());
        SeekBar seek = (SeekBar) convertView.findViewById(R.id.level);
        seek.setPadding(0, 0, 0, 0);
        seek.setProgress(item.getProgress());
        seek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return convertView;
    }
}