package com.amco.cidnews.Utilities;

public class DrawerItemNavBar {
    private String txt;
    private  int iconId;

    public DrawerItemNavBar(String txt, int iconId){


        this.txt = txt;
        this.iconId = iconId;
    }

    public String getString(){
        return txt;
    }

    public void setString(String newTxt){
        this.txt = newTxt;
    }

    public int getIconID(){
        return iconId;
    }

    public void setIconId(int newID){
        this.iconId = newID;
    }


}
