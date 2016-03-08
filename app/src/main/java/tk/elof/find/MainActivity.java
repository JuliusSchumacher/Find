package tk.elof.find;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);

        user.intent = "search";
        user.query = "admin";
        DownloadPageTask task = new DownloadPageTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DownloadPageTask extends AsyncTask<String, Void, String> {
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
            user.result = result;
            taskResult();
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void taskResult() {
        if (Objects.equals(user.result, "Failure")) {
            Log.w("RESULT", "Failure");
        } else {
            String[] values = new String[] {"1", "2", "3", user.result};

            final ArrayList<String> list = new ArrayList<String>();

            for(int i = 0; i < values.length; i++) {
                list.add(values[i]);
            }

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    final String item = (String) parent.getItemAtPosition(position);
                    view.animate().setDuration(2000).alpha(0)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    list.remove(item);
                                    adapter.notifyDataSetChanged();
                                    view.setAlpha(1);
                                }
                            });
                }

            });
        }
    }
}
