package com.marduc812.workcalculator;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ListView listView;
    ArrayList<String> variable,desc;
    ArrayList<Integer> shape;
    Long start_time_ms,elapsed_time_ms,finish_time_ms;
    String start_time,start_only_time;
    Boolean working;
    float curinc,income;
    String Currency;
    boolean active_ui;
    SQLController dbcon;
    FloatingActionButton ssb,addwork,cancel_shift;
    private FragmentActivity myContext;
    FloatingActionsMenu work_fab;
    Boolean RestartWeekDay;
    int dayofmonth;
    String name;
    int weekofyear, weekofyearSP;
    RelativeLayout relay;

    // Na mpei to currency onpause

    // Stats variables
    long total_hours_worked, Weekly_hours_worked,longest_shift,average_shift,monthly_hours;
    float total_income,weekly_income,highest_income,average_income,monthly_income;
    int times_worked;
    int week_starts_day;
    int last_day;



    AlertDialog alert;

    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ssb = (FloatingActionButton) v.findViewById(R.id.start_work_fab);
        addwork = (FloatingActionButton) v.findViewById(R.id.add_work_fab);
        work_fab = (FloatingActionsMenu) v.findViewById(R.id.multiple_actions);
        cancel_shift = (FloatingActionButton) v.findViewById(R.id.cancel_shift);
        relay = (RelativeLayout) v.findViewById(R.id.rwllay);



        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        income = Float.valueOf(getPrefs.getString("income_per_hour", "10"));
        week_starts_day = Integer.valueOf(getPrefs.getString("week_start_day","4"));
        Currency = getPrefs.getString("currency","$");
        name = getPrefs.getString("work_title","Shift");

        working = false;

        listView = (ListView) v.findViewById(R.id.listView2);

        if (!working) // if now working show empty view
        {
            ImageView empty = (ImageView) v.findViewById(R.id.empty);
            listView.setEmptyView(empty);
            cancel_shift.setVisibility(View.GONE);

        }

        cancel_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                working=false;
                ssb.setTitle("Start Shift");
                Toast.makeText(getContext(),"Swift Canceled. You can start at any time a new swift.",Toast.LENGTH_SHORT).show();
            }
        });

        ssb.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (working)
                {
                    finish_time_ms = getCurrentTimeinms();
                    elapsed_time_ms = finish_time_ms - start_time_ms;
                    total_hours_worked+=elapsed_time_ms;
                    Weekly_hours_worked+=elapsed_time_ms;
                    monthly_hours+=elapsed_time_ms;
                    working=false;

                    dbcon = new SQLController(getContext());
                    dbcon.open();
                    String elapsed_time_s = long2mins(getElapsedTime(start_time_ms));
                    String incoming = String.valueOf(new DecimalFormat("##.##").format((getElapsedTime(start_time_ms)/1000)*(income/3600)))+Currency;
                    dbcon.insertData(getMonthday(),getMonthYear(),start_only_time,elapsed_time_s,incoming);
                    curinc = getElapsedTime(start_time_ms)/1000*(income/3600);
                    weekly_income+=curinc;
                    monthly_income+=curinc;
                    total_income+=curinc;
                    CreateCalendarEvent();
                    checkifLongest(elapsed_time_ms);
                    checkifHighest(curinc);
                    times_worked++;
                    updateList();
                    CreateData();
                    cancel_shift.setVisibility(View.GONE);
                    ssb.setTitle("Start Shift");
                    Snackbar.make(relay, "Your Shift if over.", Snackbar.LENGTH_LONG).show();


                }
                else
                {   startWork();
                    updateList();
                    starting();
                    cancel_shift.setVisibility(View.VISIBLE);
                    ssb.setTitle("End Shift");
                }

            work_fab.collapse();
            }


        });

        addwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                work_fab.collapse();
                Intent i = new Intent(getActivity(),TimeDatePick.class);
                startActivity(i);
            }
        });

        starting();
        return v;
    }

    private void CreateCalendarEvent(){

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean eventCalendar = getPrefs.getBoolean("calendar_event", true);


        if (eventCalendar)
        {
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setData(CalendarContract.Events.CONTENT_URI);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, name);
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start_time_ms);
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finish_time_ms);
            calIntent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
            calIntent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "You earned " + new DecimalFormat("##.##").format((curinc))+Currency + " in " + long2mins(elapsed_time_ms));
            startActivity(calIntent);
        }
    }



    public void updateList()
    {
        CreateData();
        Home_Adapter adapter = new Home_Adapter((Activity) getContext(),variable,desc,shape);
        listView.setAdapter(adapter);
    }

    public String getMonthday() { // Gia na fainetai h hmeromhnia sto megalo kouti
        Calendar calendar = Calendar.getInstance();
        String Monthday = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return Monthday;
    }

    public String getOnlyTime()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("kk:mm:ss");
        String cur_time = dateformat.format(c.getTime());
        return cur_time;
    }

    public void checkifHighest(float income)
    {
        if (income>highest_income)
            highest_income=income;
    }

    public void checkifLongest (Long elapsed_time_ms)
    {
        if (elapsed_time_ms>longest_shift)
        {
            longest_shift = elapsed_time_ms;
        }
    }



    public String getMonthYear() { // Gia na vrei mhna kai xrono gia to katw meros
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(calendar.getTime());
        int year = calendar.get(Calendar.YEAR);
        String monthYear = String.valueOf(month_name + "/" + year);
        return monthYear;
    }

    @Override
    public void onPause() {
        super.onPause();
        active_ui = false;
        SharedPreferences settings = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
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

        // Auto-resets of stats
        //editor.putInt("reset_of_month",dayofmonth);
        editor.commit();
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
        // Extras
        //dayofmonth = settings.getInt("reset_of_month",25);
        last_day = settings.getInt("last_days",2);
        weekofyear = settings.getInt("week_of_year",0);
        starting();
        if (working)
            {
                //ssb.setText("Stop Swift");
                updateList();
                ssb.setTitle("End Shift");
                cancel_shift.setVisibility(View.VISIBLE);
            }

        RestartWeek();
        RestartMonth();
    }

    public void RestartMonth()
    {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (last_day > day)
        {
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            Boolean weekChange = getPrefs.getBoolean("week_change", true);
            Toast.makeText(getContext(), "Week Change " + weekChange, Toast.LENGTH_SHORT).show();
            if (weekChange) {
                if (monthly_income != 0)
                {
                new MaterialDialog.Builder(getContext())
                        .title("Do you want to restart your monthly stats?")
                        .items("You can always restart stats Manually at any time")
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            //Toast.makeText(getContext(), "Which: " + which, Toast.LENGTH_SHORT).show();
                                            monthly_income=0;
                                            monthly_hours=0;
                                            Toast.makeText(getContext(),"New Month Started",Toast.LENGTH_SHORT).show();
                                        }})
                        .negativeText("Cancel")
                        .show();
                }

                }
            else
            {
                monthly_income=0;
                monthly_hours=0;
                Toast.makeText(getContext(), "New Month stated", Toast.LENGTH_SHORT).show();
            }
        }

        last_day=day;
    }

    public void RestartWeek()
    {
        Calendar cal = Calendar.getInstance();
        weekofyearSP = cal.get(Calendar.WEEK_OF_YEAR);

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int weekstartday = Integer.valueOf(getPrefs.getString("week_start_day","1"));

        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day==weekstartday) // H mera pou xekinaei h vdomada
        {
            if (weekofyear!=weekofyearSP) // an exei allaxei h vdomada
            {
                WeekChange();
                Toast.makeText(getActivity(),"WeekofYear" + weekofyear + "\n weekofyearsp" + weekofyearSP,Toast.LENGTH_SHORT).show();
                weekofyear = weekofyearSP;
            }

        }
        else
        {
            if (weekofyear!=weekofyearSP) // an exei allaxei h vdomada
            {
                if (day>weekstartday) // an exei perasei h mera apo thn mera pou oristhke
                {
                    WeekChange();
                }


            }
            //Toast.makeText(getContext(),"Today is NOT a restart day",Toast.LENGTH_SHORT).show();
            weekofyear=weekofyearSP;
        }


    }


    public void WeekChange()
    {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean weekChange = getPrefs.getBoolean("week_change", true);

        if (weekChange)
        {
            if (weekly_income!=0 || weekofyearSP!=weekofyear)
            {
                new MaterialDialog.Builder(getContext())
                        .title("Do you want to restart your Weekly stats?")
                        .items("Seems like your restart day passed")
                        .positiveText("Restart")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //Toast.makeText(getContext(), "Which: " + which, Toast.LENGTH_SHORT).show();
                                weekly_income = 0;
                                Weekly_hours_worked = 0;
                                Toast.makeText(getContext(), "New Week Started", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .negativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                weekofyear=weekofyearSP;
                            }
                        })
                        .show();
            }

        }
        else
        {
            weekly_income=0;
            Weekly_hours_worked=0;
            Toast.makeText(getContext(),"New Week Started",Toast.LENGTH_SHORT).show();
        }
    }

    public void CreateData()
    {
        variable = new ArrayList<String>();
        variable.add(start_time);
        variable.add(long2mins(getElapsedTime(start_time_ms)));
        variable.add(String.valueOf(new DecimalFormat("##.##").format((getElapsedTime(start_time_ms)/1000)*(income/3600)))+Currency);
        variable.add(String.valueOf(new DecimalFormat("##.##").format((weekly_income)))+Currency);
        variable.add(String.valueOf(new DecimalFormat("##.##").format((income)))+Currency + " per hour");

        desc = new ArrayList<String>();
        desc.add("Start Time");
        desc.add("Elapsed Time");
        desc.add("Current Income");
        desc.add("Weekly Income");
        desc.add("Selected Income");

        shape = new ArrayList<Integer>();
        shape.add(R.drawable.ic_timelapse_white_36dp);
        shape.add(R.drawable.ic_timeline_white_36dp);
        shape.add(R.drawable.ic_timelapse_white_36dp);
        shape.add(R.drawable.ic_timelapse_white_36dp);
        shape.add(R.drawable.ic_timelapse_white_36dp);

    }


    public String getCurrentTime() // pairnei wra se string morfh
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


    public long getCurrentTimeinms() // pairnei long
    {
        long currentlongtime = System.currentTimeMillis();
        //Toast.makeText(getContext(),"Current time in ms: "+ currentlongtime,Toast.LENGTH_SHORT).show();
        return currentlongtime;
    }


    public void startWork()
    {   start_only_time = getOnlyTime();
        start_time_ms = getCurrentTimeinms();
        start_time = getCurrentTime();
        working=true;
        //ssb.setText("Stop Swift");
    }

    public Long getElapsedTime(long starttime) // ypologizei xrono pou perase
    {
        long currentime = getCurrentTimeinms();
        long elapsed_ms_time = currentime - starttime;
        return elapsed_ms_time;
    }

    public String long2mins(long difference)
    {
        int hours = (int) (difference/(1000 * 60 * 60));
        int min = (int) (difference/(1000*60)) % 60;
        int sec = (int) (difference/(1000)) % 60;
        String result = hours+":"+zeroInfront(min)+":"+zeroInfront(sec);
        return result;
    }

    public String zeroInfront(int number)
    {
        String resnum;
        if (number<10)
            resnum = "0"+number;
        else
            resnum = String.valueOf(number);
        return resnum;
    }



    private void starting() {
        new Thread() {
            public void run() {
                while (active_ui && working) {

                    if(getActivity() == null)
                        return;

                    try {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                CreateData();
                                updateList();
                            }
                        });
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }



}



