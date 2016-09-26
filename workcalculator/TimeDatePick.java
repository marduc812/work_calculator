package com.marduc812.workcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by marduc on 10/09/16.
 */
public class TimeDatePick extends Activity implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    Button dateadd,leavedate,endwork;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    int sday,smonth,syear,shour,smin,ssec;
    int eday,emonth,eyear,ehour,emin,esec;
    int cday,cmonth,cyear,chour,cmin,csec;
    long start_time_l,end_time;
    ListView listView;
    String currency;
    Button add_shift;
    float income;
    HomeFragment home;
    String totalincome,time_worked;
    SQLController dbcon;
    int working_hard;
    Button StartTime,EndTime,StartDay;
    TextView EndDay;
    Switch switcher;
    long extra_day;
    ListView listview;
    // Stats variables
    long total_hours_worked, Weekly_hours_worked,longest_shift,average_shift,monthly_hours;
    float total_income,weekly_income,highest_income,average_income,monthly_income;
    int times_worked;


    Boolean starttimeset,startdateset,endtimeset,enddateset;
    private long start_time_ms;
    private String start_time,start_only_time;
    private int weekofyear,last_day;
    private boolean working;
    private float totalincomefloat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datetime);

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        income = Float.valueOf(getPrefs.getString("income_per_hour", "10"));
        currency = getPrefs.getString("currency", "€");
        extra_day=0;
        esec = ssec = csec = 0; // Den me endiaferoun ta deuterolepta
        home = new HomeFragment();

        StartDay = (Button) findViewById(R.id.button3);
        StartTime = (Button) findViewById(R.id.button2);
        EndTime = (Button) findViewById(R.id.button4);
        EndDay = (TextView) findViewById(R.id.textView12);
        switcher = (Switch) findViewById(R.id.switch1);
        listView = (ListView) findViewById(R.id.listView3);

        //start = (Button) findViewById(R.id.button);
        //end = (Button) findViewById(R.id.button2);

        add_shift = (Button) findViewById(R.id.button);

        /*StartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });*/

        Calendar calendar = Calendar.getInstance();
        sday = eday = cday = calendar.get(Calendar.DAY_OF_MONTH);
        smonth = emonth = cmonth = calendar.get(Calendar.MONTH);
        syear = eyear = cyear = calendar.get(Calendar.YEAR);
        shour = ehour = chour = calendar.get(Calendar.HOUR_OF_DAY);
        smin = emin = cmin = calendar.get(Calendar.MINUTE);
        totalincome = time_worked = "-";

        UpdateUI();

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    extra_day=86400000;
                }
                else
                {
                    extra_day=0;
                }
                Long longdate = DatetoLong(eyear,emonth,eday,0,0)+extra_day;
                EndDay.setText(longtoDate(longdate));
                updatevalues();
            }
        });

        add_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbcon = new SQLController(TimeDatePick.this);
                dbcon.open();
                times_worked++;
                total_income+= totalincomefloat;
                weekly_income+=totalincomefloat;
                monthly_income+=totalincomefloat;
                total_hours_worked+=end_time-start_time_l;
                Weekly_hours_worked+=end_time-start_time_l;
                monthly_hours+=end_time-start_time_l;
                checkifLongest(end_time-start_time_l);
                checkifHighest(totalincomefloat);

                dbcon.insertData(String.valueOf(sday),monthfromnum(smonth) +"/"+ syear,shour+":"+home.zeroInfront(smin)+":00",time_worked,totalincome+""+currency);
                Toast.makeText(getApplicationContext(),"Shift Successfully added to your history",Toast.LENGTH_SHORT).show();
                finish();


            }
        });

        StartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                working_hard=0;
                PickATime();
                 //orizei Start Time
            }
        });

        StartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickADate();
            }
        });

        EndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                working_hard=3;
                PickATime();
            }
        });


        //updatevalues();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void UpdateUI() {
        StartTime.setText(shour+":"+home.zeroInfront(smin));
        StartDay.setText(sday+" "+monthfromnum(smonth)+" "+syear);
        EndTime.setText(ehour+":"+home.zeroInfront(emin));
        EndDay.setText(eday+" "+monthfromnum(emonth)+" "+eyear);
    }

    public String longtoDate(long dateinlong)
    {
        return  new SimpleDateFormat("dd MMM yyyy").format(new Date(dateinlong));
    }


    public static String monthfromnum(int month){
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }

    public long DatetoLong(int years, int months, int days, int hours, int mins)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(years, months, days,
                hours, mins, 0);
        long date_in_ms = calendar.getTimeInMillis();
        return date_in_ms;
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

    public void PickATime()
    {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                TimeDatePick.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true //24hours mode true
        );
        tpd.show(getFragmentManager(),"Timepickerdialog");

    }

    public void PickADate()
    {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                TimeDatePick.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

            eday=sday=dayOfMonth;
            eyear=syear=year;
            emonth=smonth=monthOfYear;

        UpdateUI();
        updatevalues();

    }

    private void updatevalues() {

        calculatetime();
        
        String[] defined_values = new String[] {
                "Income: " + totalincome , "Time Worked: " + time_worked};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, defined_values);

           listView.setAdapter(adapter);
    }

    private void calculatetime() {



        start_time_l = DatetoLong(syear,smonth,sday,shour,smin);
        end_time = DatetoLong(eyear,emonth,eday,ehour,emin)+extra_day;


        if (end_time> start_time_l)
        {
        totalincome = String.valueOf(new DecimalFormat("##.##").format((end_time - start_time_l)/1000*(income/3600)));
        totalincomefloat = (end_time - start_time_l)/1000*(income/3600);
        time_worked = TimeFromMS(end_time- start_time_l);
            add_shift.setEnabled(true);
        }
        else // Xekinhse afou eixe sxolasei ERROR
        {
            totalincome = time_worked = "-";
            add_shift.setEnabled(false);
        }

    }

    private String TimeFromMS(long ms) {

        int seconds = (int) (ms / 1000) % 60 ;
        int minutes = (int) ((ms / (1000*60)) % 60);
        int hours   = (int) ((ms / (1000*60*60)));

        String time = hours+":"+home.zeroInfront(minutes)+":"+home.zeroInfront(seconds);
        return time;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        if (working_hard==0)
        {
            shour = hourOfDay;
            smin = minute;
        }
        else
        {
            ehour = hourOfDay;
            emin = minute;
        }
        UpdateUI();
        updatevalues();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TimeDatePick Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.marduc812.workcalculator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TimeDatePick Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.marduc812.workcalculator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();

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

        // Auto-resets of stats
        //editor.putInt("reset_of_month",dayofmonth);
        editor.commit();
    }


    @Override
    public void onResume() {
        super.onResume();

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
        //dayofmonth = settings.getInt("reset_of_month",25);
        last_day = settings.getInt("last_days",2);
        weekofyear = settings.getInt("week_of_year",0);

        calculatetime();
    }


    @Override
    public void onClick(View v) {

    }
}
