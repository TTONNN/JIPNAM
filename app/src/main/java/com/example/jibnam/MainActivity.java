package com.example.jibnam;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // หน้าหลัก
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.setPadding(30, 30, 30, 30);
        mainLayout.setBackgroundColor(Color.rgb(240, 248, 250));

        // =========================
        // ข้อความ "น้อย"
        // =========================
        TextView textLow = createTextView("น้อย", Color.rgb(120, 220, 70));
        mainLayout.addView(textLow);

        // =========================
        // ข้อความ "กลาง"
        // =========================
        TextView textMedium = createTextView("กลาง", Color.YELLOW);
        mainLayout.addView(textMedium);

        // =========================
        // ข้อความ "มาก"
        // =========================
        TextView textHigh = createTextView("มาก", Color.RED);
        mainLayout.addView(textHigh);


        // ช่องว่าง
        addSpace(mainLayout, 50);


        // =========================
        // ปุ่มสี 3 ปุ่ม
        // =========================
        LinearLayout colorLayout = new LinearLayout(this);
        colorLayout.setOrientation(LinearLayout.HORIZONTAL);
        colorLayout.setGravity(Gravity.CENTER);

        Button greenButton = createColorButton(Color.rgb(120, 220, 70));
        Button yellowButton = createColorButton(Color.YELLOW);
        Button redButton = createColorButton(Color.RED);

        colorLayout.addView(greenButton);
        colorLayout.addView(yellowButton);
        colorLayout.addView(redButton);

        mainLayout.addView(colorLayout);


        // =========================
        // กำหนดการทำงานปุ่มสี
        // =========================

        greenButton.setOnClickListener(v -> {
            Toast.makeText(this, "เลือก น้อย", Toast.LENGTH_SHORT).show();
        });

        yellowButton.setOnClickListener(v -> {
            Toast.makeText(this, "เลือก กลาง", Toast.LENGTH_SHORT).show();
        });

        redButton.setOnClickListener(v -> {
            Toast.makeText(this, "เลือก มาก", Toast.LENGTH_SHORT).show();
        });


        // ช่องว่าง
        addSpace(mainLayout, 50);


        // =========================
        // หัวข้อเวลาออกกำลังกาย
        // =========================
        TextView title = new TextView(this);
        title.setText("เวลาออกกำลังกาย");
        title.setTextSize(22);
        title.setTextColor(Color.BLACK);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        mainLayout.addView(title);


        // ช่องว่าง
        addSpace(mainLayout, 20);


        // =========================
        // ช่องกรอกเวลา
        // =========================
        LinearLayout timeLayout = new LinearLayout(this);
        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeLayout.setGravity(Gravity.CENTER);

        EditText hourInput = new EditText(this);
        hourInput.setHint("");
        hourInput.setInputType(2);
        hourInput.setGravity(Gravity.CENTER);
        hourInput.setTextSize(18);

        GradientDrawable hourBackground = new GradientDrawable();
        hourBackground.setColor(Color.rgb(245, 245, 245));
        hourBackground.setStroke(2, Color.BLACK);
        hourBackground.setCornerRadius(8);
        hourInput.setBackground(hourBackground);

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(70, 55);

        timeLayout.addView(hourInput, inputParams);


        // ข้อความ "ชั่วโมง"
        TextView hourText = new TextView(this);
        hourText.setText(" ชั่วโมง ");
        hourText.setTextSize(16);
        hourText.setTextColor(Color.BLACK);
        hourText.setGravity(Gravity.CENTER);

        timeLayout.addView(hourText);


        // ช่องนาที
        EditText minuteInput = new EditText(this);
        minuteInput.setInputType(2);
        minuteInput.setGravity(Gravity.CENTER);
        minuteInput.setTextSize(18);

        GradientDrawable minuteBackground = new GradientDrawable();
        minuteBackground.setColor(Color.rgb(245, 245, 245));
        minuteBackground.setStroke(2, Color.BLACK);
        minuteBackground.setCornerRadius(8);
        minuteInput.setBackground(minuteBackground);

        timeLayout.addView(minuteInput, inputParams);


        // ข้อความ "นาที"
        TextView minuteText = new TextView(this);
        minuteText.setText(" นาที");
        minuteText.setTextSize(16);
        minuteText.setTextColor(Color.BLACK);
        minuteText.setGravity(Gravity.CENTER);

        timeLayout.addView(minuteText);

        mainLayout.addView(timeLayout);


        // ช่องว่าง
        addSpace(mainLayout, 25);


        // =========================
        // ปุ่ม Submit
        // =========================
        Button submitButton = new Button(this);
        submitButton.setText("submit");
        submitButton.setTextSize(16);
        submitButton.setTextColor(Color.WHITE);

        GradientDrawable submitBackground = new GradientDrawable();
        submitBackground.setColor(Color.BLACK);
        submitBackground.setCornerRadius(40);
        submitButton.setBackground(submitBackground);

        LinearLayout.LayoutParams submitParams =
                new LinearLayout.LayoutParams(120, 60);

        mainLayout.addView(submitButton, submitParams);


        // =========================
        // การทำงานของ Submit
        // =========================
        submitButton.setOnClickListener(v -> {

            String hour = hourInput.getText().toString();
            String minute = minuteInput.getText().toString();

            if (hour.isEmpty() || minute.isEmpty()) {
                Toast.makeText(
                        this,
                        "กรุณากรอกเวลาให้ครบ",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        this,
                        "เวลา " + hour + " ชั่วโมง " + minute + " นาที",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // แสดงหน้าจอ
        setContentView(mainLayout);
    }


    // ==========================================
    // สร้าง TextView สำหรับ น้อย / กลาง / มาก
    // ==========================================
    private TextView createTextView(String text, int textColor) {

        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(22);
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(30);

        textView.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        60
                );

        params.setMargins(0, 10, 0, 10);

        textView.setLayoutParams(params);

        // TextView จึงกดไม่ได้เหมือนปุ่ม
        textView.setClickable(false);

        return textView;
    }


    // ==========================================
    // สร้างปุ่มสี
    // ==========================================
    private Button createColorButton(int color) {

        Button button = new Button(this);

        GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(20);

        button.setBackground(background);
        button.setText("");

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(80, 70);

        params.setMargins(15, 0, 15, 0);

        button.setLayoutParams(params);

        return button;
    }


    // ==========================================
    // สร้างช่องว่าง
    // ==========================================
    private void addSpace(LinearLayout layout, int height) {

        View space = new View(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        1,
                        height
                );

        layout.addView(space, params);
    }
}