package com.example.uvrisk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                    latValue = Integer.parseInt(lat.getText().toString());
                }
                catch (Exception e) {
                    error.setVisibility(View.VISIBLE);
                }
                try {
                    lonValue = Integer.parseInt(lon.getText().toString());
                }
                catch (Exception e) {
                    error.setVisibility(View.VISIBLE);
                }
                findUVValue(latValue, lonValue);
            }
        });
    }
    public void findUVValue(double latitude, double longitude) {
        try {
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
                        System.out.println(error);
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
            textView.setText("UV Risk: " + response.get("value").toString());
        } catch (JSONException e) {
            System.out.println("JSON error");
        }
    }

}
