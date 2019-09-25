package jp.co.hiropro.seminar_hall.controller.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.hiropro.seminar_hall.ForestApplication;
import jp.co.hiropro.seminar_hall.GlideApp;
import jp.co.hiropro.seminar_hall.R;
import jp.co.hiropro.seminar_hall.model.NewsItem;
import jp.co.hiropro.seminar_hall.model.User;
import jp.co.hiropro.seminar_hall.util.AppConstants;
import jp.co.hiropro.seminar_hall.util.AppUtils;
import jp.co.hiropro.seminar_hall.util.RequestDataUtils;
import jp.co.hiropro.seminar_hall.util.Utils;
import jp.co.hiropro.seminar_hall.view.EditTextApp;
import jp.co.hiropro.utils.NetworkUtils;

public class SendNewsActivity extends BaseActivity {
    @BindView(R.id.imvThumb)
    ImageView imvThumb;
    @BindView(R.id.imvMain)
    ImageView imvMain;
    @BindView(R.id.edt_news_title)
    EditTextApp edtnewsTitle;
    @BindView(R.id.edtContent)
    EditTextApp edtContent;
    @BindView(R.id.tv_title_error)
    TextView tvTitleError;
    @BindView(R.id.tv_content_error)
    TextView tvContentError;
    @BindView(R.id.lnTitle)
    LinearLayout lnTitle;
    @BindView(R.id.lnContent)
    LinearLayout lnContent;
    private String fileThumb = "";
    private String filePathMain = "";
    private static final String IMAGE_DIRECTORY = "/seminar_hall";
    private int GALLERY = 1, CAMERA = 2;
    private Boolean isThumb = false;
    public static User user = null;
    private NewsItem mNewsObject;
    private boolean isNew = true;
    private int newsId = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setupTitleScreen(getString(R.string.name_menu_send_news));
        btnMenu.setVisibility(View.INVISIBLE);
        btnShop.setVisibility(View.GONE);
        user = User.getInstance().getCurrentUser();
        mNewsObject = getIntent().getParcelableExtra(AppConstants.KEY_SEND.KEY_SEND_NEW_OBJECT);
        if (mNewsObject != null) {
            isNew = false;
            getNewDetail(String.valueOf(mNewsObject.getId()));
            newsId = mNewsObject.getId();
        }
    }

    @OnClick({R.id.btnSendnews, R.id.lnThumb, R.id.lnImage})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btnSendnews:
                String title = edtnewsTitle.getText().toString();
                String content = edtContent.getText().toString();
                if (checkValidate(title, content)) {
                    if (isNew) {
                        processSendnews(title, content);
                    } else {
                        updateNews(title, content);
                    }
                }
                break;
            case R.id.lnThumb:
                isThumb = true;
                selectImage();
                break;
            case R.id.lnImage:
                isThumb = false;
                selectImage();
                break;
        }
    }

    private void getNewDetail(String idnews) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), user.getUserId());
        params.put(AppConstants.KEY_PARAMS.ID.toString(), idnews);
        params.put("memtype", "0");
        params.put(AppConstants.KEY_PARAMS.DEVICE_ID.toString(), AppUtils.getDeviceID(SendNewsActivity.this));
        RequestDataUtils.requestData(Request.Method.GET, SendNewsActivity.this, AppConstants.SERVER_PATH.NEW_DETAIL_TEACH.toString(),
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject response, String msg) {
                        if (response.length() > 0) {
                            try {
                                JSONObject objectNew = response.getJSONObject(AppConstants.KEY_PARAMS.DETAIL.toString());
                                NewsItem item = NewsItem.parser(objectNew);
                                edtnewsTitle.setText(item.getTitle());
                                edtContent.setText(item.getBody());
                                if (!TextUtils.isEmpty(item.getThumbNail())) {
                                    fileThumb = item.getThumbNail();
                                    GlideApp.with(SendNewsActivity.this).load(item.getThumbNail()).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            imvThumb.setImageResource(R.mipmap.imv_default);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    }).into(imvThumb);
                                }
                                if (!TextUtils.isEmpty(item.getImage())) {
                                    filePathMain = item.getImage();
                                    GlideApp.with(SendNewsActivity.this).load(item.getImage()).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            imvMain.setImageResource(R.mipmap.imv_default);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    }).into(imvMain);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dismissLoading();
                    }

                    @Override
                    public void onFail(int error) {
                        dismissLoading();
                    }
                });
    }

    private void updateNews(String title, String content) {
        showLoading();
        Log.d("LOG_THUMB", fileThumb);
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), user.getUserId());
        params.put(AppConstants.KEY_PARAMS.ID.toString(), String.valueOf(newsId));
        params.put(AppConstants.KEY_PARAMS.TITLE.toString(), title);
        params.put(AppConstants.KEY_PARAMS.BODY.toString(), content);
        params.put("thumbnail", Utils.encodeBase64(fileThumb));
        params.put("image", Utils.encodeBase64(filePathMain));
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.UPDATE_NEWS.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                Toast.makeText(SendNewsActivity.this, "成功した", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SendNewsActivity.this, ListnewsTeachActivity.class));
                            } else {
                                dismissLoading();
                            }
                        }
                        System.out.println(response);
                        dismissLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(SendNewsActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }

    private void selectImage() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                showPictureDialog();
                            }

                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .onSameThread()
                    .check();

        } else {
            showPictureDialog();
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SendNewsActivity.this);
        builder.setTitle("許可取得必要");
        builder.setMessage("このアプリには、この機能を使用するため許可が必要です。 アプリの設定で許可を付与できます。");
        builder.setPositiveButton(" 設定移動", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void processSendnews(String title, String content) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put(AppConstants.KEY_PARAMS.CLIENT_ID.toString(), String.valueOf(AppConstants.CLIENT_ID));
        params.put(AppConstants.KEY_PARAMS.MEMBER_ID.toString(), user.getUserId());
        params.put(AppConstants.KEY_PARAMS.TITLE.toString(), title);
        params.put(AppConstants.KEY_PARAMS.BODY.toString(), content);
        params.put("thumbnail", Utils.encodeBase64(fileThumb));
        params.put("image", Utils.encodeBase64(filePathMain));
        params.put("is_push", "1");
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AppConstants.SERVER_PATH.SEND_NEWS.toString(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            int status = response.optInt(AppConstants.KEY_PARAMS.STATUS.toString(), 1);
                            if (status == AppConstants.REQUEST_SUCCESS) {
                                Toast.makeText(SendNewsActivity.this, "成功した", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SendNewsActivity.this, ListnewsTeachActivity.class));
                            } else {
                                dismissLoading();
                            }
                        }
                        System.out.println(response);
                        dismissLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoading();
                NetworkUtils.showDialogError(SendNewsActivity.this, error);
            }
        });
        request.setHeaders(getAuthHeader());
        ForestApplication.getInstance().addToRequestQueue(request);
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("アクション選択");
        String[] pictureDialogItems = {
                "ギャラリーから写真を選択する",
                "カメラから写真をキャプチャする"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri resultUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    if (isThumb) {
                        fileThumb = getPathFromURI(resultUri);
                        imvThumb.setImageBitmap(bitmap);
                    } else {
                        filePathMain = getPathFromURI(resultUri);
                        imvMain.setImageBitmap(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SendNewsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imvThumb.setImageBitmap(thumbnail);
            saveImage(thumbnail);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            if (isThumb) {
                fileThumb = f.getAbsolutePath();
                Log.d("CAMERATHUMB",fileThumb);
            } else {
                filePathMain = f.getAbsolutePath();
            }

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    private boolean checkValidate(String title, String content) {
        boolean valuesTitle = checkErrorTitle(title);
        boolean valueContent = checkErrorContent(content);
        return valuesTitle && valueContent;
    }

    private boolean checkErrorTitle(String title) {
        if (title.length() == 0) {
            setErrorTitleMsg("タイトルを入力してください");
            return false;
        } else {
            setErrorTitleMsg("");
            return true;
        }
    }

    private boolean checkErrorContent(String content) {
        if (content.length() == 0) {
            setErrorContentMsg("コンテンツを入力してください");
            return false;
        } else {
            setErrorContentMsg("");
            return true;
        }
    }

    protected void setErrorTitleMsg(String msg) {
        lnTitle.setBackgroundResource(msg.length() > 0 ? R.drawable.bg_edittext_error : R
                .drawable.bg_edittext_gray_border);
        tvTitleError.setVisibility(msg.length() > 0 ? View.VISIBLE : View.GONE);
        if (msg.length() > 0) {
            tvTitleError.setText(msg);
        }
    }

    protected void setErrorContentMsg(String msg) {
        lnContent.setBackgroundResource(msg.length() > 0 ? R.drawable.bg_edittext_error : R
                .drawable.bg_edittext_gray_border);
        tvContentError.setVisibility(msg.length() > 0 ? View.VISIBLE : View.GONE);
        if (msg.length() > 0) {
            tvContentError.setText(msg);
        }
    }

}
