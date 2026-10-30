
package com.example.jibnam;

import android.graphics.Color;

public class Screen_4 extends BaseClass {

    CalendarMonth month;
    MockHistory mock;

    Txt title;
    Box calendarBox;
    Txt detail;

    @Override
    void build(Box pg) {

        // สร้างข้อมูลเดือนและข้อมูลการดื่มน้ำ
        month = new CalendarMonth(10, 2026);
        mock = new MockHistory();

        // ชื่อเดือนและปี
        title = txt("");
        alg(title, A_C);
        title.setTextSize(20);
        add(pg, title);

        // ปุ่มเปลี่ยนเดือน
        Box buttons = row();

        Btn prev = btn("<");
        Btn next = btn(">");

        addW(buttons, prev, 1f);
        addW(buttons, next, 1f);
        add(pg, buttons);

        Btn back = btn("ย้อนกลับ");
        add(pg, back);

        onTap(back, () -> {
            finish();
        });

        onTap(prev, () -> {
            month.previousMonth();
            updateCalendar();
        });

        onTap(next, () -> {
            month.nextMonth();
            updateCalendar();
        });

        // พื้นที่แสดงปฏิทิน
        calendarBox = col();
        add(pg, calendarBox);

        // พื้นที่แสดงรายละเอียดวันที่
        detail = txt("เลือกวันที่");
        add(pg, detail);

        // แสดงปฏิทินครั้งแรก
        updateCalendar();
    }

    // อัปเดตชื่อเดือนและสร้างตารางปฏิทินใหม่
    void updateCalendar() {

        String[] months = {
                "มกราคม", "กุมภาพันธ์", "มีนาคม",
                "เมษายน", "พฤษภาคม", "มิถุนายน",
                "กรกฎาคม", "สิงหาคม", "กันยายน",
                "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
        };

        title.setText(
                months[month.getMonth() - 1]
                        + " " + (month.getYear() + 543)
        );

        // ล้างปฏิทินเดิมก่อนสร้างใหม่
        clr(calendarBox);

        String[] weekdays = {
                "อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"
        };

        // สร้างหัวตารางชื่อวัน
        Box header = row();

        for (String weekday : weekdays) {
            Txt text = txt(weekday);
            alg(text, A_C);
            text.setTextSize(14);

            addW(header, text, 1f);
        }

        add(calendarBox, header);

        // หาว่าวันที่ 1 ตรงกับวันอะไร
        int firstDay = month.getFirstDayOfMonth();

        // หาจำนวนวันทั้งหมดของเดือน
        int daysInMonth = month.getDaysInMonth();

        Box week = row();

        // เว้นช่องว่างก่อนถึงวันที่ 1
        for (int i = 0; i < firstDay; i++) {
            Txt blank = txt("");
            addW(week, blank, 1f);
        }

        // สร้างช่องวันที่ตั้งแต่ 1 จนถึงวันสุดท้าย
        for (int day = 1; day <= daysInMonth; day++) {

            final int selectedDay = day;

            Box dayBox = col();

            // ดึงข้อมูลการดื่มน้ำของวันที่กำลังสร้าง
            MockHistory.DayData data =
                    mock.getDay(
                            month.getYear(),
                            month.getMonth(),
                            day
                    );

            // ค่าเริ่มต้น: ไม่มีข้อมูล
            int bgColor = Color.WHITE;
            String percent = "-";

            if (data != null && data.target > 0) {

                double value =
                        data.drank / data.target * 100;

                percent = data.getPercent() + "%";

                // มากกว่าหรือเท่ากับ 120% = สีฟ้า
                if (value >= 120) {
                    bgColor = Color.rgb(173, 216, 255);

                    // ตั้งแต่ 100% = สีเขียว
                } else if (value >= 100) {
                    bgColor = Color.rgb(190, 230, 190);

                    // ตั้งแต่ 50% แต่ไม่ถึง 100% = สีเหลือง
                } else if (value >= 50) {
                    bgColor = Color.rgb(255, 235, 160);

                    // ต่ำกว่า 50% = สีแดง
                } else {
                    bgColor = Color.rgb(245, 180, 180);
                }
            }

            dayBox.setBackgroundColor(bgColor);

            dayBox.setPadding(
                    dp(2), dp(5), dp(2), dp(5)
            );

            // แสดงเลขวันที่
            Txt dayText = txt(String.valueOf(day));
            alg(dayText, A_C);
            dayText.setTextSize(18);
            dayText.setTextColor(Color.BLACK);

            add(dayBox, dayText);

            // แสดงเปอร์เซ็นต์ใต้เลขวันที่
            Txt percentText = txt(percent);
            alg(percentText, A_C);
            percentText.setTextSize(10);
            percentText.setTextColor(Color.DKGRAY);

            add(dayBox, percentText);

            // เพิ่มช่องวันที่ลงในแถว
            addW(week, dayBox, 1f);

            // เมื่อครบ 7 ช่อง ให้เพิ่มแถวลงในปฏิทิน
            if ((firstDay + day) % 7 == 0) {
                add(calendarBox, week);
                week = row();
            }

            // เมื่อกดวันที่ ให้แสดงรายละเอียด
            onTap(dayBox, () -> {

                MockHistory.DayData selectedData =
                        mock.getDay(
                                month.getYear(),
                                month.getMonth(),
                                selectedDay
                        );

                if (selectedData == null) {

                    detail.setText(
                            "วันที่ " + selectedDay
                                    + "\nไม่มีข้อมูล"
                    );

                } else {

                    detail.setText(
                            "วันที่ " + selectedDay
                                    + "\nดื่มน้ำ " + selectedData.drank
                                    + " ลิตร"
                                    + "\nเป้าหมาย " + selectedData.target
                                    + " ลิตร"
                                    + "\nคิดเป็น "
                                    + selectedData.getPercent() + "%"
                    );
                }
            });
        }

        // เพิ่มแถวสุดท้าย และเติมช่องว่างให้ครบ 7 วัน
        if (week.getChildCount() > 0) {

            while (week.getChildCount() < 7) {
                Txt blank = txt("");
                addW(week, blank, 1f);
            }

            add(calendarBox, week);
        }
    }
}
