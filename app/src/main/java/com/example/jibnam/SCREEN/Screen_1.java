package com.example.jibnam.SCREEN;

import com.example.jibnam.R;
import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;

public class Screen_1 extends BaseClass {

    Inp weightInput;

    Inp wakeHourInput;
    Inp wakeMinuteInput;

    Inp sleepHourInput;
    Inp sleepMinuteInput;

    Txt targetDrinkText;


    @Override
    protected void build(Box pg) {

        // ชื่อแอป
        Txt title = txt("JIBNAM");
        title.setTextSize(48);
        bold(title);
        centerX(pg, title, 0, 0, WRAP, WRAP);

        // น้ำหนักตัว
        Txt weightLabel = txt("น้ำหนักตัว (กิโลกรัม)");
        weightLabel.setTextSize(20);
        bold(weightLabel);
        centerX(pg, weightLabel, 0, 220, WRAP, WRAP);

        weightInput = numInp();
        centerX(pg, weightInput, 0, 300, 360, WRAP);          // กว้าง 360 เหมือนช่องกรอกใน Screen_2

        // ปุ่มคำนวณ
        Img calculateButton = imgBtn(R.drawable.b_calc);
        centerX(pg, calculateButton, 0, 450, WRAP, WRAP);

        onTap(calculateButton, () -> calculateTarget());


        // แสดงปริมาณน้ำที่แนะนำ
        targetDrinkText = txt("ปริมาณน้ำที่แนะนำจะแสดงที่นี่");
        targetDrinkText.setTextSize(20);
        bold(targetDrinkText);
        centerX(pg, targetDrinkText, 0, 650, WRAP, WRAP);
        // เวลาตื่นนอน

        Txt wakeLabel = txt("เวลาตื่นนอน");
        wakeLabel.setTextSize(20);
        bold(wakeLabel);
        centerX(pg, wakeLabel, 0, 800, WRAP, WRAP);

        // ชั่วโมงตื่นนอน
        wakeHourInput = numInp();
        wakeHourInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, wakeHourInput, 250, 1000, 140, WRAP);


        Txt wakeHourText = txt("ชั่วโมง");
        wakeHourText.setTextSize(18);
        put(pg, wakeHourText,400, 1000, WRAP, WRAP);

        // นาทีตื่นนอน
        wakeMinuteInput = numInp();
        wakeMinuteInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, wakeMinuteInput, 550, 1000, 140, WRAP);

        Txt wakeMinuteText = txt("นาที");
        wakeMinuteText.setTextSize(18);
        put(pg, wakeMinuteText, 700, 1000, WRAP, WRAP);


        // เวลาเข้านอน

        Txt sleepLabel = txt("เวลาเข้านอน");
        sleepLabel.setTextSize(20);
        bold(sleepLabel);
        centerX(pg, sleepLabel, 0, 1200, WRAP, WRAP);


        // ชั่วโมงเข้านอน
        sleepHourInput = numInp();
        sleepHourInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, sleepHourInput, 250, 1400, 140, WRAP);

        Txt sleepHourText = txt("ชั่วโมง");
        sleepHourText.setTextSize(18);
        put(pg, sleepHourText, 400, 1400, WRAP, WRAP);


        // นาทีเข้านอน
        sleepMinuteInput = numInp();
        sleepMinuteInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        put(pg, sleepMinuteInput, 550, 1400, 140, WRAP);

        Txt sleepMinuteText = txt("นาที");
        sleepMinuteText.setTextSize(18);
        put(pg, sleepMinuteText, 700, 1400, WRAP, WRAP);


        // =========================
        // ปุ่มเริ่มต้นใช้งาน
        // =========================

        Img submitButton = imgBtn(R.drawable.b_start);
        centerX(pg, submitButton, 0, 1550, WRAP, WRAP);

        onTap(submitButton, () -> submit());
    }


    // =========================
    // เชื่อมต่อ Data
    // =========================s

    private Data getData() {

        return new Data(DataApi.realDataFile(this));
    }


    // =========================
    // คำนวณปริมาณน้ำ
    // =========================

    private void calculateTarget() {

        Double weight = readWeight();

        if (weight == null) {
            return;
        }

        double liters = WaterCalculate.targetL(weight);

        setTxt(targetDrinkText, "ปริมาณน้ำโดยประมาณ " + WaterCalculate.L(liters) + "/วัน");
    }


    // =========================
    // อ่านน้ำหนัก
    // =========================

    private Double readWeight() {

        String value = getTxt(weightInput);

        if (value.isEmpty()) {
            setTxt(targetDrinkText, "กรุณากรอกน้ำหนัก");
            return null;
        }

        try {

            double weight = Double.parseDouble(value);

            if (weight <= 0 || weight > 500) {
                setTxt(targetDrinkText, "กรุณาตรวจสอบน้ำหนัก");
                return null;
            }
            return weight;

        } catch (NumberFormatException e) {

            setTxt(targetDrinkText, "กรุณากรอกน้ำหนักเป็นตัวเลข");
            return null;
        }
    }


    // =========================
    // อ่านค่าจำนวนเต็ม
    // =========================

    private Integer readInt(Inp input) {

        String value = getTxt(input);

        if (value.isEmpty()) {
            return null;
        }

        try {

            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            return null;
        }
    }


    // =========================
    // ตรวจสอบเวลา
    // =========================

    private boolean time(int hour, int minute) {

        return hour >= 0 && hour <= 24
                && minute >= 0 && minute < 60;
    }


    // =========================
    // บันทึกข้อมูล
    // =========================
    private void submit() {

        // อ่านน้ำหนัก
        Double weight = readWeight();

        if (weight == null) {
            return;
        }


        // อ่านเวลาตื่นนอน
        Integer wakeHour = readInt(wakeHourInput);
        Integer wakeMinute = readInt(wakeMinuteInput);

        // อ่านเวลาเข้านอน
        Integer sleepHour = readInt(sleepHourInput);
        Integer sleepMinute = readInt(sleepMinuteInput);

        // ตรวจสอบว่ากรอกครบทุกช่อง
        if (wakeHour == null || wakeMinute == null || sleepHour == null || sleepMinute == null) {

            setTxt(targetDrinkText, "กรุณากรอกเวลาให้ครบ");
            return;
        }

        // ตรวจสอบความถูกต้องของเวลา
        if (!time(wakeHour, wakeMinute) || !time(sleepHour, sleepMinute)) {
            setTxt(targetDrinkText, "กรุณากรอกเวลาให้ถูกต้อง");
            return;
        }


        // สร้าง Data Object
        Data data = getData();


        // บันทึกข้อมูลลง RealData.txt
        data.saveUser(
                weight,
                wakeHour,
                wakeMinute,
                sleepHour,
                sleepMinute
        );


        // ไป Screen_2
        go(Screen_2.class);
    }
}