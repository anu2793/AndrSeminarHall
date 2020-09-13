package jp.co.hiropro.seminar_hall.view.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.SpeedObject;
import jp.co.hiropro.seminar_hall.view.TextViewApp;

public class SpeedAdapter extends RecyclerView.Adapter<SpeedAdapter.ViewHolder> {
    private Context mContext;
    private int INDEX_SELECT = 2;
    private ArrayList<SpeedObject> mListSpeed;

    public SpeedAdapter(Context context, ArrayList<SpeedObject> mListSpeed) {
        this.mContext = context;
        this.mListSpeed = mListSpeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drop_speed, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mListSpeed.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mListSpeed.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextViewApp mTvName;
        ImageView mImvCheck;
        LinearLayout mLlMain;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImvCheck = itemView.findViewById(R.id.imv_check);
            mLlMain = itemView.findViewById(R.id.linearspeed);
        }

        public void bindData(SpeedObject speed, int position) {
            mImvCheck.setVisibility(INDEX_SELECT == position ? View.VISIBLE : View.INVISIBLE);
//            mLlMain.setBackgroundColor(INDEX_SELECT == position ? Color.parseColor("#303030") : Color.parseColor("#191919"));
            mTvName.setText(speed.getName());
        }
    }

    public void setIndexChoice(int position) {
        INDEX_SELECT = position;
        notifyDataSetChanged();
    }


}
