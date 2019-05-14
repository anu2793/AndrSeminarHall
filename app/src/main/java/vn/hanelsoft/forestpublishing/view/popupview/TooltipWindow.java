package vn.hanelsoft.forestpublishing.view.popupview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import vn.hanelsoft.forestpublishing.R;
import vn.hanelsoft.forestpublishing.view.TextViewApp;

/**
 * Created by dinhdv on 1/30/2018.
 */

public class TooltipWindow {

    private static final int MSG_DISMISS_TOOLTIP = 100;
    private Context ctx;
    private PopupWindow tipWindow;
    private View contentView;
    private LayoutInflater inflater;
    private RelativeLayout mRlQrCode, mRlReceive, mRlSwipe, mRlQrScan;
    private onAction mAction;
    private View mImvArrowLeft, mImvArrowRight;
    private LinearLayout mLlTeacher, mLlScan;
    private RelativeLayout mRlTop;
    private TextViewApp mTvLeft, mTvRight;

    public TooltipWindow(final Context ctx, boolean isShowSwipe) {
        this.ctx = ctx;
        tipWindow = new PopupWindow(ctx);
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.layout_choice_type_share, null);
        mRlQrCode = contentView.findViewById(R.id.rl_qr_code);
        mRlReceive = contentView.findViewById(R.id.rl_receive);
        mRlSwipe = contentView.findViewById(R.id.rl_swipe_screen);
        mImvArrowLeft = contentView.findViewById(R.id.arrow_left);
        mImvArrowRight = contentView.findViewById(R.id.arrow_right);
        mLlTeacher = contentView.findViewById(R.id.ll_teacher);
        mRlQrScan = contentView.findViewById(R.id.rl_qr_code_scan);
        mLlScan = contentView.findViewById(R.id.ll_scan);
        mRlTop = contentView.findViewById(R.id.rl_top);
        mTvLeft = contentView.findViewById(R.id.tv_choice_type_share);
        mTvRight = contentView.findViewById(R.id.tv_screen_receive);

        mRlQrCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRlQrCode.setBackground(ctx.getDrawable(R.drawable.tooltip_bg_press));
                        mImvArrowLeft.setBackground(ctx.getDrawable(R.drawable.nav_up_press));
                        break;
                    case MotionEvent.ACTION_UP:
                        mRlQrCode.setBackground(ctx.getDrawable(R.drawable.tooltip_bg));
                        mImvArrowLeft.setBackground(ctx.getDrawable(R.drawable.nav_up));
                        if (mAction != null)
                            mAction.onQrCodeChoice();
                        dismissTooltip();
                        break;
                }
                return true;
            }
        });

        mRlReceive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRlReceive.setBackground(ctx.getDrawable(R.drawable.tooltip_bg_press));
                        mImvArrowRight.setBackground(ctx.getDrawable(R.drawable.nav_up_press));
                        break;
                    case MotionEvent.ACTION_UP:
                        mRlReceive.setBackground(ctx.getDrawable(R.drawable.tooltip_bg));
                        mImvArrowRight.setBackground(ctx.getDrawable(R.drawable.nav_up));
                        if (mAction != null)
                            mAction.onReceiveScreenChoice();
                        dismissTooltip();
                        break;
                }
                return true;
            }
        });
        showChoiceTypeShare(isShowSwipe);
        mRlSwipe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRlSwipe.setBackground(ctx.getDrawable(R.drawable.tooltip_bg_press));
                        mImvArrowRight.setBackground(ctx.getDrawable(R.drawable.nav_up_press));
                        break;
                    case MotionEvent.ACTION_UP:
                        mRlSwipe.setBackground(ctx.getDrawable(R.drawable.tooltip_bg));
                        mImvArrowRight.setBackground(ctx.getDrawable(R.drawable.nav_up));
                        if (mAction != null)
                            mAction.onSwipeScreenChoice();
                        dismissTooltip();
                        break;
                }
                return true;
            }
        });

        mRlQrScan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRlQrScan.setBackground(ctx.getDrawable(R.drawable.tooltip_bg_press));
                        mImvArrowRight.setBackground(ctx.getDrawable(R.drawable.nav_up));
                        break;
                    case MotionEvent.ACTION_UP:
                        mRlQrScan.setBackground(ctx.getDrawable(R.drawable.tooltip_bg));
                        mImvArrowRight.setBackground(ctx.getDrawable(R.drawable.nav_up));
                        if (mAction != null)
                            mAction.onScanQr();
                        dismissTooltip();
                        break;
                }
                return true;
            }
        });

        mRlTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissTooltip();
            }
        });
        mTvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceTypeShare(true);
            }
        });

        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceTypeShare(false);
            }
        });
    }

    private void showChoiceTypeShare(boolean isShowSwipe) {
        mLlTeacher.setVisibility(isShowSwipe ? View.VISIBLE : View.GONE);
        mImvArrowLeft.setVisibility(isShowSwipe ? View.VISIBLE : View.GONE);
        mLlScan.setVisibility(isShowSwipe ? View.GONE : View.VISIBLE);
        mImvArrowRight.setVisibility(isShowSwipe ? View.GONE : View.VISIBLE);
    }

    public void showToolTip(View anchor) {
        tipWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        tipWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        tipWindow.setOutsideTouchable(true);
        tipWindow.setTouchable(true);
        tipWindow.setFocusable(true);
        tipWindow.setBackgroundDrawable(new BitmapDrawable());
        contentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tipWindow.setContentView(contentView);
//        tipWindow.showAsDropDown(anchor);
        tipWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, anchor.getBottom() - 100);
// send message to handler to dismiss tipWindow after X milliseconds
//        handler.sendEmptyMessageDelayed(MSG_DISMISS_TOOLTIP, 4000);
    }

    public boolean isTooltipShown() {
        if (tipWindow != null && tipWindow.isShowing())
            return true;
        return false;
    }

    void dismissTooltip() {
        if (tipWindow != null && tipWindow.isShowing())
            tipWindow.dismiss();
    }

    public void setAction(onAction action) {
        mAction = action;
    }

    public interface onAction {
        void onQrCodeChoice();

        void onReceiveScreenChoice();

        void onSwipeScreenChoice();

        void onScanQr();
    }

}