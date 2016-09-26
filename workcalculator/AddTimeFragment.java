package com.marduc812.workcalculator;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTimeFragment extends Fragment {

    // Stats variables
    long total_hours_worked, Weekly_hours_worked,longest_shift,average_shift,monthly_hours;
    float total_income,weekly_income,highest_income,average_income,monthly_income;
    int times_worked;
    PullToRefreshView mPullToRefreshView;
    View v;

    // Other fields
    Long start_time_ms,elapsed_time_ms,finish_time_ms;
    String start_time,start_only_time;
    Boolean working,active_ui;

    protected RecyclerView recyclerView;
    protected RecyclerView.Adapter DatasAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        update();

    }

    public AddTimeFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_time, container, false);

        mPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        update();
                        Toast.makeText(getContext(),"pulled",Toast.LENGTH_SHORT).show();
                        DatasAdapter.notifyDataSetChanged();
                        //recyclerView.setAdapter(DatasAdapter);
                    }
                }, 1000);
            }
        });

        recyclerView  = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(DatasAdapter);

        return v;
    }

    public void update()
    {
        if (times_worked>0)
        {
            average_income = total_income/times_worked;
            average_shift = total_hours_worked/times_worked;
        }
        


        final Stats_ad stats_ad[] =
        {
                new Stats_ad("Times Worked",String.valueOf(times_worked)),
                new Stats_ad("Total Hours",String.valueOf(total_hours_worked)),
                new Stats_ad("Weekly Hours",String.valueOf(Weekly_hours_worked)),
                new Stats_ad("Longest Shift",String.valueOf(longest_shift)),
                new Stats_ad("Average Shift",String.valueOf(average_shift)),
                new Stats_ad("Monthly Hours",String.valueOf(monthly_hours)),
                new Stats_ad("Total Income",String.valueOf(total_income)),
                new Stats_ad("Weekly Income",String.valueOf(weekly_income)),
                new Stats_ad("Highest Income",String.valueOf(highest_income)),
                new Stats_ad("Average Income",String.valueOf(average_income)),
                new Stats_ad("Monthly Income",String.valueOf(monthly_income)),
        };

        DatasAdapter = new DatasAdapter(stats_ad);

    }

    @Override
    public void onPause() {
        super.onPause();

        active_ui = false;

    }


    @Override
    public void onResume() {

        super.onResume();
        active_ui = true;
        SharedPreferences settings = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
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

        update();
        DatasAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(DatasAdapter);

    }



}
