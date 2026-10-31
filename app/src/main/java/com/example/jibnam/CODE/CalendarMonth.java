
package com.example.jibnam.CODE;

import java.util.Calendar;

public class CalendarMonth {

    private int year;
    private int month;

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

    public int howManyDayInMonth ()
    {
        //(มกราคม, มีนาคม, พฤษภาคม, กรกฎาคม, สิงหาคม, ตุลาคม, ธันวาคม)
        if (month == 1 || month == 3 || month == 5 || month == 7 ||
                month == 8 || month == 10 || month == 12) {
            return 31;
        }
        // เมษายน, มิถุนายน, กันยายน, พฤศจิกายน
        else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        // 3. เดือนกุมภาพันธ์
        else if (month == 2) {
            // ปีที่หารด้วย 4 ลงตัวจะเป็นปีที่มี 366 ยกเว้นปีที่หารด้วย 100 ลงตัวแต่หารด้วย 400 ไม่ลงตัว
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
            {
                return 29;
            }
            else
            {
                return 28;
            }
        }

        return 0;
    }

    public int getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // คศ , 0-11 , วันที่แรก เทียบหาวัน
        return cal.get(Calendar.DAY_OF_WEEK) - 1; // 1-7 -> 0-6 ในกริตปฏิทิน
    }

    public void nextMonth()
    {
        month++;
        if (month > 12)
        {
            month = 1;
            year++;
        }
    }

    public void previousMonth()
    {
        month--;
        if (month < 1)
        {
            month = 12;
            year--;
        }
    }

}
