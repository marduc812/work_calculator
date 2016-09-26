package com.marduc812.workcalculator;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by marduc on 2/25/15.
 */
public class DatasAdapter extends RecyclerView.Adapter<DatasAdapter.ViewHolder> {

    private Stats_ad[] mDataset;
    private ClickListener clickListener;
    public Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DatasAdapter(Stats_ad[] myDataset) {
        this.mDataset = myDataset;
        this.context=context;



    }



    @Override
    public DatasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_card_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final Context context = null;
        // each data item is just a string in this case

        public TextView title,value;
        public CardView cardView;
        //Typeface tf;


        public ViewHolder(View v) {
            super(v);
            Typeface tf = Typeface.createFromAsset(v.getContext().getAssets(),"helveticaneue.ttf");


            title = (TextView) v.findViewById(R.id.title);
            value = (TextView) v.findViewById(R.id.desc);

            title.setTypeface(tf);
            value.setTypeface(tf);

            cardView = (CardView) v.findViewById(R.id.card_view);
            //cardView.setPreventCornerOverlap(false);


        }


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(mDataset[position].getTitles());
        holder.value.setText(mDataset[position].getValues());


        /*holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Info button clicked on position: " + position);
                Toast.makeText(v.getContext(),"Info Item Clicked",Toast.LENGTH_SHORT).show();
            }
        });*/
    }
    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public interface ClickListener
    {
        public void itemClicked(View view, int pos);
    }
}

