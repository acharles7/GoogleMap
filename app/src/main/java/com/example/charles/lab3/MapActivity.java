package com.example.charles.lab3;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.charles.lab3.AppConfig.CAFE_RESTAURANT_ID;
import static com.example.charles.lab3.AppConfig.GEOMETRY;
import static com.example.charles.lab3.AppConfig.ICON;
import static com.example.charles.lab3.AppConfig.LATITUDE;
import static com.example.charles.lab3.AppConfig.LOCATION;
import static com.example.charles.lab3.AppConfig.LONGITUDE;
import static com.example.charles.lab3.AppConfig.NAME;
import static com.example.charles.lab3.AppConfig.OK;
import static com.example.charles.lab3.AppConfig.PLACE_ID;
import static com.example.charles.lab3.AppConfig.PROXIMITY_RADIUS;
import static com.example.charles.lab3.AppConfig.RATING;
import static com.example.charles.lab3.AppConfig.REFERENCE;
import static com.example.charles.lab3.AppConfig.STATUS;
import static com.example.charles.lab3.AppConfig.VICINITY;
import static com.example.charles.lab3.AppConfig.ZERO_RESULTS;





public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds( new LatLng( -38,-89 ),new LatLng( 44,129 ) );
    private Marker marker;

    private AutoCompleteTextView searchText;
    private ImageView gpsImage;
    private ImageView info;
    private ImageView places;
    private ImageView cafe;
    private ImageView cafelist_btn;


    private GoogleApiClient googleApiClient;
    private PlaceInfo placeInfo;
    private LatLng clatlng;
    private ArrayList<PlaceDetail> cafelist = new ArrayList<PlaceDetail>(  );


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );

        searchText = (AutoCompleteTextView) findViewById( R.id.search );
        gpsImage = (ImageView) findViewById( R.id.gps_btn );
        info = (ImageView) findViewById( R.id.info_btn );
        cafe = (ImageView) findViewById( R.id.cafe_btn );
        cafelist_btn = (ImageView)findViewById( R.id.cafelist );


        if(isServicesOK()){
            getLocationPermission();
        }

    }


    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void gLocate(){
        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder( MapActivity.this );
        List<Address> list = new ArrayList<>(  );
        try {
            list = geocoder.getFromLocationName( searchString,1 );
        }catch (IOException e){
            Log.e(  TAG,"IO Error" );
        }

        if(list.size()>0){
            Address address = list.get( 0 );
            Log.d( TAG, "gLocate: Location"+address.toString() );
            moveCamera(new LatLng( address.getLatitude(),address.getLongitude()),10f,address.getAddressLine( 0 ));
        }
    }
    private void init(){

        googleApiClient = new GoogleApiClient.Builder( this ).addApi(Places.GEO_DATA_API ).
                addApi( Places.PLACE_DETECTION_API).
                enableAutoManage( this,this ).build();
        searchText.setOnItemClickListener( onItemClickListener );
        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, googleApiClient, LAT_LNG_BOUNDS,null  );
        searchText.setAdapter(placeAutoCompleteAdapter);
        searchText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENDCALL){
                    gLocate();
                    return true;
                }
                return false;
            }
        } );
        gpsImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        } );

        info.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(marker.isInfoWindowShown()){
                        marker.hideInfoWindow();
                    }
                    else {
                        marker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.d( TAG, "Null Pointer"+e.getMessage() );
                }
            }
        } );

        cafelist_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this,RecyclerActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("CAFELIST",(Serializable)cafelist);
                intent.putExtra("BUNDLE",args);
                startActivity( intent );
            }
        } );
        hKeyBoard();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.
                        getPlaceById( googleApiClient,place.getId() );
                placeBufferPendingResult.setResultCallback( updatePlaceDetailsCallback );
            }
        }
    }
    private void initMap() {
        Log.d( TAG, "initMap: initializing map" );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );

        mapFragment.getMapAsync( MapActivity.this );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText( this, "Initializing Map", Toast.LENGTH_SHORT ).show();
        Log.d( TAG, "onMapReady: map is ready" );
        mMap = googleMap;


        if (mLocationPermissionsGranted) {
            getCurrentLocation();
            //Toast.makeText( this, "Current Map is Ready", Toast.LENGTH_SHORT ).show();
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            mMap.setMyLocationEnabled( true );
            mMap.getUiSettings().setMyLocationButtonEnabled( false );

            init();
        }
    }

    public void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                            { Log.d(TAG, "onComplete: foundlocation");
                                Location currentLocation = (Location) task.getResult();
                                if(currentLocation!= null){
                                    double latitude = currentLocation.getLatitude();
                                    double longitude = currentLocation.getLongitude();
                                    clatlng = new LatLng( latitude,longitude );
                                    Log.d(TAG, "onComplete: locationworking"+latitude+"and"+longitude);
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            15f,"Current Location");
                                    Log.d(TAG, "onComplete: camera");

                                }
                                else{
                                    Log.d(TAG, "onComplete: cccvlocationnotworking");
                                }
                            }
                        else
                            {
                            Log.d(TAG, "onComplete: Lacation not found Failed");
                            Toast.makeText(MapActivity.this, "not get Current loc", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

        } catch(SecurityException e){
                Log.e(TAG, "getCurrentLocation: Security Exe" + e.getMessage());
            }

        }
    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        mMap.clear();
        mMap.setInfoWindowAdapter( new CustomInfoAdapter( MapActivity.this ) );

        if (placeInfo != null){
            try{
                Log.d(TAG, "Placeinfo have information " );
                String info = "Address:   "+placeInfo.getAddress()+ "\n"+
                        "Phone Number:   "+placeInfo.getPhoneNumber()+ "\n"+
                "Website:   "+placeInfo.getWebsite()+ "\n"+
                "Rating:   "+placeInfo.getRating()+ "\n";

                MarkerOptions markerOptions = new MarkerOptions().position( latLng ).title( placeInfo.getName() ).snippet( info );

                marker = mMap.addMarker( markerOptions );
                Log.d( TAG,"Place info done");

            }catch (NullPointerException e){
                Log.d( TAG,"Place info null"+ e.getMessage() );
            }
        }else {
            mMap.addMarker( new MarkerOptions().position( latLng ) );
        }
        hKeyBoard();

    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals( "Current Location" )){
            MarkerOptions markerOptions = new MarkerOptions().position( latLng ).title(title);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            markerOptions.zIndex(1.0f);
            mMap.addMarker( markerOptions );

        }
        hKeyBoard();

    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
                Log.d(TAG, "getLocationPermission: location permissions granted");
            }
            else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "getLocationPermission:location permissions denied");
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d(TAG, "getLocationPermission:location permissions denied");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: asd called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;

                    initMap();
                }
            }
        }
    }

    private void hKeyBoard(){
        InputMethodManager imm = (InputMethodManager) MapActivity.this.getSystemService( Activity.INPUT_METHOD_SERVICE );
        View view = MapActivity.this.getCurrentFocus();
        if (view == null){
            view = new View( MapActivity.this );
        }
        imm.hideSoftInputFromWindow( view.getWindowToken(),0 );

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hKeyBoard();

            final AutocompletePrediction item = placeAutoCompleteAdapter.getItem( position );
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById( googleApiClient,placeId );
            placeBufferPendingResult.setResultCallback( updatePlaceDetailsCallback );
        }
    };

    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if (!places.getStatus().isSuccess()){
                Log.d( TAG,"Not found" );
                places.release();
                return;

            }
            final Place place = places.get( 0 );


            try {
                placeInfo = new PlaceInfo();
                placeInfo.setName( place.getName().toString() );
                placeInfo.setAddress( place.getAddress().toString() );
                placeInfo.setId( place.getId());
                placeInfo.setPhoneNumber( place.getName().toString() );
                placeInfo.setRating( (double) place.getRating() );
                Log.d(TAG, "onResult: rating: " + place.getRating());
                placeInfo.setLatLng( place.getLatLng() );
                placeInfo.setWebsite( place.getWebsiteUri() );
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

            }catch (NullPointerException e){
                Log.d(TAG, "onResult: notsure");
                Log.e( TAG, "onResult:NullPointerrrrr"+e.getMessage());


            }
            moveCamera( new LatLng( place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude ),15f,placeInfo);

            places.release();



        }
    };

    public void findCafe(View view){
        StringBuilder stringBuilder = new StringBuilder( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        String locationStr = clatlng.latitude +","+clatlng.longitude ;
        stringBuilder.append( "location="+ locationStr);
        stringBuilder.append( "&radius=").append( PROXIMITY_RADIUS );
        stringBuilder.append( "&type="+"cafe");
        stringBuilder.append("&sensor=true");
        stringBuilder.append( "&key="+getResources().getString( R.string.new_key ));

        String url = stringBuilder.toString();
        JsonObjectRequest request = new JsonObjectRequest(url,
        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {

                Log.i(TAG, "onResponse: Result= " + result.toString());
                try {
                    parseLocationResult(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }



    private void parseLocationResult(JSONObject result) throws JSONException {

        String id, place_id, placeName = "", reference, icon, ivicinity = "";
        Double placeRating = 0.0;
        double latitude, longitude;

        JSONArray jsonArray = result.getJSONArray("results");

        if (result.getString(STATUS).equalsIgnoreCase(OK)) {

            mMap.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject place = jsonArray.getJSONObject(i);

                id = place.getString(CAFE_RESTAURANT_ID);
                place_id = place.getString(PLACE_ID);
                if (!place.isNull(NAME)) {
                    placeName = place.getString(NAME);
                }
                if (!place.isNull(VICINITY)) {
                    ivicinity = place.getString(VICINITY);
                }
                if(!place.isNull( RATING )){
                    placeRating = place.getDouble( RATING );
                }


                latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                        .getDouble(LATITUDE);
                longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                        .getDouble(LONGITUDE);
                reference = place.getString(REFERENCE);
                icon = place.getString(ICON);




                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(latitude, longitude);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + ivicinity);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                PlaceDetail cafe= new PlaceDetail(placeName,ivicinity,placeRating);

                cafelist.add( cafe );
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                mMap.addMarker(markerOptions);
            }


            Toast.makeText(getBaseContext(), jsonArray.length() + " Cafe Found!",
                    Toast.LENGTH_LONG).show();
        } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
            Toast.makeText(getBaseContext(), "No Cafe Found in 4 Miles Radius!!!",
                    Toast.LENGTH_LONG).show();
        }

    }





}

