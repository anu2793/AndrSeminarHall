package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.RecentlyMessage;

public class RecentlyMessageActivity extends BaseActivity {
    private String currentTime;
    private List<RecentlyMessage> recentlyMessageList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recently_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
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


    }

}
