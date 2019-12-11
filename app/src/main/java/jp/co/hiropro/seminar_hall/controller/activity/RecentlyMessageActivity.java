package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.db.AppDatabase;
import jp.co.hiropro.seminar_hall.db.RecentlyMessage;
import jp.co.hiropro.seminar_hall.view.adapter.RecentyleMessageAdapter;

public class RecentlyMessageActivity extends BaseActivity {
    @BindView(R.id.rcvListRecentlyMessage)
    RecyclerView rcvMessage;
    private String currentTime;
    private List<RecentlyMessage> listRecentlyMessage = new ArrayList<>();
    private RecentyleMessageAdapter adapter;
    private AppDatabase mDb;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recently_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mDb = AppDatabase.getInMemoryDatabase(getApplicationContext());
        setupTitleScreen("Recenty Message");
        btnShop.setVisibility(View.INVISIBLE);
        btnMenu.setImageResource(R.drawable.ic_plus);
        currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecentlyMessageActivity.this, ListFriendActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        listRecentlyMessage = mDb.recentlyMessageDao().findAllRecentlyMessageSync();
        adapter = new RecentyleMessageAdapter(listRecentlyMessage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rcvMessage.setLayoutManager(layoutManager);
        rcvMessage.setItemAnimator(new DefaultItemAnimator());
        rcvMessage.setAdapter(adapter);
    }

}
