package com.marduc812.workcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity {

    // To DO
    // Na allazoun ta toolbar titles analoga me to tab

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    Long start_time_ms,elapsed_time_ms,finish_time_ms;
    String start_time,start_only_time;
    Boolean working;
    float curinc,income;
    String Currency;
    boolean active_ui;
    SQLController dbcon;
    FloatingActionButton ssb,addwork;
    private FragmentActivity myContext;
    FloatingActionsMenu work_fab;
    Boolean RestartWeekDay;
    int dayofmonth;
    String name;
    int weekofyear, weekofyearSP;
    HomeFragment home;

    // Na mpei to currency onpause

    // Stats variables
    long total_hours_worked, Weekly_hours_worked,longest_shift,average_shift,monthly_hours;
    float total_income,weekly_income,highest_income,average_income,monthly_income;
    int times_worked;
    int week_starts_day;
    int last_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme_Dark);
        setContentView(R.layout.activity_main);

        home = new HomeFragment();

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setElevation(0);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HomeFragment(), "Start Work");
        //viewPagerAdapter.addFragments(new AddTimeFragment(), "Stats");
        viewPagerAdapter.addFragments(new ReportFragment(), "History");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_timelapse_white_36dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_timeline_white_36dp);
       // tabLayout.getTabAt(2).setIcon(R.drawable.ic_timeline_white_36dp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.preferences:
            {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                return true;
            }

            case R.id.delete_history:
            {
                new MaterialDialog.Builder(this)
                        .title("Want to delete history?")
                        .positiveText("Delete")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //Toast.makeText(getContext(), "Which: " + which, Toast.LENGTH_SHORT).show();
                                DBhelper dbh = new DBhelper(MainActivity.this);
                                SQLiteDatabase db = dbh.getWritableDatabase();
                                db.delete(dbh.TABLE_NAME, null, null);
                                Toast.makeText(getApplicationContext(),"History cleared. You can go to history tab and pull down to refresh.",Toast.LENGTH_LONG).show();
                            }})
                        .negativeText("Cancel")
                        .show();

                    return true;
            }

            case R.id.stats:
            {
                Intent i = new Intent(MainActivity.this,Stats.class);
                startActivity(i);
                return true;
            }

            case R.id.rateapp:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.marduc812.workcalculator"));
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
