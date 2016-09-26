package com.marduc812.workcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yalantis.phoenix.PullToRefreshView;

import java.text.DecimalFormat;

/**
 * Created by marduc on 19/09/16.
 */
public class Stats extends AppCompatActivity{

    // Stats variables
    long total_hours_worked, Weekly_hours_worked,longest_shift,average_shift,monthly_hours;
    float total_income,weekly_income,highest_income,average_income,monthly_income;
    int times_worked;
    HomeFragment home;

    // Other fields
    Long start_time_ms,elapsed_time_ms,finish_time_ms;
    String start_time,start_only_time;
    Boolean working,active_ui;
    String Currency;

    int last_day,weekofyear;

    protected RecyclerView recyclerView;
    protected RecyclerView.Adapter DatasAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);



        update();


        int or = getResources().getConfiguration().orientation;
        if (or==1)
        {
            mLayoutManager = new LinearLayoutManager(this);
        }
        else
        {
            mLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        }




        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        if (position==0)
                        {
                            clearDialog("Times worked",position);
                        }
                        else if (position==1)
                        {
                            clearDialog("Total Hours",position);
                        }
                        else if (position==2)
                        {
                            clearDialog("Weekly Hours",position);
                        }
                        else if (position==3)
                        {
                            clearDialog("Longest Shift",position);
                        }
                        else if (position==4)
                        {
                            clearDialog("Average Shift",position);
                        }
                        else if (position==5)
                        {
                            clearDialog("Monthly Hours",position);
                        }
                        else if (position==6)
                        {
                            clearDialog("Total Income",position);
                        }
                        else if (position==7)
                        {
                            clearDialog("Weekly Income",position);
                        }
                        else if (position==8)
                        {
                            clearDialog("Highest Income",position);
                        }
                        else if (position==9)
                        {
                            clearDialog("Average Income",position);
                        }
                        else if (position==10)
                        {
                            clearDialog("Monthly Income",position);
                        }
                    }
                })
        );
    }

    public void clearDialog(String selection, final int position)
    {
        new MaterialDialog.Builder(this)
                .title("Want to reset "+selection)
                .positiveText("Reset")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (position==0)
                        {
                            times_worked=0;
                        }
                        else if (position==1)
                        {
                            total_hours_worked=0;

                        }
                        else if (position==2)
                        {
                            Weekly_hours_worked=0;
                        }
                        else if (position==3)
                        {
                            longest_shift=0;
                        }
                        else if (position==4)
                        {
                            average_shift=0;
                        }
                        else if (position==5)
                        {
                            monthly_hours=0;
                        }
                        else if (position==6)
                        {
                            total_income=0;
                        }
                        else if (position==7)
                        {
                            weekly_income=0;
                        }
                        else if (position==8)
                        {
                            highest_income=0;
                        }
                        else if (position==9)
                        {
                            average_income=0;
                        }
                        else if (position==10)
                        {
                            monthly_income=0;
                        }
                        update();
                        
                    }})
                .show();

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }

    public void update()
    {
        if (times_worked>0)
        {
            average_income = total_income/times_worked;
            average_shift = total_hours_worked/times_worked;
        }

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        Currency = getPrefs.getString("currency","$");

        home = new HomeFragment();

        final Stats_ad stats_ad[] =
                {
                        new Stats_ad("Times Worked",String.valueOf(times_worked)),
                        new Stats_ad("Total Hours",String.valueOf(home.long2mins(total_hours_worked))),
                        new Stats_ad("Weekly Hours",String.valueOf(home.long2mins(Weekly_hours_worked))),
                        new Stats_ad("Longest Shift",String.valueOf(home.long2mins(longest_shift))),
                        new Stats_ad("Average Shift",String.valueOf(home.long2mins(average_shift))),
                        new Stats_ad("Monthly Hours",String.valueOf(home.long2mins(monthly_hours))),
                        new Stats_ad("Total Income",String.valueOf(new DecimalFormat("##.##").format((total_income)))+Currency),
                        new Stats_ad("Weekly Income",String.valueOf(new DecimalFormat("##.##").format((weekly_income)))+Currency),
                        new Stats_ad("Highest Income",String.valueOf(new DecimalFormat("##.##").format((highest_income)))+Currency),
                        new Stats_ad("Average Income",String.valueOf(new DecimalFormat("##.##").format((average_income)))+Currency),
                        new Stats_ad("Monthly Income",String.valueOf(new DecimalFormat("##.##").format((monthly_income)))+Currency),
                };

        DatasAdapter = new DatasAdapter(stats_ad);

        recyclerView  = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(DatasAdapter);

    }

    @Override
    public void onPause() {
        super.onPause();
        active_ui = false;
        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putLong("start_time_l",start_time_ms);
        editor.putString("start_time_s",start_time);
        editor.putBoolean("working_b",working);
        editor.putString("start_only_times",start_only_time);
        // Stats
        editor.putInt("time_worked",times_worked);
        // Time preferences
        editor.putLong("total_hours",total_hours_worked);
        editor.putLong("long_shift",longest_shift);
        editor.putLong("aver_shift",average_shift);
        editor.putLong("weekly_hours",Weekly_hours_worked);
        editor.putLong("month_hours",monthly_hours);
        // Money Preferences
        editor.putFloat("total_inc",total_income);
        editor.putFloat("highest_inc",highest_income);
        editor.putFloat("average_inc",average_income);
        editor.putFloat("weekly_inc",weekly_income);
        editor.putFloat("monthly_inc",monthly_income);

        editor.putInt("last_days",last_day);
        editor.putInt("week_of_year",weekofyear);


        editor.commit();
    }

    @Override
    public void onResume() {

        super.onResume();
        active_ui = true;


        SharedPreferences settings = this.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        start_time_ms = settings.getLong("start_time_l",0);
        working = settings.getBoolean("working_b",false);
        start_time = settings.getString("start_time_s","-");
        start_only_time = settings.getString("start_only_times","-");
        //Stats
        times_worked = settings.getInt("time_worked",0);
        // Time Preferences
        total_hours_worked = settings.getLong("total_hours",0);
        longest_shift = settings.getLong("long_shift",0);
        average_shift = settings.getLong("aver_shift",0);
        Weekly_hours_worked = settings.getLong("weekly_hours",0);
        monthly_hours = settings.getLong("month_hours",0);
        // Money Preferences
        total_income = settings.getFloat("total_inc",0);
        highest_income = settings.getFloat("highest_inc",0);
        average_income = settings.getFloat("average_inc",0);
        weekly_income = settings.getFloat("weekly_inc",0);
        monthly_income = settings.getFloat("monthly_inc",0);
        // Extras

        last_day = settings.getInt("last_days", 2);
        weekofyear = settings.getInt("week_of_year",0);

        update();
        DatasAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(DatasAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.itemsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.stats:
            {
                    times_worked=0;
                    total_hours_worked=0;
                    Weekly_hours_worked=0;
                    longest_shift=0;
                    average_shift=0;
                    monthly_hours=0;
                    total_income=0;
                    weekly_income=0;
                    highest_income=0;
                    average_income=0;
                    monthly_income=0;
                update();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
