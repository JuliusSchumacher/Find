package tk.elof.find;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ContactAdapter(Context context, int resource, Contact[] contacts, User user) {
        super(context, resource, contacts);
        this.user = user;
        Log.w("ADAPT", "INIT");
    }
    private User user;


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if(view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.contact_list_item, null);
        }

        final Contact c = getItem(i);

        if(c != null) {
            TextView name = (TextView) view.findViewById(R.id.Name);
            TextView position = (TextView) view.findViewById(R.id.Position);
            Switch viewSwitch = (Switch) view.findViewById(R.id.CanView);

            name.setText(c.getName());
            position.setText(c.getPosition());
            viewSwitch.setChecked(c.getViews());

            viewSwitch.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final String bool;
                    if(c.getViews()) {
                        bool = "false";
                    } else {
                        bool = "true";
                    }

                    class SetViewTask extends AsyncTask<String, Void, String> {
                        @Override
                        protected String doInBackground(String... urls) {
                            Log.w("BACK", "background-task ");
                            try {
                                String link = "http://apktest.site90.com/"
                                        + "?intent=" + "contact"
                                        + "&token=" + user.token
                                        + "&edit=" + "view"
                                        + "&add=" + bool
                                        + "&id=" + c.getID();

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
                            return;
                        }
                    }

                    SetViewTask task = new SetViewTask();
                    task.execute();

                    if(c.getViews()) {
                        c.setViews(false);
                    } else {
                        c.setViews(true);
                    }
                }
            });
        }

        return view;
    }
}

