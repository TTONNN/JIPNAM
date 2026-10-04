package com.example.jibnam;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);

        Button greenButton = new Button(this);
        greenButton.setText("น้อย");
        greenButton.setTextColor(Color.WHITE);
        greenButton.setBackgroundColor(Color.GREEN);

        Button yellowButton = new Button(this);
        yellowButton.setText("กลาง");
        yellowButton.setTextColor(Color.BLACK);
        yellowButton.setBackgroundColor(Color.YELLOW);

        Button redButton = new Button(this);
        redButton.setText("มาก");
        redButton.setTextColor(Color.WHITE);
        redButton.setBackgroundColor(Color.RED);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 150);



        params.setMargins(0, 30, 0, 30);

        layout.addView(greenButton, params);
        layout.addView(yellowButton, params);
        layout.addView(redButton, params);

        setContentView(layout);
    }
}