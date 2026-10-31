
package com.example.jibnam.SCREEN;

import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Screen_6 extends BaseClass {

    Data data;

    int wakeMin, sleepMin;
    double targetL;

    double[] ratio = {0.15, 0.275, 0.275, 0.15, 0.15};
    int[] start = new int[5];
    int[] end = new int[5];

    double[] goal = new double[5];
    double[] drank = new double[5];

    String[] periodName = {
            "ช่วงที่ 1", "ช่วงที่ 2", "ช่วงที่ 3",
            "ช่วงที่ 4", "ช่วงที่ 5"
    };

    Calendar cycleStart;

    SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    List<String[]> allDrinks;

    @Override
    protected void build(Box pg) {

        data = new Data(DataApi.realDataFile(this));

        String[] user = data.lastUser();

        double weight = Double.parseDouble(user[2]);

        wakeMin = Integer.parseInt(user[3]) * 60
                + Integer.parseInt(user[4]);

        sleepMin = Integer.parseInt(user[5]) * 60
                + Integer.parseInt(user[6]);

        targetL = WaterCalculate.targetL(weight);

        calculatePeriods();

        cycleStart = getCycleStart();

        // อ่านข้อมูลการดื่มน้ำ
        allDrinks = data.getDrinks(null);

        calculateDrinks(cycleStart);

        // หัวข้อ
        showTxt(pg, "สรุปการดื่มน้ำ", 22, 10, 20, 340, 40);

        // ยอดน้ำที่ดื่มแล้ว
        double total = 0;

        for (int i = 0; i < 5; i++) {
            total += drank[i];
        }

        showTxt(pg,
                "ดื่มแล้ว " + ml(total) + " / " + ml(targetL) + " ml",
                16, 10, 65, 340, 30);

        showTxt(pg, "รายละเอียดการดื่มแต่ละช่วง",
                16, 10, 100, 340, 30);

        // แสดง 5 ช่วงเวลา แยกเป็น 5 กล่อง
        int nowOffset = getCurrentOffset();

        for (int i = 0; i < 5; i++) {

            String emoji = getEmoji(i, nowOffset);
            String marker = "";

            if (emoji.equals("👉")) {
                marker = " 👈";
            }

            String text = emoji + " ช่วงที่ " + (i + 1)
                    + " : " + timeText(start[i])
                    + "-" + timeText(end[i])
                    + " น. เป้าหมาย " + ml(goal[i])
                    + " ml ดื่มไป " + ml(drank[i])
                    + " ml" + marker;

            // สร้างกล่องของแต่ละช่วง
            Col itemBox = col();
            border(itemBox);

            Txt item = txt(text);
            item.setTextSize(12);
            item.setSingleLine(true);
            item.setGravity(17);
            item.setPadding(5, 0, 5, 0);
            item.setIncludeFontPadding(false);

            add(itemBox, item, u(330), u(40));

            // วางกล่องแต่ละช่วง
            put(pg, itemBox,
                    u(10), u(130 + i * 48),
                    u(340), u(40));
        }

        // คำนวณย้อนหลัง 7 รอบ
        double[] weeklyVolume = new double[5];
        int[] drankDays = new int[5];

        boolean complete7Days = true;

        Calendar day = (Calendar) cycleStart.clone();
        day.add(Calendar.DAY_OF_MONTH, -6);

        for (int d = 0; d < 7; d++) {

            if (!hasDrinkInCycle(day)) {
                complete7Days = false;
            }

            calculateDrinks(day);

            for (int i = 0; i < 5; i++) {

                weeklyVolume[i] += drank[i];

                if (drank[i] > 0) {
                    drankDays[i]++;
                }
            }

            day.add(Calendar.DAY_OF_MONTH, 1);
        }

        // กลับมาคำนวณรอบปัจจุบัน
        calculateDrinks(cycleStart);

        // วิเคราะห์ 7 วัน
        showTxt(pg, "วิเคราะห์การดื่มย้อนหลัง 7 วัน",
                16, 10, 380, 340, 30);

        if (!complete7Days) {

            showTxt(pg, "กรุณาใช้แอปนี้ถึง 7 วันก่อน",
                    14, 10, 415, 340, 35);

        } else {

            showWeekly(pg, weeklyVolume, drankDays);
        }

        // คำเตือนเรื่องการดื่มน้ำ
        showTxt(pg, "⚠️ คำเตือนเรื่องการดื่มน้ำ",
                14, 10, 525, 340, 30);

        showTxt(pg,
                "ดื่มน้ำมากเกินไป วันละ 6-7 ลิตร",
                11, 10, 555, 340, 25);

        showTxt(pg,
                "อาจเป็นอันตรายถึงขั้นเสียชีวิตได้",
                11, 10, 580, 340, 25);

        showTxt(pg,
                "ดื่มน้ำน้อยเกินไป เสี่ยงเกิดโรคต่าง ๆ",
                11, 10, 605, 340, 25);

        showTxt(pg,
                "เช่น โรคนิ่ว และทางเดินปัสสาวะอักเสบ",
                11, 10, 630, 340, 25);

        // ปุ่มกลับ
        Btn back = btn("กลับ");
        placeBtn(pg, back, 80, 675, 200, 45);

        onTap(back, () -> go(Screen_2.class));
    }

    // แสดงข้อความ
    void showTxt(Box pg, String text, int size,
                 int x, int y, int w, int h) {

        Txt v = txt(text);
        v.setTextSize(size);
        v.setGravity(17);

        placeTxt(pg, v, x, y, w, h);
    }

    // เลือก Emoji
    String getEmoji(int i, int now) {

        boolean current = now >= start[i] && now < end[i];

        if (current) {
            return "👉";
        } else if (drank[i] >= goal[i] * 1.2) {
            return "🟦";
        } else if (now < start[i]) {
            return "⬜";
        } else if (drank[i] >= goal[i]) {
            return "🟩";
        } else if (drank[i] > 0) {
            return "🟨";
        } else if (now >= end[i]) {
            return "🟥";
        }

        return "⬜";
    }

    // แสดงผลวิเคราะห์ 7 วัน
    void showWeekly(Box pg, double[] volume, int[] days) {

        double max = volume[0];
        double min = volume[0];

        int maxMissed = 7 - days[0];

        for (int i = 1; i < 5; i++) {

            if (volume[i] > max) {
                max = volume[i];
            }

            if (volume[i] < min) {
                min = volume[i];
            }

            int missed = 7 - days[i];

            if (missed > maxMissed) {
                maxMissed = missed;
            }
        }

        String most = "💧 ดื่มน้ำมากที่สุด: ";
        String least = "💧 ดื่มน้ำน้อยที่สุด: ";
        String missedText = " ขาดการดื่มบ่อยที่สุด: ";

        for (int i = 0; i < 5; i++) {

            if (volume[i] == max) {
                most += periodName[i] + " (รวม "
                        + ml(volume[i]) + " ml, ดื่ม "
                        + days[i] + "/7 วัน) ";
            }

            if (volume[i] == min) {
                least += periodName[i] + " (รวม "
                        + ml(volume[i]) + " ml, ดื่ม "
                        + days[i] + "/7 วัน) ";
            }

            int missed = 7 - days[i];

            if (missed == maxMissed) {
                missedText += periodName[i] + " (" + missed + " วัน) ";
            }
        }

        showTxt(pg, most, 11, 10, 415, 340, 35);
        showTxt(pg, least, 11, 10, 450, 340, 35);
        showTxt(pg, missedText, 11, 10, 485, 340, 35);
    }

    // แปลงหน่วยและวางตำแหน่ง
    int u(int value) {

        return value * getResources()
                .getDisplayMetrics().widthPixels / 360;
    }

    void placeTxt(Box p, Txt v, int x, int y, int w, int h) {

        int height;

        if (h == WRAP) {
            height = WRAP;
        } else {
            height = u(h);
        }

        centerX(p, v, u(x), u(y), u(w), height);
    }

    void placeBtn(Box p, Btn v, int x, int y, int w, int h) {

        centerX(p, v, u(x), u(y), u(w), u(h));
    }

    // คำนวณเวลา 5 ช่วง
    void calculatePeriods() {

        int total = sleepMin - wakeMin;

        if (total <= 0) {
            total += 1440;
        }

        double accumulated = 0;
        int current = wakeMin;

        for (int i = 0; i < 5; i++) {

            start[i] = current;
            accumulated += ratio[i];

            if (i == 4) {
                end[i] = wakeMin + total;
            } else {
                end[i] = wakeMin + (int) Math.round(total * accumulated);
            }
            current = end[i];
            goal[i] = targetL * ratio[i];
        }
    }

    // หารอบการตื่นปัจจุบัน
    Calendar getCycleStart() {

        Calendar c = Calendar.getInstance();

        int now = c.get(Calendar.HOUR_OF_DAY) * 60
                + c.get(Calendar.MINUTE);

        if (now < wakeMin) {
            c.add(Calendar.DAY_OF_MONTH, -1);
        }

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c;
    }

    // ตรวจว่ารอบนั้นมีรายการดื่มน้ำหรือไม่
    boolean hasDrinkInCycle(Calendar day) {

        Calendar begin = (Calendar) day.clone();

        begin.set(Calendar.HOUR_OF_DAY, wakeMin / 60);
        begin.set(Calendar.MINUTE, wakeMin % 60);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);

        Calendar finish = (Calendar) begin.clone();
        finish.add(Calendar.MINUTE, end[4] - wakeMin);

        long from = begin.getTimeInMillis();
        long to = finish.getTimeInMillis();

        for (String[] d : allDrinks) {

            try {

                Date date = dateFormat.parse(d[1]);

                if (date != null) {

                    long time = date.getTime();

                    if (time >= from && time < to) {
                        return true;
                    }
                }

            } catch (Exception e) {
                // ข้ามข้อมูลที่อ่านไม่ได้
            }
        }

        return false;
    }

    // คำนวณน้ำที่ดื่มในแต่ละช่วง
    void calculateDrinks(Calendar day) {

        for (int i = 0; i < 5; i++) {
            drank[i] = 0;
        }

        Calendar begin = (Calendar) day.clone();

        begin.set(Calendar.HOUR_OF_DAY, wakeMin / 60);
        begin.set(Calendar.MINUTE, wakeMin % 60);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);

        Calendar finish = (Calendar) begin.clone();
        finish.add(Calendar.MINUTE, end[4] - wakeMin);

        long from = begin.getTimeInMillis();
        long to = finish.getTimeInMillis();

        for (String[] d : allDrinks) {

            try {

                Date date = dateFormat.parse(d[1]);

                if (date == null) {
                    continue;
                }

                long time = date.getTime();

                if (time < from || time >= to) {
                    continue;
                }

                int elapsed = (int) ((time - from) / 60000);

                for (int i = 0; i < 5; i++) {

                    if (elapsed >= start[i] - wakeMin
                            && elapsed < end[i] - wakeMin) {

                        drank[i] += Double.parseDouble(d[2]);
                        break;
                    }
                }

            } catch (Exception e) {
                // ข้ามข้อมูลที่อ่านไม่ได้
            }
        }
    }

    // เวลาปัจจุบันนับจากเวลาตื่น
    int getCurrentOffset() {

        Calendar begin = (Calendar) cycleStart.clone();

        begin.set(Calendar.HOUR_OF_DAY, wakeMin / 60);
        begin.set(Calendar.MINUTE, wakeMin % 60);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);

        int elapsed = (int) (
                (System.currentTimeMillis() - begin.getTimeInMillis())
                        / 60000
        );

        int total = end[4] - wakeMin;

        if (elapsed < 0) {
            elapsed = 0;
        }

        if (elapsed > total) {
            elapsed = total;
        }

        return wakeMin + elapsed;
    }

    // แปลงเวลาและปริมาณน้ำ
    String timeText(int minute) {

        int m = minute % 1440;

        return String.format(Locale.getDefault(), "%02d:%02d", m / 60,m % 60);
    }

    String ml(double liters) {
        return String.format(Locale.getDefault(), "%.0f", liters * 1000);
    }
}
