package com.example.charles.lab3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View view;
    private Context mContext;

    public CustomInfoAdapter(Context context) {
        mContext = context;
        view = LayoutInflater.from(context).inflate( R.layout.custom_info,null);


    }

    private void windowRendor(Marker marker, View view){
        String title = marker.getTitle();
        TextView textviewTitle = (TextView) view.findViewById( R.id.title );

        if (!title.equals( "" )){
            textviewTitle.setText( title );
        }


        String info = marker.getSnippet();
        TextView textviewSnippet = (TextView) view.findViewById( R.id.snippet );

        if (!info.equals( "" )){
            textviewSnippet.setText( info );
        }



    }

    @Override
    public View getInfoWindow(Marker marker) {
        windowRendor( marker,view );
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        windowRendor( marker,view );
        return view;
    }
}
