package com.example.jibnam;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Screen_1 extends Activity {

    // UI
    private EditText weightInput;
    private EditText wakeHourInput;
    private EditText wakeMinuteInput;
    private EditText sleepHourInput;
    private EditText sleepMinuteInput;

    private TextView waterAmount;

    private Button calculateButton;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // หน้าหลัก
        // =========================

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(dp(24), dp(24), dp(24), dp(32));

        scrollView.addView(mainLayout);

        // =========================
        // รูปขวดน้ำ
        // =========================
        // ใช้รูป bottle ที่อยู่ใน res/drawable
        // เรียกใช้ผ่าน R โดยตรง
        int imageId = R.drawable.bottle;
        ImageView waterBottle = new ImageView(this);
        waterBottle.setImageResource(imageId);

        waterBottle.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(220)
                );

        mainLayout.addView(waterBottle, imageParams);

        // =========================
        // JIBNAM
        // =========================

        TextView title = createTitle("JIBNAM");

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        mainLayout.addView(title, titleParams);

        // =========================
        // ใส่น้ำหนัก
        // =========================

        TextView weightTitle = createSectionTitle("ใส่น้ำหนัก");

        LinearLayout.LayoutParams weightTitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        weightTitleParams.topMargin = dp(28);

        mainLayout.addView(weightTitle, weightTitleParams);

        // แถวของน้ำหนัก
        LinearLayout weightRow = new LinearLayout(this);
        weightRow.setOrientation(LinearLayout.HORIZONTAL);
        weightRow.setGravity(Gravity.CENTER_VERTICAL);

        // ช่องน้ำหนัก
        weightInput = createInput("น้ำหนัก");
        weightInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        LinearLayout.LayoutParams weightInputParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(55),
                        1
                );

        weightRow.addView(weightInput, weightInputParams);

        // kg
        TextView kgText = new TextView(this);
        kgText.setText(" kg");
        kgText.setTextSize(18);
        kgText.setTextColor(Color.BLACK);
        kgText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams kgParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        dp(55)
                );

        kgParams.leftMargin = dp(8);

        weightRow.addView(kgText, kgParams);

        // ปุ่มคำนวณ
        calculateButton = new Button(this);
        calculateButton.setText("คำนวณ");
        calculateButton.setTextSize(16);

        LinearLayout.LayoutParams calculateParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        dp(55)
                );

        calculateParams.leftMargin = dp(8);

        weightRow.addView(calculateButton, calculateParams);

        LinearLayout.LayoutParams weightRowParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(55)
                );

        weightRowParams.topMargin = dp(8);

        mainLayout.addView(weightRow, weightRowParams);

        // =========================
        // ปริมาณน้ำที่ควรดื่ม
        // =========================

        TextView waterTitle =
                createSectionTitle("ปริมาณน้ำที่ควรดื่ม");

        LinearLayout.LayoutParams waterTitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        waterTitleParams.topMargin = dp(28);

        mainLayout.addView(waterTitle, waterTitleParams);

        // กล่องแสดงปริมาณน้ำ
        waterAmount = new TextView(this);
        waterAmount.setText("-- L");
        waterAmount.setTextSize(24);
        waterAmount.setTextColor(Color.BLACK);
        waterAmount.setTypeface(null, Typeface.BOLD);
        waterAmount.setGravity(Gravity.CENTER);
        waterAmount.setBackground(createBoxBackground());

        LinearLayout.LayoutParams waterAmountParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(65)
                );

        waterAmountParams.topMargin = dp(8);

        mainLayout.addView(waterAmount, waterAmountParams);

        // =========================
        // เวลาตื่นนอน
        // =========================

        TextView wakeTitle =
                createSectionTitle("เวลาตื่นนอน");

        LinearLayout.LayoutParams wakeTitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        wakeTitleParams.topMargin = dp(28);

        mainLayout.addView(wakeTitle, wakeTitleParams);

        LinearLayout wakeRow = createTimeRow();

        mainLayout.addView(
                wakeRow,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(55)
                )
        );

        // =========================
        // เวลานอนหลับ
        // =========================

        TextView sleepTitle =
                createSectionTitle("เวลานอนหลับ");

        LinearLayout.LayoutParams sleepTitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        sleepTitleParams.topMargin = dp(28);

        mainLayout.addView(sleepTitle, sleepTitleParams);

        LinearLayout sleepRow = createTimeRow();

        mainLayout.addView(
                sleepRow,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(55)
                )
        );

        // =========================
        // Submit
        // =========================

        submitButton = new Button(this);
        submitButton.setText("submit");
        submitButton.setTextSize(18);
        submitButton.setTextColor(Color.WHITE);
        submitButton.setTypeface(null, Typeface.BOLD);
        submitButton.setBackgroundColor(Color.BLACK);

        LinearLayout.LayoutParams submitParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(60)
                );

        submitParams.topMargin = dp(35);

        mainLayout.addView(submitButton, submitParams);

        // =========================
        // แสดงหน้าจอ
        // =========================

        setContentView(scrollView);
    }

    // =========================================================
    // สร้างช่องเวลา HH : MM
    // =========================================================

    private LinearLayout createTimeRow() {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        // ชั่วโมง
        EditText hour = createInput("HH");
        hour.setGravity(Gravity.CENTER);
        hour.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        hour.setMaxLines(1);

        LinearLayout.LayoutParams hourParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(55),
                        1
                );

        row.addView(hour, hourParams);

        // :
        TextView colon = new TextView(this);
        colon.setText(":");
        colon.setTextSize(24);
        colon.setTextColor(Color.BLACK);
        colon.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams colonParams =
                new LinearLayout.LayoutParams(
                        dp(40),
                        dp(55)
                );

        row.addView(colon, colonParams);

        // นาที
        EditText minute = createInput("MM");
        minute.setGravity(Gravity.CENTER);
        minute.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        minute.setMaxLines(1);

        LinearLayout.LayoutParams minuteParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(55),
                        1
                );

        row.addView(minute, minuteParams);

        return row;
    }

    // =========================================================
    // สร้าง EditText
    // =========================================================

    private EditText createInput(String hint) {

        EditText input = new EditText(this);

        input.setHint(hint);
        input.setTextSize(18);
        input.setTextColor(Color.BLACK);
        input.setHintTextColor(Color.GRAY);
        input.setSingleLine(true);
        input.setPadding(
                dp(12),
                0,
                dp(12),
                0
        );

        input.setBackground(createBoxBackground());

        return input;
    }

    // =========================================================
    // สร้างหัวข้อ
    // =========================================================

    private TextView createSectionTitle(String text) {

        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(null, Typeface.BOLD);

        return textView;
    }

    // =========================================================
    // สร้าง Title JIBNAM
    // =========================================================

    private TextView createTitle(String text) {

        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(28);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    // =========================================================
    // สร้างกรอบช่อง Input
    // =========================================================

    private GradientDrawable createBoxBackground() {

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);

        background.setStroke(
                dp(2),
                Color.BLACK
        );

        background.setCornerRadius(
                dp(12)
        );

        return background;
    }

    // =========================================================
    // แปลง dp
    // =========================================================

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}