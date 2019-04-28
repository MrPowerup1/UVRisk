package com.example.uvrisk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String APIKEY = "ab2c57df8757b2533797c8412820d3b9";
    /*
    URL FOR API CALL http://api.openweathermap.org/data/2.5/uvi?appid={appid}&lat={lat}&lon={lon}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        final TextView textView = findViewById(R.id.textView);
        final EditText lat = findViewById(R.id.editText);
        final EditText lon = findViewById(R.id.editText2);
        final TextView error = findViewById(R.id.Error);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                error.setVisibility(View.INVISIBLE);
                int latValue = 0;
                int lonValue = 0;
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
                int UV = findUVValue(latValue, lonValue);
                textView.setText("UV Risk: " + UV);
            }
        });
    }
    public int findUVValue(int latitude, int longitude) {
        return latitude + longitude;
    }

}
