package com.marduc812.workcalculator;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by marduc on 19/09/16.
 */
public class Libraries extends AppCompatActivity{

    protected RecyclerView recyclerView;
    protected RecyclerView.Adapter LibrariesAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libraries);


        final Libraries_ad lib_ad[] =
                {
                        new Libraries_ad("FloatingActionButton","chalup"," http://www.apache.org/licenses/LICENSE-2.0","https://github.com/futuresimple/android-floating-action-button"),
                        new Libraries_ad("material-preferences","ferrannp","The MIT License (MIT)","https://github.com/ferrannp/material-preferences"),
                        new Libraries_ad("Material Dialogs","Aidan Follestad","The MIT License (MIT)","https://github.com/afollestad/material-dialogs"),
                        new Libraries_ad("Material DateTime Picker - Select a time/date in style","wdullaer","http://www.apache.org/licenses/LICENSE-2.0","https://github.com/wdullaer/MaterialDateTimePicker"),
                        new Libraries_ad("Phoenix Pull-to-Refresh","Yalantis","http://www.apache.org/licenses/LICENSE-2.0","https://github.com/Yalantis/Phoenix")

                };

        LibrariesAdapter = new LibrariesAdapter(lib_ad);

        int or = getResources().getConfiguration().orientation;
        if (or==1)
        {
            mLayoutManager = new LinearLayoutManager(this);

        }
        else
        {
            mLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        }



        // use a  layout manager
        recyclerView  = (RecyclerView) findViewById(R.id.my_recycler_view);
        //mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(LibrariesAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lib_ad[position].getUrl()));
                        startActivity(browserIntent);

                    }
                })
        );

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }


}
