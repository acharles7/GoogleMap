package com.example.charles.lab3;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<PlaceDetail> cafelist = new ArrayList<PlaceDetail>(  );

    public RecyclerViewAdapter(ArrayList<PlaceDetail> cafelist) {
        this.cafelist = cafelist;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int i) {
        Log.d("recycle", "in recycleview");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cafe, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.name.setText( cafelist.get( i ).getName() );
        viewHolder.rating.setText(String.valueOf( cafelist.get( i ).getRating()) );
        viewHolder.extra.setText( cafelist.get( i ).getAddress() );

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView rating;
        TextView extra;
        ConstraintLayout parent_Layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById( R.id.name );
            rating = (TextView)itemView.findViewById( R.id.rating );
            extra = (TextView)itemView.findViewById( R.id.extra );
            parent_Layout = itemView.findViewById(R.id.parent_layout);




        }
    }

    @Override
    public int getItemCount() {


        return cafelist.size();
    }
}
