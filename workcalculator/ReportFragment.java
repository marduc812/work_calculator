package com.marduc812.workcalculator;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yalantis.phoenix.PullToRefreshView;


/**
 * A simple {@link Fragment} subclass.
 *
 *
 *
 * Thelw Start time, wra ergasias, lefta
 */
public class ReportFragment extends Fragment {

    SQLController dbcon;
    PullToRefreshView mPullToRefreshView;
    ListView favlist;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            CreateDatas();
        }
        else {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_report, container, false);
        favlist = (ListView) v.findViewById(R.id.listView);
        CreateDatas();

        mPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        CreateDatas();
                    }
                }, 1000);
            }
        });


        favlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

                new MaterialDialog.Builder(getContext())
                        .title("Delete Entry")
                        .items("Are you sure you want to delete this event?")
                        .positiveText("DELETE")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //Toast.makeText(getContext(), "Which: " + which, Toast.LENGTH_SHORT).show();
                                dbcon.deleteData(id);
                                CreateDatas();
                            }})
                        .negativeText("Cancel")
                        .show();

                return false;
            }
        });





        return v;
    }



    public void CreateDatas()
    {
        dbcon = new SQLController(getContext());
        dbcon.open();


        Cursor cursor = dbcon.readData();
        final String[] from = new String[] { DBhelper.UID, DBhelper.DAY, DBhelper.MONTHYEAR ,DBhelper.STARTED, DBhelper.ELAPSED, DBhelper.INCOME };


        int[] to = new int[] { R.id.textView, R.id.date_tv, R.id.month_tv,R.id.started_tv ,R.id.worked_tv, R.id.income_tv};


        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(), R.layout.listitem, cursor, from, to);

        favlist.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        CreateDatas();
    }
}
