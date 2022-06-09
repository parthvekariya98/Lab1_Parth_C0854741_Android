package com.example.lab1_parth_c0854741_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lab1_parth_c0854741_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap google_map;
    private ActivityMapsBinding binding;

    //marker and marker list
    private Marker userMarker;
    List<Marker> markersList = new ArrayList();

    //permission request code and polygon lines
    private static final int REQUEST_CODE = 1;
    private static final int POLYGON_EDGES = 4;

    // location manager and listener declaration
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        google_map = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override public void onLocationChanged(Location location) {
                //set user location marker
                setUserMarker(location);
                lastLocation = location;
            }
        };

        if (!hasLocationPermission()) {
            requestPermissionForLocation();
        } else {
            startUpdatingLocation();
        }

        google_map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override public void onPolylineClick(@NonNull Polyline polyline) {
                List<LatLng> test = polyline.getPoints();
                float[] distance = new float[1];
                //show distance between two marker on polyline click
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude, distance);
                Toast.makeText(MapsActivity.this, "Distance between this two points is "
                        +distance[0], Toast.LENGTH_LONG).show();
            }
        });
        google_map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                //calculation for all four points of polygon
                List<LatLng> markerList = polygon.getPoints();
                float[] distanceBtnAAndB = new float[1];
                Location.distanceBetween(markerList.get(0).latitude,
                        markerList.get(0).longitude,
                        markerList.get(1).latitude,
                        markerList.get(1).longitude,
                        distanceBtnAAndB);

                float[] distanceBtnBAndC = new float[1];
                Location.distanceBetween(markerList.get(0).latitude,
                        markerList.get(0).longitude,
                        markerList.get(1).latitude,
                        markerList.get(1).longitude,
                        distanceBtnBAndC);

                float[] distanceBtnCAndD = new float[1];
                Location.distanceBetween(markerList.get(0).latitude,
                        markerList.get(0).longitude,
                        markerList.get(1).latitude,
                        markerList.get(1).longitude,
                        distanceBtnCAndD);

                float[] distanceBtnDAndA = new float[1];
                Location.distanceBetween(markerList.get(0).latitude,
                        markerList.get(0).longitude,
                        markerList.get(1).latitude,
                        markerList.get(1).longitude,
                        distanceBtnDAndA);

                float first = distanceBtnAAndB[0];
                float second = distanceBtnBAndC[0];
                float third = distanceBtnCAndD[0];
                float forth = distanceBtnDAndA[0];

                //sum of all four distance and display toast
                float total = first + second + third + forth;
                Toast.makeText(MapsActivity.this, "Total Distance(A-B-C-D) is " + total,
                        Toast.LENGTH_LONG).show();
            }
        });

        google_map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override public void onMapClick(@NonNull LatLng latLng) {
                if (markersList.size() == POLYGON_EDGES) {
                    //clear polygon on marker size 4
                    clearMapView();
                }

                //check size of marker list and add marker
                if (markersList.size() == 0){
                    //add first marker with title A
                    MarkerOptions options = new MarkerOptions().position(latLng).title("A");
                    markersList.add(google_map.addMarker(options));
                }else if (markersList.size() == 1){
                    //add second marker with title B
                    MarkerOptions options = new MarkerOptions().position(latLng).title("B");
                    markersList.add(google_map.addMarker(options));
                }else if (markersList.size() == 2){
                    //add third marker with title C
                    MarkerOptions options = new MarkerOptions().position(latLng).title("C");
                    markersList.add(google_map.addMarker(options));
                }else if (markersList.size() == 3){
                    //add forth marker with title D
                    MarkerOptions options = new MarkerOptions().position(latLng).title("D");
                    markersList.add(google_map.addMarker(options));
                }

                //draw polygon and connect lines on markers size 4
                if (markersList.size() == POLYGON_EDGES) {
                    drawPolygonOnMap();
                    drawPolyLineOnMap();
                }
            }

            private void clearMapView() {
                for (Marker marker: markersList) {
                    marker.remove();
                }
                markersList.clear();
                google_map.clear();
                setUserMarker(lastLocation);
            }

            private void drawPolyLineOnMap() {
                //add polyline for all four edges of polygon

                PolylineOptions line1 = new PolylineOptions().color(Color.RED).width(12)
                        .add(markersList.get(0).getPosition(), markersList.get(1).getPosition());
                line1.clickable(true);
                //zIndex for polyline view layer
                line1.zIndex(2F);
                google_map.addPolyline(line1);

                PolylineOptions line2 = new PolylineOptions().color(Color.RED).width(12)
                        .add(markersList.get(1).getPosition(), markersList.get(2).getPosition());
                line2.clickable(true);
                line2.zIndex(2F);
                google_map.addPolyline(line2);

                PolylineOptions line3 = new PolylineOptions().color(Color.RED).width(12)
                        .add(markersList.get(2).getPosition(), markersList.get(3).getPosition());
                line3.clickable(true);
                line3.zIndex(2F);
                google_map.addPolyline(line3);

                PolylineOptions line4 = new PolylineOptions().color(Color.RED).width(12)
                        .add(markersList.get(3).getPosition(), markersList.get(0).getPosition());
                line4.clickable(true);
                line4.zIndex(2F);
                google_map.addPolyline(line4);
            }

            private void drawPolygonOnMap() {
                //set 35% transparency and #00FF00(green) color with width 5 for polygon
                PolygonOptions options = new PolygonOptions().fillColor(0x3500FF00)
                        .strokeColor(Color.RED).strokeWidth(5);
                options.clickable(true);

                for (int i=0; i<POLYGON_EDGES; i++) {
                    options.add(markersList.get(i).getPosition());
                }
                //add polygon on map
                google_map.addPolygon(options);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            //check for user location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // check user location on every 5 seconds
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    private void startUpdatingLocation() {
        //check for user location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // check user location on every 5 seconds
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //set marker on current location
        setUserMarker(currentLocation);
    }

    private void setUserMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //set user marker title
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("My Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("User Location");
        userMarker = google_map.addMarker(options);
        //animate zoom to user location
        google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

    }

    //check for user location permission
    private void requestPermissionForLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}