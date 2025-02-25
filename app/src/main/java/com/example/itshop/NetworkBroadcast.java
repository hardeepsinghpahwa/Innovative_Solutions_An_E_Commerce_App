package com.example.itshop;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.itshop.R;
import com.valdesekamdem.library.mdtoast.MDToast;

public class NetworkBroadcast extends BroadcastReceiver {

    Dialog dialog;

    @Override
    public void onReceive(final Context context, Intent intent) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        boolean isMetered = cm.isActiveNetworkMetered();

        if(isConnected)
        {
            if(dialog!=null)
            {
                dialog.dismiss();
                MDToast.makeText(context,"Connection Succesfull",MDToast.LENGTH_SHORT,MDToast.TYPE_SUCCESS).show();
            }
        }
        else{

            TextView retry;

            dialog=new Dialog(context);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.nointernetdialog);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_UpDown);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            retry=dialog.findViewById(R.id.retry);

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager cm =
                            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if(isConnected)
                    {
                        dialog.dismiss();
                    }
                }
            });

        }
    }

}
