
package com.example.jibnam.SCREEN;

import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;

import java.util.Locale;

public class Screen_5 extends BaseClass {

    // ข้อมูลแก้วที่เลือก
    private Data data;
    private String serial;
    private String[] oldDrink;

    // ช่องกรอกข้อมูล
    private Inp amountInput;
    private Inp hourInput;
    private Inp minuteInput;
    private Txt msgTxt;
    @Override
    protected void build(Box pg) {

        // เชื่อมต่อไฟล์ข้อมูล
        data = new Data(DataApi.realDataFile(this));

        // รับ serial ที่ส่งมาจาก Screen_2
        serial = arg("serial");

        // ค้นหาข้อมูลแก้วที่ต้องการแก้ไข
        oldDrink = data.getDrink(serial);

        // ชื่อแอป
        Txt title = txt("JIBNAM");
        title.setTextSize(48);
        centerX(pg, title, 0,0 , WRAP, WRAP);

        // หัวข้อ
        Txt subtitle = txt("แก้ไขประวัติ");
        subtitle.setTextSize(24);
        centerX(pg, subtitle, 0, 180, WRAP, WRAP);

        // ตรวจสอบว่ามีข้อมูลแก้วหรือไม่
        if (oldDrink == null) {
            Txt errorText = txt("ไม่พบประวัติที่ต้องการแก้ไข");
            centerX(pg, errorText, 0, 300, WRAP, WRAP);

            Btn backButton = btn("กลับหน้าหลัก");
            centerX(pg, backButton, 0, 500, WRAP, WRAP);
            onTap(backButton, () -> go(Screen_2.class));

            return;
        }

        // ข้อมูลก่อนแก้ไข
        Txt oldTitle = txt("ข้อมูลก่อนแก้ไข");
        oldTitle.setTextSize(22);
        put(pg, oldTitle, 20, 300, WRAP, WRAP);

        // ปริมาณน้ำเดิม
        double oldLiter = Double.parseDouble(oldDrink[2]);
        double oldMl = oldLiter * 1000;

        Txt oldAmountText = txt("ปริมาณน้ำ: " + formatAmount(oldMl) + " ml");
        oldAmountText.setTextSize(20);
        put(pg, oldAmountText, 20, 400, WRAP, WRAP);

        // เวลาเดิม
        Txt oldTimeText = txt("เวลา: " + formatTime(oldDrink[3], oldDrink[4]));
        oldTimeText.setTextSize(20);
        put(pg, oldTimeText, 20, 500, WRAP, WRAP);

        // ปริมาณน้ำใหม่
        Txt amountLabel = txt("ปริมาณน้ำ (ml)");
        amountLabel.setTextSize(20);
        centerX(pg, amountLabel, 0, 600, WRAP, WRAP);

        amountInput = numInp();
        amountInput.setTextSize(20);
        centerX(pg, amountInput, 0, 700, WRAP, WRAP);
        setTxt(amountInput, formatAmount(oldMl));

        // เวลาใหม่
        Txt timeLabel = txt("เวลา");
        timeLabel.setTextSize(20);
        centerX(pg, timeLabel, 0, 850, WRAP, WRAP);
        // ชั่วโมง
        hourInput = numInp();
        hourInput.setTextSize(20);
        hourInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, hourInput, 250, 1000, WRAP, WRAP);

        Txt HourText = txt("ชั่วโมง");
        HourText.setTextSize(20);
        put(pg, HourText,400, 1000, WRAP, WRAP);
        // นาที
        minuteInput = numInp();
        minuteInput.setTextSize(20);
        minuteInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, minuteInput, 650, 1000, WRAP, WRAP);

        Txt MinuteText = txt("นาที");
        MinuteText.setTextSize(20);
        put(pg, MinuteText,800, 1000, WRAP, WRAP);

        msgTxt = txt("");
        msgTxt.setTextSize(25);
        centerX(pg, msgTxt, 0, 1400, WRAP, WRAP);

        setTxt(hourInput, oldDrink[3]);
        setTxt(minuteInput, oldDrink[4]);

        // ปุ่มลบประวัติ
        Btn deleteButton = btn("ลบประวัติ");
        put(pg, deleteButton, 200, 1200, WRAP, WRAP);
        onTap(deleteButton,() ->deleteHistory());

        // ปุ่ม Submit
        Btn submitButton = btn("Submit");
        put(pg, submitButton, 600, 1200, WRAP, WRAP);
        onTap(submitButton,() -> submit());
    }

    // บันทึกการแก้ไข
    private void submit() {

        String amount = getTxt(amountInput);
        String hour = getTxt(hourInput);
        String minute = getTxt(minuteInput);

        double ml;
        int h;
        int m;

        // ตรวจสอบปริมาณน้ำ
        try {
            ml = Double.parseDouble(amount);

            if (ml <= 0) {
                showMsg("ปริมาณน้ำต้องมากกว่า 0");
                return;
            } else if (ml > 3000) {
                showMsg("ปริมาณน้ำต้องไม่เกิน 3000 ml");
                return;
            }

        } catch (NumberFormatException e) {
            showMsg("กรุณากรอกปริมาณน้ำ");
            return;
        }

        // ตรวจสอบเวลา
        try {
            h = Integer.parseInt(hour);
            m = Integer.parseInt(minute);

            if (h < 0 || h > 24 || m < 0 || m > 59) {
                showMsg("กรุณากรอกเวลาให้ถูกต้อง");
                return;
            }

        } catch (NumberFormatException e) {
            showMsg("กรุณากรอกเวลาให้ครบ");
            return;
        }

        // แปลง ml เป็น L ก่อนบันทึก
        double liters = ml / 1000.0;

        // แก้ไขข้อมูลใน RealData.txt
        data.editDrink(serial, liters, h, m);

        // กลับไปหน้า 2 เพื่ออ่านข้อมูลใหม่
        go(Screen_2.class);
        finish();
    }

    // ลบประวัติ
    private void deleteHistory() {

        // ลบข้อมูลแก้วที่เลือกจาก RealData.txt
        data.deleteDrink(serial);

        // กลับไปหน้า 2
        go(Screen_2.class);
        finish();
    }

    // แสดงข้อความแจ้งเตือน
    private void showMsg(String text) {
        setTxt(msgTxt, text);
    }

    // จัดรูปแบบปริมาณน้ำ
    private String formatAmount(double value) {

        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }

        return String.format(Locale.US, "%.2f", value);
    }

    // จัดรูปแบบเวลา
    private String formatTime(String hour, String minute) {

        return String.format(
                Locale.US,
                "%02d:%02d",
                Integer.parseInt(hour),
                Integer.parseInt(minute)
        );
    }
}
