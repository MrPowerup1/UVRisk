package com.example.uvrisk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient client;
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
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
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
                    if (latValue == 0 && lonValue ==0) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(LOG,"HERE");
                            return;
                        }
                        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    findUVValue(location.getLatitude(), location.getLongitude());
                                }
                            }
                        });
                    }
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
            suggest(Double.parseDouble(response.get("value").toString()));
        } catch (JSONException e) {
            Log.d(LOG,"JSON error");
        }
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
    private void suggest(double UV) {
        TextView textView = findViewById(R.id.suggestion);
        if (UV < 2.9) {
            textView.setTextColor(Color.GREEN);
            textView.setText("LOW: Wear sunglasses on bright days. If you burn easily, cover up and use broad spectrum SPF 30+ sunscreen. Bright surfaces, such as sand, water, and snow, will increase UV exposure.");
        } else if (UV < 5.9) {
            textView.setTextColor(Color.GREEN);
            textView.setText("MEDIUM: Stay in shade near midday when the sun is strongest. I    f outdoors, wear sun protective clothing, a wide-brimmed hat, and UV-blocking sunglasses. Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days, and after swimming or sweating. Bright surfaces, such as sand, water, and snow, will increase UV exposure.");
        } else if (UV < 7.9) {
            textView.setTextColor(Color.RED);
            textView.setText("HIGH: Reduce time in the sun between 10 a.m. and 4 p.m. If outdoors, seek shade and wear sun protective clothing, a wide-brimmed hat, and UV-blocking sunglasses. Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days, and after swimming or sweating. Bright surfaces, such as sand, water, and snow, will increase UV exposure.");
        } else if (UV < 10.9) {
            textView.setTextColor(Color.RED);
            textView.setText("VERY HIGH: Minimize sun exposure between 10 a.m. and 4 p.m. If outdoors, seek shade and wear sun protective clothing, a wide-brimmed hat, and UV-blocking sunglasses. Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days, and after swimming or sweating. Bright surfaces, such as sand, water, and snow, will increase UV exposure.");
        } else {
            textView.setTextColor(Color.RED);
        textView.setText("EXTREME: Try to avoid sun exposure between 10 a.m. and 4 p.m. If outdoors, seek shade and wear sun protective clothing, a wide-brimmed hat, and UV-blocking sunglasses. Generously apply broad spectrum SPF 30+ sunscreen every 2 hours, even on cloudy days, and after swimming or sweating. Bright surfaces, such as sand, water, and snow, will increase UV exposure.");
        }
        }


        }
