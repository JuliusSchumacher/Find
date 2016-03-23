package tk.elof.find;

import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    public LocationUpdateService() {
    }

    User user = new User();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user.token = intent.getStringExtra("token");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStatusChanged(String string, int i, Bundle bundle) {
        return;
    }



    public class UpdateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.w("APP", "background-task");
            try {
                String link = "http://apktest.site90.com/"
                        + "?intent=" + "update"
                        + "&token=" + user.token
                        + "&pos=" + user.pos;

                Log.w("APP", link);


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.w("DOWN", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                Log.w("APP", e.toString());
                return "Failure";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            return;
        }
    }


}
