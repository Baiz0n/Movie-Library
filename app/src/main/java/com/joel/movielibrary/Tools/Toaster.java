package com.joel.movielibrary.Tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.joel.movielibrary.R;

public class Toaster {

    private static Toast toast;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void  longMidToast(Context context, String msg) {

        style(context ,msg, 1,1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void longBotToast(Context context, String msg) {

        style(context, msg, 1,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void shortMidToast(Context context, String msg) {

        style(context, msg, 0, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void shortBotToast(Context context, String msg) {

        style(context, msg, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void style(Context context, String msg, int length, int gravity) {

        if ( length == 0 ) {

            toast = Toast.makeText(context,
                    msg,
                    Toast.LENGTH_SHORT);

        } else {

            toast = Toast.makeText(context,
                    msg,
                    Toast.LENGTH_LONG);
        }

        if ( gravity == 1 ) {

            toast.setGravity(Gravity.CENTER,0,0);
        }

        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(25);
        tv.setBackground(context.getDrawable(R.drawable.rounded_corner));
        toast.show();
    }
}
