package com.example.jibnam.CODE;

import java.util.Calendar;

/** CalendarMonth : ข้อมูลเดือนสำหรับปฏิทิน Screen_4 — จำนวนวัน, วันที่ 1 ตรงกับวันอะไร, เลื่อนเดือน */
public class CalendarMonth {

    private int year;    // ปี ค.ศ. เช่น 2026
    private int month;   // เดือน 1-12

    public CalendarMonth(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int howManyDayInMonth() {            // เดือนนี้มีกี่วัน
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        // กุมภาพันธ์: ปีที่หาร 4 ลงตัว = 29 วัน ยกเว้นหาร 100 ลงตัวแต่หาร 400 ไม่ลงตัว
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return 29;
        }
        return 28;
    }

    public int getFirstDayOfMonth() {           // วันที่ 1 ตรงกับวันอะไร 0 = อาทิตย์ ... 6 = เสาร์
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public void nextMonth() {                   // เดือนถัดไป (ธ.ค. → ม.ค. ปีหน้า)
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
    }

    public void previousMonth() {               // เดือนก่อน (ม.ค. → ธ.ค. ปีก่อน)
        month--;
        if (month < 1) {
            month = 12;
            year--;
        }
    }
}
