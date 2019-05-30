package jp.co.hiropro.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.co.hiropro.mylibrary.R;
import jp.co.hiropro.utils.IntentUtils;

//import vn.hanelsoft.utils.FileUtils;


/**
 * Created by sam on 14-8-14.
 */
public class StickerView extends View {
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 4;
    public static final int BOTTOM_RIGHT = 6;
    public float maxScale = 4f;
    public float minScale = 0.5f;

    private float[] mOriginPoints;
    private float[] mPoints;
    private float mLastPointX, mLastPointY;
    private float mControllerWidth, mControllerHeight, mDeleteWidth, mDeleteHeight, mFlipHorizontalWidth, mFlipHorizontalHeight;
    private float mStickerScaleSize = 1.0f;

    private int positionController = TOP_RIGHT;
    private int positionDelete = TOP_RIGHT;
    private int positionFlipHorizontal = BOTTOM_LEFT;


    private int positionFlipVertical = BOTTOM_RIGHT;

    private boolean isShowFlipHorizontal = true;
    private boolean isShowController = true;
    private boolean isShowDelete = false;
    private boolean mInDelete = false;
    private boolean mInController, mInMove, mInFlipHorizontal;
    private boolean mDrawController = true;


    private RectF mOriginContentRect;
    private RectF mContentRect;
    private RectF mViewRect;

    private Bitmap mBitmap;
    private Bitmap mControllerBitmap, mDeleteBitmap, mFlipHorizontalBitmap;
    private Matrix mMatrix;
    private Paint mPaint;
    private OnStickerDeleteListener mOnStickerDeleteListener;
    private ImageView overlay;

    public StickerView(Context context, ImageView overlay, Bitmap sticker) {
        super(context);
        this.overlay = overlay;
        ((ViewGroup) overlay.getParent()).addView(this, overlay.getLayoutParams());

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(Color.WHITE);

        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_control);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_delete);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();
        setSticker(sticker);
    }

    public void setSticker(Bitmap bitmap) {
        if (bitmap == null) throw new NullPointerException("Sticker not null");
        this.mBitmap = bitmap;
        mStickerScaleSize = 1.0f;
        setFocusable(true);
        try {
            float px = mBitmap.getWidth();
            float py = mBitmap.getHeight();
            mOriginPoints = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};
            mOriginContentRect = new RectF(0, 0, px, py);
            mPoints = new float[10];
            mContentRect = new RectF();

            mMatrix = new Matrix();
            mMatrix.preScale(0.5f, 0.5f, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
//            float transtLeft = (overlay.getWidth() - mBitmap.getWidth()) / 2;
//            float transtTop = (overlay.getHeight() - mBitmap.getHeight()) / 2;
//            mMatrix.postTranslate(transtLeft, transtTop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null || mMatrix == null) {
            return;
        }

        mMatrix.mapPoints(mPoints, mOriginPoints);

        mMatrix.mapRect(mContentRect, mOriginContentRect);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        if (mDrawController && isFocusable()) {
//            canvas.drawLine(mPoints[0], mPoints[1], mPoints[2], mPoints[3], mBorderPaint);
//            canvas.drawLine(mPoints[2], mPoints[3], mPoints[4], mPoints[5], mBorderPaint);
//            canvas.drawLine(mPoints[4], mPoints[5], mPoints[6], mPoints[7], mBorderPaint);
//            canvas.drawLine(mPoints[6], mPoints[7], mPoints[0], mPoints[1], mBorderPaint);
            System.out.println(positionController + ", " + positionDelete);
            if (isShowController) {
                canvas.drawBitmap(mControllerBitmap, mPoints[positionController] - mControllerWidth / 2,
                        mPoints[positionController + 1] - mControllerHeight / 2, mPaint);
            }
            if (isShowDelete) {
                canvas.drawBitmap(mDeleteBitmap, mPoints[positionDelete] - mDeleteWidth / 2,
                        mPoints[positionDelete + 1] - mDeleteHeight / 2, mPaint);
            }
            if (isShowFlipHorizontal) {
                canvas.drawBitmap(mFlipHorizontalBitmap, mPoints[positionFlipHorizontal] - mFlipHorizontalWidth / 2,
                        mPoints[positionFlipHorizontal + 1] - mFlipHorizontalHeight / 2, mPaint);
            }
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mDrawController = false;
        draw(canvas);
        mDrawController = true;
        canvas.save();
        return bitmap;
    }

    public void setShowDrawController(boolean show) {
        mDrawController = show;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInController(x, y)) {
                    mInController = true;
                    mLastPointY = y;
                    mLastPointX = x;
                    break;
                }

                if (isInDelete(x, y)) {
                    mInDelete = true;
                    break;
                }
                if (isInHorizontalVertical(x, y)) {
                    mInFlipHorizontal = true;
                    break;
                }
                if (isInHorizontalVertical(x, y)) {
                    isShowFlipHorizontal = true;
                    break;
                }
                if (mContentRect.contains(x, y)) {
                    mLastPointY = y;
                    mLastPointX = x;
                    mInMove = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isInDelete(x, y) && mInDelete) {
                    doDeleteSticker();
                }
                if (isInHorizontalVertical(x, y) && mInFlipHorizontal) {
                    mMatrix.preScale(-1.0f, 1.0f, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
                    this.setPositionDelete((positionDelete + 2) % 4);
                    this.setPositionController((positionController + 2) % 4);
                    this.setPositionFlipHorizontal((4 + (positionFlipHorizontal + 2) % 4));
                    invalidate();

                }
            case MotionEvent.ACTION_CANCEL:
                mLastPointX = 0;
                mLastPointY = 0;
                mInController = false;
                mInMove = false;
                mInDelete = false;
                mInFlipHorizontal = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInController) {
                    mMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    float nowLenght = caculateLength(mPoints[0], mPoints[1]);
                    float touchLenght = caculateLength(event.getX(), event.getY());
                    if (Math.sqrt((nowLenght - touchLenght) * (nowLenght - touchLenght)) > 0.0f) {
                        float scale = touchLenght / nowLenght;
                        float nowsc = mStickerScaleSize * scale;
                        if (nowsc >= minScale && nowsc <= maxScale) {
                            mMatrix.postScale(scale, scale, mPoints[8], mPoints[9]);
                            mStickerScaleSize = nowsc;
                        }
                    }
                    invalidate();
                    mLastPointX = x;
                    mLastPointY = y;
                    break;
                }

                if (mInMove == true) {
                    float cX = x - mLastPointX;
                    float cY = y - mLastPointY;
                    mInController = false;

                    if (Math.sqrt(cX * cX + cY * cY) > 2.0f && canStickerMove(cX, cY)) {
                        mMatrix.postTranslate(cX, cY);
                        postInvalidate();
                        mLastPointX = x;
                        mLastPointY = y;
                    }
                    break;
                }
                return true;
        }
        return true;
    }

    /**
     * {@link #capturePhoto(String, File)}
     * @return
     * @throws IOException
     */
    public String capturePhoto() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return capturePhoto(imageFileName, dir);
    }

    /**
     * @param fileName    Tên file ảnh sau khi thêm sticker lên ảnh gốc
     * @param directory   Thư mục chứa file
     * @return Bitmap chứa ảnh đã chỉnh sửa
     * @throws IOException
     */
    public String capturePhoto(String fileName, File directory) throws IOException {
        Bitmap source = ((BitmapDrawable) overlay.getDrawable()).getBitmap();
        Bitmap sticker = getBitmap();

        int[] position = getBitmapPositionInsideImageView(overlay);
        int drawableWidth = overlay.getDrawable().getIntrinsicWidth();
        int imgWidth = position[2];
        float ratio = (float) drawableWidth / imgWidth;
        int newStickerWidth = (int) (sticker.getWidth() * ratio);
        int newStickerHeight = (int) (sticker.getHeight() * ratio);
        sticker = Bitmap.createScaledBitmap(sticker, newStickerWidth, newStickerHeight, true);

        Bitmap bmCompress = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        Canvas canvas = new Canvas(bmCompress);
        canvas.drawBitmap(source, new Matrix(), null);
        canvas.drawBitmap(sticker, -position[0] * ratio, -position[1] * ratio, null);

        OutputStream os = null;
        File newImage = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                directory      /* directory */
        );
        try {
            os = new FileOutputStream(newImage);
            bmCompress.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        RotateImage(newImage,overlay);
        IntentUtils.scanMediaFile(getContext(), newImage);
        return newImage.getAbsolutePath();
    }

    /**
     * Returns the bitmap position inside an imageView.
     *
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    private int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH) / 2;
        int left = (int) (imgViewW - actW) / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

    public void doDeleteSticker() {
        mBitmap.recycle();
        mControllerBitmap.recycle();
        mDeleteBitmap.recycle();
        mBitmap = null;
        mControllerBitmap = null;
        mDeleteBitmap = null;
        invalidate();
        if (mOnStickerDeleteListener != null) {
            mOnStickerDeleteListener.onDelete();
            mOnStickerDeleteListener = null;
        }
        ((ViewGroup) getParent()).removeView(this);
    }

    private boolean canStickerMove(float cx, float cy) {
        float px = cx + mPoints[8];
        float py = cy + mPoints[9];
        if (mViewRect.contains(px, py)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInController(float x, float y) {
        float rx = mPoints[positionController];
        float ry = mPoints[positionController + 1];
        RectF rectF = new RectF(rx - mControllerWidth / 2,
                ry - mControllerHeight / 2,
                rx + mControllerWidth / 2,
                ry + mControllerHeight / 2);
        return isShowController & rectF.contains(x, y);
    }

    private boolean isInDelete(float x, float y) {
        float rx = mPoints[positionDelete];
        float ry = mPoints[positionDelete + 1];
        RectF rectF = new RectF(rx - mDeleteWidth / 2,
                ry - mDeleteHeight / 2,
                rx + mDeleteWidth / 2,
                ry + mDeleteHeight / 2);
        return isShowDelete & rectF.contains(x, y);
    }

    private boolean isInHorizontalVertical(float x, float y) {
        float rx = mPoints[positionFlipHorizontal];
        float ry = mPoints[positionFlipHorizontal + 1];
        RectF rectF = new RectF(positionFlipHorizontal - mFlipHorizontalWidth / 2,
                ry - mFlipHorizontalHeight / 2,
                rx + mFlipHorizontalWidth / 2,
                ry + mFlipHorizontalHeight / 2);
        return isShowFlipHorizontal & rectF.contains(x, y);
    }

    private float caculateLength(float x, float y) {
        float ex = x - mPoints[8];
        float ey = y - mPoints[9];
        return (float) Math.sqrt(ex * ex + ey * ey);
    }

    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - mPoints[8];
        double delta_y = y - mPoints[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public void setPositionController(int positionController) {
        this.positionController = positionController % 8 & 0b110;
    }

    public void setPositionDelete(int positionDelete) {
        this.positionDelete = positionDelete % 8 & 0b110;
    }

    public void setPositionFlipHorizontal(int positionFlipHorizontal) {
        this.positionFlipHorizontal = positionFlipHorizontal % 8 & 0b110;
    }

    public void setPositionFlipVertical(int positionFlipVertical) {
        this.positionFlipVertical = positionFlipVertical % 8 & 0b110;

    }

    public boolean isShowController() {
        return isShowController;
    }

    public void setShowController(boolean isShowController) {
        this.isShowController = isShowController;
        invalidate();
    }

    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        invalidate();
    }

    public interface OnStickerDeleteListener {
        public void onDelete();
    }

    public void setOnStickerDeleteListener(OnStickerDeleteListener listener) {
        mOnStickerDeleteListener = listener;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    private Bitmap RotateImage(File file, ImageView imgView) {

        Bitmap bm = decodeBitmap(file.getAbsolutePath(), imgView);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        bm = bm.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//        Bitmap bmBackground = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        IntentUtils.scanMediaFile(getContext(), file);
        return bm;
    }

    public Bitmap decodeBitmap(String mCurrentPhotoPath, ImageView imgView) {
        // Get the dimensions of the View


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / imgView.getWidth(), photoH / imgView.getHeight());

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return bitmap;
    }
}
