package com.amco.cidnews.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amco.cidnews.Utilities.DrawerItemNavBar;
import com.amco.cidnews.R;

import java.util.List;

public class DrawerAdapter extends ArrayAdapter {

        Context _context;
        public DrawerAdapter(Context context, List objects) {
            super(context, 0, objects);
            this._context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.items_navbar, null);
            }

            DrawerItemNavBar item = (DrawerItemNavBar) getItem(position);
            ImageView icon = (ImageView) convertView.findViewById(R.id.ic_avatar);
            TextView tipo = (TextView) convertView.findViewById(R.id.txt_tipo);

            icon.setImageResource(item.getIconID());
            tipo.setText(item.getString());
            return convertView;
        }
    }
