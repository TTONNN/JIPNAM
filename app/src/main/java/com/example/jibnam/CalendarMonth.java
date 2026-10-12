
package com.example.jibnam;

import java.util.Calendar;

public class CalendarMonth {

    private int month;
    private int year;

    public CalendarMonth(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDaysInMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);

        // อาทิตย์ = 0, จันทร์ = 1, ... เสาร์ = 6
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public void nextMonth() {
        month++;

        if (month > 12) {
            month = 1;
            year++;
        }
    }

    public void previousMonth() {
        month--;

        if (month < 1) {
            month = 12;
            year--;
        }
    }
}
