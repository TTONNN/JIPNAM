package com.example.jibnam;


import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PunScreen extends AppCompatActivity {

    // แปลง dp ให้เหมาะกับหน้าจอมือถือ
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // หน้าหลัก
        // =========================
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        mainLayout.setPadding(
                dp(25),
                dp(20),
                dp(25),
                dp(20)
        );

        mainLayout.setBackgroundColor(Color.rgb(239, 247, 249));


        // =========================
        // น้อย
        // =========================
        TextView lowText = createLevelText(
                "น้อย",
                Color.rgb(116, 215, 72)
        );

        mainLayout.addView(lowText);


        // =========================
        // กลาง
        // =========================
        TextView mediumText = createLevelText(
                "กลาง",
                Color.YELLOW
        );

        mainLayout.addView(mediumText);


        // =========================
        // มาก
        // =========================
        TextView highText = createLevelText(
                "มาก",
                Color.RED
        );

        mainLayout.addView(highText);


        // เว้นระยะ
        addSpace(mainLayout, 55);


        // =========================
        // ปุ่มสี
        // =========================
        LinearLayout colorLayout = new LinearLayout(this);

        colorLayout.setOrientation(LinearLayout.HORIZONTAL);
        colorLayout.setGravity(Gravity.CENTER);


        Button greenButton = createColorButton(
                Color.rgb(116, 215, 72)
        );

        Button yellowButton = createColorButton(
                Color.YELLOW
        );

        Button redButton = createColorButton(
                Color.RED
        );


        colorLayout.addView(greenButton);
        colorLayout.addView(yellowButton);
        colorLayout.addView(redButton);

        mainLayout.addView(colorLayout);


        // =========================
        // กดปุ่มสี
        // =========================

        greenButton.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "เลือก น้อย",
                    Toast.LENGTH_SHORT
            ).show();
        });

        yellowButton.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "เลือก กลาง",
                    Toast.LENGTH_SHORT
            ).show();
        });

        redButton.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "เลือก มาก",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // เว้นระยะ
        addSpace(mainLayout, 45);


        // =========================
        // เวลาออกกำลังกาย
        // =========================

        TextView title = new TextView(this);

        title.setText("เวลาออกกำลังกาย");
        title.setTextSize(21);
        title.setTextColor(Color.BLACK);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        mainLayout.addView(title);


        // เว้นระยะ
        addSpace(mainLayout, 18);


        // =========================
        // ส่วนกรอกเวลา
        // =========================

        LinearLayout timeLayout = new LinearLayout(this);

        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeLayout.setGravity(Gravity.CENTER);


        // ช่องชั่วโมง
        EditText hourInput = createInput();

        timeLayout.addView(
                hourInput,
                new LinearLayout.LayoutParams(
                        dp(62),
                        dp(45)
                )
        );


        // ชั่วโมง
        TextView hourText = new TextView(this);

        hourText.setText(" ชั่วโมง ");
        hourText.setTextSize(15);
        hourText.setTextColor(Color.BLACK);
        hourText.setGravity(Gravity.CENTER);

        timeLayout.addView(hourText);


        // ช่องนาที
        EditText minuteInput = createInput();

        timeLayout.addView(
                minuteInput,
                new LinearLayout.LayoutParams(
                        dp(62),
                        dp(45)
                )
        );


        // นาที
        TextView minuteText = new TextView(this);

        minuteText.setText(" นาที");
        minuteText.setTextSize(15);
        minuteText.setTextColor(Color.BLACK);
        minuteText.setGravity(Gravity.CENTER);

        timeLayout.addView(minuteText);


        mainLayout.addView(timeLayout);


        // เว้นระยะ
        addSpace(mainLayout, 25);


        // =========================
        // ปุ่ม Submit
        // =========================

        Button submitButton = new Button(this);

        submitButton.setText("submit");
        submitButton.setTextSize(16);
        submitButton.setTextColor(Color.WHITE);
        submitButton.setAllCaps(false);

        GradientDrawable submitBackground =
                new GradientDrawable();

        submitBackground.setColor(Color.BLACK);
        submitBackground.setCornerRadius(dp(30));

        submitButton.setBackground(submitBackground);


        LinearLayout.LayoutParams submitParams =
                new LinearLayout.LayoutParams(
                        dp(115),
                        dp(55)
                );

        submitParams.gravity = Gravity.CENTER;

        mainLayout.addView(
                submitButton,
                submitParams
        );


        // =========================
        // กด Submit
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
                        "เวลา " + hour +
                                " ชั่วโมง " +
                                minute +
                                " นาที",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // แสดงหน้าจอ
        setContentView(mainLayout);
    }


    // ==================================================
    // สร้างข้อความ น้อย / กลาง / มาก
    // ==================================================

    private TextView createLevelText(
            String text,
            int textColor
    ) {

        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(21);
        textView.setTextColor(textColor);

        textView.setTypeface(
                null,
                Typeface.BOLD
        );

        textView.setGravity(Gravity.CENTER);


        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(20));

        textView.setBackground(background);


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        params.setMargins(
                0,
                dp(7),
                0,
                dp(7)
        );

        textView.setLayoutParams(params);


        // ไม่ให้กด
        textView.setClickable(false);
        textView.setFocusable(false);

        return textView;
    }


    // ==================================================
    // สร้างปุ่มสี
    // ==================================================

    private Button createColorButton(int color) {

        Button button = new Button(this);

        button.setText("");
        button.setPadding(0, 0, 0, 0);


        GradientDrawable background =
                new GradientDrawable();

        background.setColor(color);
        background.setCornerRadius(dp(18));

        button.setBackground(background);


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(78),
                        dp(65)
                );

        params.setMargins(
                dp(15),
                0,
                dp(15),
                0
        );

        button.setLayoutParams(params);

        return button;
    }


    // ==================================================
    // สร้างช่องกรอก
    // ==================================================

    private EditText createInput() {

        EditText input = new EditText(this);

        input.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        input.setTextSize(17);
        input.setTextColor(Color.BLACK);
        input.setGravity(Gravity.CENTER);

        input.setPadding(0, 0, 0, 0);


        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(245, 245, 245)
        );

        background.setStroke(
                dp(2),
                Color.BLACK
        );

        background.setCornerRadius(dp(7));

        input.setBackground(background);

        return input;
    }


    // ==================================================
    // สร้างช่องว่าง
    // ==================================================

    private void addSpace(
            LinearLayout layout,
            int height
    ) {

        View space = new View(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        1,
                        dp(height)
                );

        layout.addView(space, params);
    }
}