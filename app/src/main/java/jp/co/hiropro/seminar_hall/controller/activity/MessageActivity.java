package jp.co.hiropro.seminar_hall.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.Friend;
import jp.co.hiropro.seminar_hall.model.Message;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.SocketService;
import jp.co.hiropro.seminar_hall.view.adapter.MessageAdapter;

public class MessageActivity extends BaseActivity {
    @BindView(R.id.list_message)
    RecyclerView recyclerView;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    @BindView(R.id.edt_message)
    EditText edtMessage;
    @BindView(R.id.imvPhoto)
    ImageView imvPhoto;
    private List<Message> messageList;
    private MessageAdapter mAdapter;
    private String message;
    private String currentTime;
    private int GALLERY = 1, CAMERA = 2;
    private Friend friend;
    private File dir;
    List<File> files;
    private List<String> extensions = Arrays.asList(new String[]{"mp3", "mp4", "wav", "png", "jpg", "jpeg"});

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        btnShop.setVisibility(View.INVISIBLE);
        btnMenu.setVisibility(View.INVISIBLE);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            friend = (Friend) bundle.getSerializable(AppConstants.KEY_SEND.KEY_DATA);
        }
        setupTitleScreen(friend.getmName());
        currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        loadData();
        mAdapter = new MessageAdapter(messageList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = edtMessage.getText().toString().trim();
                if (message.length() > 0) {
                    addMessage(Message.TYPE_MESSAGE_SENT, message, currentTime);
                    edtMessage.setText("");
                }
            }
        });

        imvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edtMessage.getText().toString().trim().length() > 0) {
                    btnSend.setColorFilter(ContextCompat.getColor(MessageActivity.this, R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    btnSend.setColorFilter(ContextCompat.getColor(MessageActivity.this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
            }
        });
    }

    private void loadData() {
        messageList = new ArrayList<>();
        messageList.add(new Message(0, "Hello", currentTime));
        messageList.add(new Message(0, "What your name", currentTime));
        messageList.add(new Message(1, "My Name Sang", currentTime));
        messageList.add(new Message(0, "Where are you from", currentTime));

        loadFiles();
    }

    private void loadFiles() {
        File parent = Environment.getExternalStorageDirectory();
        files = getListFiles(parent);
        int j = 0;
        for (int i = 0; i < files.size(); i++) {
            if (getFileExtension(files.get(i).getName()) != "" && extensions.contains(getFileExtension(files.get(i).getName()))) {
                Log.d("Files", "FileName:" + getFileExtension(files.get(i).getName()));
                j++;
            }
        }
        Log.d("Files", "FileName:" + j);

    }

    private static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        else return "";
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    private void scrollUp() {
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void addMessage(int mType, String mMessage, String mCreateTime) {
        //SocketService.getInstance().sendMessage(mMessage);
        messageList.add(new Message(mType, mMessage, mCreateTime));
        mAdapter.notifyItemInserted(messageList.size() - 1);
        scrollUp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketService.getInstance().disconnect();
    }
}
