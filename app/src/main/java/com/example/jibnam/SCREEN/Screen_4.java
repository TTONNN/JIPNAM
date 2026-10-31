
package com.example.jibnam.SCREEN;
import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;

import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;

public class Screen_4 extends BaseClass {

    CalendarMonth cal;
    Data data;
    Txt title;
    Box calenderBox;

    Txt detail;

    @Override
    protected void build(Box pg) {
        Calendar today = Calendar.getInstance();
        int month = today.get(Calendar.MONTH) + 1;
        int year = today.get(Calendar.YEAR);

        Btn Back = btn("<< Back");
        onTap(Back, () -> go(Screen_2.class));
        put(pg, Back, 10, 20, 100, 40);

        cal = new CalendarMonth(month, year); // แสดงที่ปัจจุบันเสมอ
        data = new Data(DataApi.realDataFile(this)); //อ่านไฟล์
        title = txt("14 กุมพา วันวาเลนไทน์"); //เอาไว้แสดงเดือนและปี
        title.setTextSize(20);
        title.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
        //int x = (pg.getWidth() - 300) / 2; //กว้างลบกรอบข้อความ
        put(pg, title, 60, 80, 240, WRAP);

        Btn prev = btn("<");
        Btn next = btn(">");
        put(pg, prev, 10, 70, 50, 50);
        put(pg, next, 300, 70, 50, 50);

        onTap(prev, () -> {
            cal.previousMonth();
            updateCalendar();
        });
        onTap(next, () -> {
            cal.nextMonth();
            updateCalendar();
        });

        calenderBox = box();
        put(pg, calenderBox, 60, 130, 240, 240);

        updateCalendar();
    }

    void updateCalendar() {
        String[] month = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
                "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
        title.setText(month[cal.getMonth() - 1] + " " + cal.getYear());
        clr(calenderBox); // ลบวันที่ในเดือนกเก่า
        int callWidth = 48;
        int callHeight = 50;

        String[] days = {"อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"};
        for (int i = 0; i < 7; i++) {
            Txt day = txt(days[i]);
            day.setTextSize(12);
            day.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
            put(calenderBox, day, i * callWidth, 0, callWidth / 7, callHeight / 7);
        }
        int firstDay = cal.getFirstDayOfMonth();
        int daysInMonth = cal.howManyDayInMonth();

        int currentCell = firstDay;
        int startY = 25;

        for (int i = 1; i <= daysInMonth; i++) {
            int selectDay = i;
        }

        int col = currentCell%7 ; // แนวนอน
        int row = currentCell/7 ; // แนวตั้ง
        int x = col * callWidth + 2 ; // แปลงเป็นพิกเซล
        int y = row * callHeight + startY; //


    }
}
