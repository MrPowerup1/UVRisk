package com.example.uvrisk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String APIKEY = "ab2c57df8757b2533797c8412820d3b9";
    private static RequestQueue requestQueue;
    private static String LOG = "UVRISK";
    private static final String nonIntegerError = "Error: Make sure your Latitude and Longitude are numbers";
    /*
    URL FOR API CALL http://api.openweathermap.org/data/2.5/uvi?appid={appid}&lat={lat}&lon={lon}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        final EditText lat = findViewById(R.id.editText);
        final EditText lon = findViewById(R.id.editText2);
        final TextView error = findViewById(R.id.Error);
        error.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                error.setVisibility(View.INVISIBLE);
                double latValue = 0;
                double lonValue = 0;
                try {
                    latValue = Double.parseDouble(lat.getText().toString());
                }
                catch (Exception e) {
                    error.setText(nonIntegerError);
                    error.setVisibility(View.VISIBLE);
                }
                try {
                    lonValue = Double.parseDouble(lon.getText().toString());
                }
                catch (Exception e) {
                    error.setText(nonIntegerError);
                    error.setVisibility(View.VISIBLE);
                }
                if (latValue > 90 || latValue < -90) {
                    error.setText("Error: Latitude must be between -90 and 90");
                    error.setVisibility(View.VISIBLE);
                } else if (lonValue > 180 || lonValue < -180) {
                    error.setText("Error: Longitude must be between -180 and 180");
                    error.setVisibility(View.VISIBLE);
                }
                else {
                    findUVValue(latValue, lonValue);
                }
            }
        });
    }
    public void findUVValue(double latitude, double longitude) {
        try {
            Log.d(LOG, "here");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://api.openweathermap.org/data/2.5/uvi?appid=" + APIKEY + "&lat=" + latitude + "&lon=" + longitude,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            apiCallDone(response);
                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG, error.toString());
                    }
                });
            jsonObjectRequest.setShouldCache(false);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void apiCallDone(JSONObject response) {
        try {
            TextView textView = findViewById(R.id.textView);
            Log.d(LOG,response.get("value").toString());
            textView.setText("UV Risk: " + response.get("value").toString());
        } catch (JSONException e) {
            Log.d(LOG,"JSON error");
        }
    }

}
