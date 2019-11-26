package jp.co.hiropro.seminar_hall.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Message;

/**
 * Created by Administrator on 09/05/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> mMessageList;

    public MessageAdapter(List<Message> messageList) {
        this.mMessageList = messageList;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.TYPE_MESSAGE_RECEIVED:
                layout = R.layout.message_item_received;
                break;
            case Message.TYPE_MESSAGE_SENT:
                layout = R.layout.message_item_send;
                break;
        }
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {
        Message currentMessage = mMessageList.get(position);
        if (currentMessage.getmType() >= 0 && currentMessage.getmMessage() != null) {
            if (currentMessage.getmType() == Message.TYPE_MESSAGE_RECEIVED) {
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(0, 10, 60, 0);
                holder.mView.setLayoutParams(textParams);
            } else {
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(60, 4, 0, 0);
                holder.mView.setLayoutParams(textParams);
                holder.mMessage.setText(currentMessage.getmMessage());
            }
            holder.mMessage.setText(currentMessage.getmMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).getmType();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView mMessage;
        public View mView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            mView = itemView;
        }
    }


}


