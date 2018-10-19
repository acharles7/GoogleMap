package com.example.charles.lab3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity  {
    private ArrayList<PlaceDetail> cafelist = new ArrayList<PlaceDetail>(  );
    private String arraylist;
    private Gson gson;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_recycler );
        //Toolbar toolbar = findViewById( R.id.toolbar );
        //setSupportActionBar( toolbar );
        Bundle bundle = getIntent().getBundleExtra( "BUNDLE" );
        if(bundle != null){

            cafelist = (ArrayList<PlaceDetail>) bundle.getSerializable("CAFELIST");
            //cafelist = gson.fromJson(temp, type);
        }

        RecyclerView recyclerView = findViewById( R.id.recycle_view );
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(  cafelist);
        recyclerView.setAdapter( adapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );


    }
}
