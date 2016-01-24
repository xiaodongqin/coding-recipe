package codingrecipe.com.reciperecommender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The project is located locally:
 * C:\Users\Administrator\AndroidStudioProjects\RecipeRecommender
 *
 * # Double check the current path
 * git remote show origin
 * git st
 * git diff
 * git commit .
 */
/**
 * If use custom list adapter, then it should not extend from ListActivity.
 */
public class MainActivity extends Activity {
    private static final String url =
            "http://fun-chinese-cooking.blogspot.com/feeds/posts/default?alt=json-in-script";

    private List<HashMap<String,String>> recipeHashMapList = new ArrayList<HashMap<String,String>>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://codingrecipe.com.reciperecommender/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://codingrecipe.com.reciperecommender/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class LoadRecipeAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler serviceHandler = new ServiceHandler();
            String jsonStr = serviceHandler.makeServiceCall(url.ServiceHandler.GET);

            // Deal with the recipe feed result from google blogger api
            int startIndex = jsonStr.indexOf('(') + 1;
            int endIndex = jsonStr.length() - 2;
            jsonStr = jsonStr.substring(startIndex, endIndex);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONObject feedJsonObject = jsonObject.getJSONObject("feed");
                    JSONArray entryJsonArray = feedJsonObject.getJSONArray("entry");

                    for (int i = 0; i < 3; i++) {
                        JSONObject entryJSONObject = entryJsonArray.getJSONObject(i);
                        JSONArray categoryJSONArray = entryJSONObject.getJSONArray("category");
                        JSONObject titleJsonObject = entryJSONObject.getJSONObject("title");

                        String recipeTitle = titleJsonObject.getString("$t");
                        JSONArray recipeLinkJsonArray = entryJSONObject.getJSONArray("link");
                        JSONObject recipeLinkJsonObject = recipeLinkJsonArray.getJSONObject(1);
                        String recipeLink = recipeLinkJsonObject.getString("href");


                        // Cannot declare as Map<String, String>, it does not match the recipeHashMapList declaration.
                        HashMap<String, String> recipeHashMap = new HashMap<>();
                        recipeHashMap.put("title", recipeTitle);
                        recipeHashMap.put("Link", recipeLink);

                        MainActivity.this.recipeHashMapList.add(recipeHashMap);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                adapter.notifyDataSetChanged();

            }
        }
    }


}
