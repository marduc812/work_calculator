package com.marduc812.workcalculator;

/**
 * Created by marduc on 2/25/15.
 */
public class Work_Data {
    int image;
    long start;
    String desc;

    public Work_Data(long start , String desc , int shape )
    {
        this.start = start;
        this.desc = desc;
        this.image = shape;


    }

    public long getStart() {return start;}

    public String getDesk() {return desc;}

    public int getImage() {return image;}

    public void setImageID()
    {
        this.image = image;
    }
}
