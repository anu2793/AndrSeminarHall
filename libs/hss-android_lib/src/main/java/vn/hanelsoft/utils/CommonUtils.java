package vn.hanelsoft.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by macmobiles on 3/18/2016.
 */
public class CommonUtils {
    /**
     * convert a dp value to pixel
     */
    public static int convertToPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int calculateListviewHeight(ListView listView) {
        Adapter adapter = listView.getAdapter();
        if (adapter == null) return 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        totalHeight += (listView.getDividerHeight() * (adapter.getCount() - 1));
        return totalHeight;
    }

    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled || isNetworkEnabled;
    }

    public static void setTintForView(View view, int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        //Keep alpha
        float[] filter = new float[]{
                0, 0, 0, 0, r,  //Red
                0, 0, 0, 0, g,  //Green
                0, 0, 0, 0, b,  //Blue
                0, 0, 0, 1, 0};
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(filter);
        if (view instanceof ImageView) {
            ((ImageView) view).setColorFilter(colorFilter);
        } else if (view instanceof TextView) {
            for (Drawable d : ((TextView) view).getCompoundDrawables()) {
                if (d != null) d.mutate().setColorFilter(colorFilter);
            }
        } else {
            view.getBackground().setColorFilter(colorFilter);
        }
    }
}
