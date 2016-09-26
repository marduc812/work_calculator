package com.marduc812.workcalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marduc on 14/07/16.
 */
public class Home_Adapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> AnnouTitle;
    private final ArrayList<String> AnnouDesc;
    private final ArrayList<Integer> AnnouImage;


    public Home_Adapter(Activity context,
                            ArrayList<String> AnnounTitle, ArrayList<String> AnnouDesc, ArrayList<Integer> AnnouImage) {
        super(context, R.layout.home_list_item, AnnounTitle);
        this.context = context;
        this.AnnouTitle = AnnounTitle;
        this.AnnouDesc = AnnouDesc;
        this.AnnouImage = AnnouImage;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"helveticaneue.ttf");


        View rowView= inflater.inflate(R.layout.home_list_item, null, true);
        TextView AnTitle = (TextView) rowView.findViewById(R.id.variable);
        TextView AnDesc = (TextView) rowView.findViewById(R.id.desc);
        AnTitle.setTypeface(tf);
        AnDesc.setTypeface(tf);
        ImageView AnView = (ImageView) rowView.findViewById(R.id.imageView);
        AnTitle.setText(AnnouTitle.get(position));
        AnDesc.setText(AnnouDesc.get(position));
        AnView.setImageResource(AnnouImage.get(position));
        return rowView;
    }
}