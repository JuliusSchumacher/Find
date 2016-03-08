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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;



public class Login extends AppCompatActivity {
    User user;
    EditText username;
    EditText password;
    EditText mail;
    EditText number;
    TextView failureText;
    Button login;
    Button create;
    Button createTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User();
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        mail = (EditText) findViewById(R.id.mail);
        number = (EditText) findViewById(R.id.number);
        login = (Button) findViewById(R.id.loginButton);
        create = (Button) findViewById(R.id.create);
        failureText = (TextView) findViewById(R.id.failureText);
        createTask = (Button) findViewById(R.id.createTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(View view) {
        failureText.setVisibility(View.GONE);
        user.intent = "login";
        user.user = username.getText().toString();
        user.pass = password.getText().toString();

        DownloadPageTask task = new DownloadPageTask();

        task.execute();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void taskResult() {
        if (Objects.equals(user.result, "Failure")) {
            Log.w("RESULT", "Failure");

            failureText.setVisibility(View.VISIBLE);
        } else {
            if (user.intent == "login") {
                user.token = user.result;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);


            } else if (user.intent == "create") {
                mail.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
                createTask.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
            }
        }
    }

    public void create(View view) {
        mail.setVisibility(View.VISIBLE);
        number.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
        createTask.setVisibility(View.VISIBLE);
        create.setVisibility(View.GONE);
    }

    public void createTask(View view) {
        failureText.setVisibility(View.GONE);

        user.intent = "create";
        user.user = username.getText().toString();
        user.pass = password.getText().toString();
        user.mail = mail.getText().toString();
        user.number = number.getText().toString();

        DownloadPageTask task = new DownloadPageTask();

        task.execute();
    }

}
