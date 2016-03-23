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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_position);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        Marker contactMarker = mMap.addMarker(new MarkerOptions()
                .position(contact)
                .title(intent.getStringExtra("name"))
                .snippet(time + " seconds ago"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(contact));

        contactMarker.showInfoWindow();
    }
}
