
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
        title.setTextSize(40);
        put(pg, title, 10, 10, 340, 55);

        // หัวข้อ
        Txt subtitle = txt("แก้ไขประวัติ");
        subtitle.setTextSize(24);
        put(pg, subtitle, 10, 70, 340, 40);

        // ตรวจสอบว่ามีข้อมูลแก้วหรือไม่
        if (oldDrink == null) {
            Txt errorText = txt("ไม่พบประวัติที่ต้องการแก้ไข");
            put(pg, errorText, 10, 140, 340, 40);

            Btn backButton = btn("กลับหน้าหลัก");
            put(pg, backButton, 110, 200, 140, 45);
            onTap(backButton, () -> go(Screen_2.class));

            return;
        }

        // ข้อมูลก่อนแก้ไข
        Txt oldTitle = txt("ข้อมูลก่อนแก้ไข");
        oldTitle.setTextSize(20);
        put(pg, oldTitle, 20, 130, 320, 35);

        // ปริมาณน้ำเดิม
        double oldLiter = Double.parseDouble(oldDrink[2]);
        double oldMl = oldLiter * 1000;

        Txt oldAmountText = txt("ปริมาณน้ำ: " + formatAmount(oldMl) + " ml");
        put(pg, oldAmountText, 20, 175, 320, 35);

        // เวลาเดิม
        Txt oldTimeText = txt("เวลา: " + formatTime(oldDrink[3], oldDrink[4]));
        put(pg, oldTimeText, 20, 210, 320, 35);

        // ปริมาณน้ำใหม่
        Txt amountLabel = txt("ปริมาณน้ำ (ml)");
        put(pg, amountLabel, 20, 275, 320, 30);

        amountInput = numInp();
        put(pg, amountInput, 20, 310, 320, 50);
        setTxt(amountInput, formatAmount(oldMl));

        // เวลาใหม่
        Txt timeLabel = txt("เวลา");
        put(pg, timeLabel, 20, 390, 320, 30);

        // ชั่วโมง
        hourInput = numInp();
        hourInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        hourInput.setHint("ชั่วโมง");
        put(pg, hourInput, 20, 430, 130, 50);

        // นาที
        minuteInput = numInp();
        minuteInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        minuteInput.setHint("นาที");
        put(pg, minuteInput, 190, 430, 130, 50);

        msgTxt = txt("");
        msgTxt.setTextSize(25);
        put(pg, msgTxt, 20, 500, 320, 30);

        setTxt(hourInput, oldDrink[3]);
        setTxt(minuteInput, oldDrink[4]);

        // ปุ่มลบประวัติ
        Btn deleteButton = btn("ลบประวัติ");
        put(pg, deleteButton, 20, 530, 150, 50);
        onTap(deleteButton, this::deleteHistory);

        // ปุ่ม Submit
        Btn submitButton = btn("Submit");
        put(pg, submitButton, 190, 530, 150, 50);
        onTap(submitButton, this::submit);
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
                showMsg("ปริมาณน้ำต้องไม่เกิน 3000");
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

            if (h < 0 || h > 23 || m < 0 || m > 59) {
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
