
package com.example.jibnam.SCREEN;

import com.example.jibnam.R;
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
        bold(title);
        title.setGravity(1);                               // 1 = ตรงกลางแนวนอน
        centerX(pg, title, 0, 0, 1000, 200);

        // หัวข้อ
        Txt subtitle = txt("แก้ไขประวัติ");
        subtitle.setTextSize(24);
        bold(subtitle);
        subtitle.setGravity(1);
        centerX(pg, subtitle, 0, 180, 1000, 100);

        // ตรวจสอบว่ามีข้อมูลแก้วหรือไม่
        if (oldDrink == null) {
            Txt errorText = txt("ไม่พบประวัติที่ต้องการแก้ไข");
            errorText.setGravity(1);
            centerX(pg, errorText, 0, 300, 1000, 80);

            Img backButton = imgBtn(R.drawable.b_home);
            centerX(pg, backButton, 0, 500, 420, 130);
            onTap(backButton, () -> finish());          // ปิดหน้านี้ กลับ Screen_2 หน้าเดิม

            return;
        }

        // ข้อมูลก่อนแก้ไข
        Txt oldTitle = txt("ข้อมูลก่อนแก้ไข");
        oldTitle.setTextSize(22);
        bold(oldTitle);
        put(pg, oldTitle, 20, 300, 1000, 90);

        // ปริมาณน้ำเดิม
        double oldLiter = Double.parseDouble(oldDrink[6]);
        Txt oldAmountText = txt("ปริมาณน้ำ: " + WaterCalculate.ml(oldLiter));
        oldAmountText.setTextSize(20);
        put(pg, oldAmountText, 20, 400, 1000, 90);

        // เวลาเดิม
        Txt oldTimeText = txt("เวลา: " + formatTime(oldDrink[7], oldDrink[8]));
        oldTimeText.setTextSize(20);
        put(pg, oldTimeText, 20, 500, 1000, 90);

        // ปริมาณน้ำใหม่
        Txt amountLabel = txt("ปริมาณน้ำ (ml)");
        amountLabel.setTextSize(20);
        bold(amountLabel);
        amountLabel.setGravity(1);
        centerX(pg, amountLabel, 0, 600, 1000, 90);

        amountInput = numInp();
        amountInput.setTextSize(20);
        centerX(pg, amountInput, 0, 700, 360, 150);       // กว้าง 360 เหมือนช่องกรอกใน Screen_2
        setTxt(amountInput, "" + Math.round(oldLiter * 1000));

        // เวลาใหม่
        Txt timeLabel = txt("เวลา");
        timeLabel.setTextSize(20);
        bold(timeLabel);
        timeLabel.setGravity(1);
        centerX(pg, timeLabel, 0, 850, 1000, 90);
        // ชั่วโมง
        hourInput = numInp();
        hourInput.setTextSize(20);
        hourInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, hourInput, 250, 1000, 140, 150);

        Txt HourText = txt("ชั่วโมง");
        HourText.setTextSize(20);
        put(pg, HourText,400, 1000, 200, 90);
        // นาที
        minuteInput = numInp();
        minuteInput.setTextSize(20);
        minuteInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        put(pg, minuteInput, 650, 1000, 140, 150);

        Txt MinuteText = txt("นาที");
        MinuteText.setTextSize(20);
        put(pg, MinuteText,800, 1000, 200, 90);

        msgTxt = txt("");
        msgTxt.setTextSize(25);
        bold(msgTxt);
        msgTxt.setGravity(1);
        centerX(pg, msgTxt, 0, 1400, 1000, 110);

        setTxt(hourInput, oldDrink[7]);
        setTxt(minuteInput, oldDrink[8]);

        // ปุ่มลบประวัติ
        Img deleteButton = imgBtn(R.drawable.b_delete);
        put(pg, deleteButton, 120, 1200, 400, 130);
        onTap(deleteButton,() ->deleteHistory());

        // ปุ่ม Submit
        Img submitButton = imgBtn(R.drawable.b_ok);
        put(pg, submitButton, 560, 1200, 400, 130);
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

        // ปิดหน้านี้ กลับ Screen_2 หน้าเดิม (Screen_2 ดึงข้อมูลใหม่เองตอนกลับมา onResume)
        finish();
    }

    // ลบประวัติ
    private void deleteHistory() {

        // ลบข้อมูลแก้วที่เลือกจาก RealData.txt
        data.deleteDrink(serial);

        // ปิดหน้านี้ กลับ Screen_2 หน้าเดิม (Screen_2 ดึงข้อมูลใหม่เองตอนกลับมา onResume)
        finish();
    }

    // แสดงข้อความแจ้งเตือน
    private void showMsg(String text) {
        setTxt(msgTxt, text);
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
