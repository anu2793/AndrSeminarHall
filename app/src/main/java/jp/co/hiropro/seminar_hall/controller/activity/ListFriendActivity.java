package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Friend;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.view.adapter.LisFriendAdapter;

public class ListFriendActivity extends BaseActivity {
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
        setupTitleScreen("List Friend");
        btnShop.setVisibility(View.INVISIBLE);
        btnMenu.setVisibility(View.INVISIBLE);
        loadData();
        mAdapter = new LisFriendAdapter(listFriend);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rcvListFriend.setLayoutManager(layoutManager);
        rcvListFriend.setItemAnimator(new DefaultItemAnimator());
        rcvListFriend.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new LisFriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Friend friend) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstants.KEY_SEND.KEY_DATA, friend);
                Intent intent = new Intent(ListFriendActivity.this, MessageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        listFriend = new ArrayList<>();
        listFriend.add(new Friend(1, "Nguyen A", ""));
        listFriend.add(new Friend(1, "Nguyen B", ""));
        listFriend.add(new Friend(1, "Nguyen C", ""));
        listFriend.add(new Friend(1, "Nguyen D", ""));
    }
}
