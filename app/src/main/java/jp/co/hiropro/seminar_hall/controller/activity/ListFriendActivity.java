package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Friend;
import jp.co.hiropro.seminar_hall.view.adapter.LisFriendAdapter;

public class ListFriendActivity extends BaseActivity implements LisFriendAdapter.OnItemClickListener {
    @BindView(R.id.rcvListFriend)
    RecyclerView rcvListFriend;
    @BindView(R.id.edtSearch)
    EditText edtSearch;
    private ArrayList<Friend> listFriend;
    private LisFriendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_friend;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        loadData();
        mAdapter = new LisFriendAdapter(listFriend);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rcvListFriend.setLayoutManager(layoutManager);
        rcvListFriend.setItemAnimator(new DefaultItemAnimator());
        rcvListFriend.setAdapter(mAdapter);
    }

    private void loadData() {
        listFriend = new ArrayList<>();
        listFriend.add(new Friend(1, "Nguyen A", ""));
        listFriend.add(new Friend(1, "Nguyen B", ""));
        listFriend.add(new Friend(1, "Nguyen C", ""));
        listFriend.add(new Friend(1, "Nguyen D", ""));
    }

    @Override
    public void onItemClick(Friend friend) {
        startActivity(new Intent(this, MessageActivity.class));
    }
}
