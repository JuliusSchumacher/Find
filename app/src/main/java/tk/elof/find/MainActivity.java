package tk.elof.find;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    ListView listView;
    TextView textView;
    EditText searchEdit;
    Button searchButton;
    LinearLayout searchBar;
    LinearLayout searchResult;
    TextView searchResultText;
    final User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.noContacts);
        searchEdit = (EditText) findViewById(R.id.search_edit_frame);
        searchButton = (Button) findViewById(R.id.search_button);
        searchBar = (LinearLayout) findViewById(R.id.search_bar);
        searchResult = (LinearLayout) findViewById(R.id.search_result);
        searchResultText = (TextView) findViewById(R.id.search_result_name);


        Intent intent = getIntent();
        user.token = intent.getStringExtra("token");

        DownloadListTask task = new DownloadListTask();
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
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                try {
                    FileOutputStream fOut = openFileOutput("token", MODE_PRIVATE);

                    fOut.write("".getBytes());
                    fOut.close();
                } catch (Exception e) {
                    Log.w("APP", e.toString());
                }

                Intent intent = new Intent(this, Login.class);
                startActivity(intent);

                return true;
            case R.id.action_search:
                searchBar.setVisibility(View.VISIBLE);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public class DownloadListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.w("APP", "background-task");
            try {
                String link = "http://apktest.site90.com/"
                        + "?intent=" + "list"
                        + "&token=" + user.token;

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
        if (Objects.equals(user.result, "Failure") || user.result.length() < 1) {
            Log.w("RESULT", user.result);
            textView.setVisibility(View.VISIBLE);
            return;
        } else {
            String[] contactIDs = user.result.split("_");

            final Contact[] contacts = new Contact[contactIDs.length];

            for(int i = 0; i < contactIDs.length; i++) {
                if(contactIDs[i].length() > 0) {
                    contacts[i] = new Contact(contactIDs[i], user);
                }
            }

            try {
                Thread.sleep(1000 * contactIDs.length);
            } catch (Exception e) {
                Log.w("APP", e.toString());
            }

            final ContactAdapter adapter = new ContactAdapter(this, R.layout.contact_list_item, contacts, user);

            listView.setAdapter(adapter);
        }
    }

    public void search(View view) {

        final String query = searchEdit.getText().toString();

        class SearchTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                Log.w("APP", "background-task");
                try {
                    String link = "http://apktest.site90.com/"
                            + "?intent=" + "search"
                            + "&query=" + query;

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
                user.id = result;
                searchResult();
                return;
            }
        }

        SearchTask task = new SearchTask();
        task.execute();

    }

    void searchResult() {
        class ViewTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                Log.w("APP", "background-task");
                try {
                    String link = "http://apktest.site90.com/"
                            + "?intent=" + "view"
                            + "&token=" + user.token
                            + "&query=" + user.result;

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
                viewResult();
                return;
            }
        }

        ViewTask task = new ViewTask();
        task.execute();
    }

    void viewResult() {
        String[] result = user.result.split("_");

        Log.w("APP", result[0]);
        searchBar.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);
        searchResultText.setText(result[0]);
    }

    public void add(View view) {
        class AddTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                Log.w("APP", "background-task");
                try {
                    String link = "http://apktest.site90.com/"
                            + "?intent=" + "contact"
                            + "&token=" + user.token
                            + "&edit=" + "contact"
                            + "&add=" + "true"
                            + "&id=" + user.id;

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

        AddTask task = new AddTask();
        task.execute();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("token", user.token);
        startActivity(intent);
    }
}
