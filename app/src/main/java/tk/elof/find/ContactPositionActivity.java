package tk.elof.find;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactPositionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker contactMarker;
    Contact markerContact;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_position);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        user = new User();
        user.token = intent.getStringExtra("token");
        markerContact = new Contact(intent.getStringExtra("id"), user);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        String position = intent.getStringExtra("position");

        String[] latLong = position.split("/");

        float lat = Float.parseFloat(latLong[0]);
        float lon = Float.parseFloat(latLong[1]);

        Log.w("POS", "lat: " + lat + " long: " + lon);

        String timeStr = intent.getStringExtra("time");
        long time = Long.parseLong(timeStr);
        long currentTime = System.currentTimeMillis() / 1000;

        Log.w("POS", "time: " + time + " currentTime: " + currentTime);

        time = currentTime - time;

        LatLng contact = new LatLng(lat, lon);
        contactMarker = mMap.addMarker(new MarkerOptions()
                .position(contact)
                .title(intent.getStringExtra("name"))
                .snippet(time + " seconds ago"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(contact));

        contactMarker.showInfoWindow();

        updateMarker();
    }

    public void updateMarker() {

        markerContact.refresh();

        long lastTime = System.currentTimeMillis();
        long interval = 2000;
        while (true) {
            long thisTime = System.currentTimeMillis();
            if((thisTime - lastTime) >= interval) {
                break;
            }
        }

        String name = markerContact.getName();
        String position = markerContact.getPosition();
        String time = markerContact.getTime();

        Log.w("APP", "name: " + name + " position: " + position + " time: " + time);


        if (position != null) {
            String[] posArray = position.split("/");
            LatLng contact = new LatLng(Long.parseLong(posArray[0]), Long.parseLong(posArray[1]));

            long currentTime = System.currentTimeMillis();
            long timeAgo = Long.parseLong(time) - currentTime;

            contactMarker.setTitle(name);
            contactMarker.setSnippet(timeAgo + " seconds ago");
            contactMarker.setPosition(contact);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(contact));
        }
    }
}