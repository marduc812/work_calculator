package com.marduc812.workcalculator;

/**
 * Created by marduc on 18/09/16.
 */
public class Libraries_ad {

    String titles,owner,type,url;

    public Libraries_ad(String title, String owner, String type, String url)
    {
        this.titles = title;
        this.owner = owner;
        this.type = type;
        this.url = url;


    }

    public String getTitles()
    {
        return titles;
    }

    public String getOwner()
    {
        return owner;
    }

    public String getType()
    {
        return type;
    }

    public String getUrl()
    {
        return url;
    }

}
