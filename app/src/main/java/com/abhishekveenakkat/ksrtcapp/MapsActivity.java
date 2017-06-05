package com.abhishekveenakkat.ksrtcapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double la,lo;
    String Rid,Bname;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle bundle = getIntent().getExtras();
        Rid = bundle.getString("RouteID");
        Bname = bundle.getString("Bname");
        Toast.makeText(getApplicationContext(),Rid, Toast.LENGTH_LONG).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = new Intent(getApplicationContext(), LocationFetcher.class);
        i.putExtra("RouteID",Rid.toString());
        startService(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(this, LocationFetcher.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    la = bundle.getDouble("latitude");
                    lo = bundle.getDouble("longitude");
                    LatLng sydney = new LatLng(la, lo);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
                    mMap.clear();
                    // Flat markers will rotate when the map is rotated,
                    // and change perspective when the map is tilted.
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busico))
                            .position(sydney)
                            .flat(true)
                            .title(Bname)
                            .anchor(0.0f, 0.5f)
                            .rotation(0));
                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(sydney)
                            .zoom(13)
                            .bearing(90)
                            .build();
                    mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    getApplicationContext(), R.raw.style_map));
                    // Animate the change in camera view over 2 seconds
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            2000, null);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(41.889, -87.622);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(41.889, -87.622), 16));

    }
}
