package tk.elof.find;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {
    public LocationUpdateService() {
    }

    User user = new User();
    GoogleApiClient googleApiClient;
    Location location;
    LocationRequest locationRequest;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        try {
            FileInputStream fIn = openFileInput("token");
            int c;
            String temp = "";
            while ((c = fIn.read()) != -1 ) {
                temp = temp + Character.toString((char)c);
            }
            fIn.close();

            user.token = temp;
        } catch (Exception e) {
            Log.w("SERVICE", e.toString());
        }

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        Toast.makeText(LocationUpdateService.this, "Started Service", Toast.LENGTH_SHORT).show();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        String locationString = String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude());
        user.pos = locationString;
        UpdateTask task = new UpdateTask();
        task.execute();
    }

    @Override
    public void onConnected(Bundle bundle) {
        int update_interval = 5000;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(update_interval);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        String locationString = String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude());
        user.pos = locationString;
        UpdateTask task = new UpdateTask();
        task.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public class UpdateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.w("SERVICE", "background-task");
            try {
                String link = "http://apktest.site90.com/"
                        + "?intent=" + "update"
                        + "&token=" + user.token
                        + "&pos=" + user.pos;

                Log.w("SERVICE", link);


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.w("SERVICE", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                Log.w("SERVICE", e.toString());
                return "Failure";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            return;
        }
    }




}

