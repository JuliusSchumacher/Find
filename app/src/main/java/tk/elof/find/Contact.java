package tk.elof.find;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.IDN;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class Contact {
    final User user;

    public Contact(String ID, User userIn) {
        Log.w("CONTACT", ID);

        id = ID;
        user = userIn;
        this.id = ID;

        DownloadContactTask task = new DownloadContactTask();
        task.execute();

        DownloadViewTask viewTask = new DownloadViewTask();
        viewTask.execute();
    }

    private void apply(String result) {
        String[] parts = result.split("_");
        this.name = parts[0];
        if(parts.length == 3) {
            this.time = parts[1];
            this.position = parts[2];
        }
        return;
    }

    private void applyViews(String result) {
       this.views = Boolean.valueOf(result);
        return;
    }

    public void setViews(boolean views) {
        this.views = views;
    }

    public void refresh() {
        DownloadContactTask task = new DownloadContactTask();
        task.execute();

        DownloadViewTask viewTask = new DownloadViewTask();
        viewTask.execute();
    }

    public class DownloadContactTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.w("BACK", "background-task " + id);
            try {
                String link = "http://apktest.site90.com/"
                        + "?intent=" + "view"
                        + "&token=" + user.token
                        + "&query=" + id;

                Log.w("BACK", link);


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.w("BACK", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                Log.w("BACK", e.toString());
                return "Failure";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            apply(result);
            return;
        }
    }

    public class DownloadViewTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.w("BACK", "background-task " + id);
            try {
                String link = "http://apktest.site90.com/"
                        + "?intent=" + "views"
                        + "&token=" + user.token
                        + "&query=" + id;

                Log.w("BACK", link);


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Log.w("BACK", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                Log.w("BACK", e.toString());
                return "Failure";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            applyViews(result);
            return;
        }
    }



    public String getID() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getPosition() {
        return this.position;
    }
    public String getTime() {
        return this.time;
    }
    public boolean getViews() {
        return this.views;
    }

    private String id;
    private String name;
    private String position;
    private String time;
    private boolean views;
}
