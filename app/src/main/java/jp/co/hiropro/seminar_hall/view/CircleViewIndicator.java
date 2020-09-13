package jp.co.hiropro.seminar_hall.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tuấn Sơn on 19/10/2015.
 */
public class CircleViewIndicator extends View {
    private float radius = 7.0f;
    private float distanceBetweenCircle = 45.0f;

    private int mNumOfViews;
    private int mPosition;
    private ViewPager mViewPager;
    private Paint paint;
    private boolean isLast;

    public CircleViewIndicator(Context context) {
        this(context, null);
    }

    public CircleViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setDistanceBetweenCircle(float distance) {
        this.distanceBetweenCircle = distance;
    }

    public void setPosition(final int position) {
        if (position < mNumOfViews) {
            mPosition = position;
            if (mViewPager != null) {
                mViewPager.setCurrentItem(mPosition);
            }
            invalidate();
        }
    }

    public void setViewPager(final AutoScrollViewPager viewPager) {
        mViewPager = viewPager;
        updateNumOfViews();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//                    if (isLast) {
//                        isLast = false;
//                        viewPager.setCurrentItem(0);
//                        setPosition(0);
//                    }
//                    Log.e("status", "user change");
//                }
//                isLast = (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1);
            }

            @Override
            public void onPageScrolled(int position, float positionOffest, int positionOffestPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateNumOfViews();
                setPosition(position);
            }
        });
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cxLeft = (getWidth() - (mNumOfViews - 1) * distanceBetweenCircle) / 2;
        float cx;
        float cy = getHeight() / 2.0f;

        for (int i = 0; i < mNumOfViews; i++) {
            cx = cxLeft + i * distanceBetweenCircle;
            if (mPosition == i) {
                paint.setColor(Color.parseColor("#404040"));
                paint.setStyle(Paint.Style.FILL);
            } else {
                paint.setColor(Color.parseColor("#e4e4e4"));
                paint.setStyle(Paint.Style.FILL);
            }
            canvas.drawCircle(cx, cy, radius, paint);
//            canvas.drawRect(cx, cy, cx + cy, cy + cx, paint);
        }
    }

    public void updateNumOfViews() {
        if (mViewPager.getAdapter() == null) {
            mNumOfViews = 0;
        } else {
            mNumOfViews = mViewPager.getAdapter().getCount();
        }
    }
}
