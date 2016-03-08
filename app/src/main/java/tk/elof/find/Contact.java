package tk.elof.find;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.IDN;
import java.net.URL;
import java.net.URLConnection;

public class Contact {
    public Contact(String ID, User userIn) {
        final User user = userIn;
         class DownloadContactTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                Log.w("APP", "background-task");
                try {
                    String link = "http://apktest.site90.com/"
                            + "?intent=" + user.intent
                            + "&token=" + user.token
                            + "&user=" + user.user
                            + "&pass=" + user.pass
                            + "&mail=" + user.mail
                            + "&number=" + user.number
                            + "&query=" + user.query
                            + "&pos=" + user.pos
                            + "&id=" + user.id
                            + "&edit=" + user.edit
                            + "&add=" + user.add;

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
                apply(result);
                return;
            }
        }

        user.intent = "view";
        user.query = ID;
        DownloadContactTask task = new DownloadContactTask();
        task.execute();

    }

    private void apply(String result) {
        String[] parts = result.split("_");
        this.name = parts[0];
        this.time = parts[1];
        this.position = parts[2];
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

    private String id;
    private String name;
    private String position;
    private String time;
}
