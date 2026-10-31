package com.example.jibnam.SCREEN;

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
        put(pg, title, 100, 10, WRAP, WRAP);

        // น้ำหนักตัว
        Txt weightLabel = txt("น้ำหนักตัว (กิโลกรัม)");
        weightLabel.setTextSize(20);
        put(pg, weightLabel, 100, 70, WRAP, WRAP);

        weightInput = numInp();
        weightInput.setHint("กรุณาใส่น้ำหนักตัวของคุณ");
        put(pg, weightInput, 10, 105, WRAP, WRAP);

        // ปุ่มคำนวณ
        Btn calculateButton = btn("คำนวณ");
        put(pg, calculateButton, 10, 155, WRAP, WRAP);

        onTap(calculateButton, this::calculateTarget);


        // แสดงปริมาณน้ำที่แนะนำ
        targetDrinkText = txt("ปริมาณน้ำที่แนะนำจะแสดงที่นี่");
        targetDrinkText.setTextSize(20);
        put(pg, targetDrinkText, 10, 210, WRAP, WRAP);
        // เวลาตื่นนอน

        Txt wakeLabel = txt("เวลาตื่นนอน");
        wakeLabel.setTextSize(18);
        put(pg, wakeLabel, 10, 290, WRAP, WRAP);

        // ชั่วโมงตื่นนอน
        wakeHourInput = numInp();
        wakeHourInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        put(pg, wakeHourInput, 10, 325, WRAP, WRAP);

        Txt wakeHourText = txt("ชั่วโมง");
        put(pg, wakeHourText, 120, 325, WRAP, WRAP);


        // นาทีตื่นนอน
        wakeMinuteInput = numInp();
        wakeMinuteInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, wakeMinuteInput, 10, 360, WRAP, WRAP);

        Txt wakeMinuteText = txt("นาที");
        put(pg, wakeMinuteText, 120, 360, WRAP, WRAP);

        // เวลาเข้านอน

        Txt sleepLabel = txt("เวลาเข้านอน");
        sleepLabel.setTextSize(18);
        put(pg, sleepLabel, 10, 425, WRAP, WRAP);


        // ชั่วโมงเข้านอน
        sleepHourInput = numInp();
        sleepHourInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        put(pg, sleepHourInput, 10, 460, WRAP, WRAP);

        Txt sleepHourText = txt("ชั่วโมง");
        put(pg, sleepHourText, 120, 460, WRAP, WRAP);


        // นาทีเข้านอน
        sleepMinuteInput = numInp();
        sleepMinuteInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        put(pg, sleepMinuteInput, 10, 495, WRAP, WRAP);

        Txt sleepMinuteText = txt("นาที");
        put(pg, sleepMinuteText, 120, 495, WRAP, WRAP);


        // =========================
        // ปุ่มเริ่มต้นใช้งาน
        // =========================

        Btn submitButton = btn("เริ่มต้นใช้งาน");
        put(pg, submitButton, 10, 565, WRAP, WRAP);

        onTap(submitButton, this::submit);
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

        double rounded = Math.round(liters * 100.0) / 100.0;

        setTxt(targetDrinkText, "ปริมาณน้ำโดยประมาณ " + rounded + " ลิตร/วัน");
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

        return hour >= 0 && hour < 24
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
        if (time(wakeHour, wakeMinute) || !time(sleepHour, sleepMinute)) {
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