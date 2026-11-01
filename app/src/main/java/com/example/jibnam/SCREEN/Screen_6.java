
package com.example.jibnam.SCREEN;

import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Screen_6 extends BaseClass {

    Data data;

    int wakeMin, sleepMin;
    double targetL;

    double[] ratio = {0.25, 0.25, 0.25, 0.25, 0.25};
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

        allDrinks = data.getDrinks(null);

        calculateDrinks(cycleStart);

        // หัวข้อ
        bold(showTxt(pg, "สรุปการดื่มน้ำ",
                24, 10, 15, 340, 45));

        // ยอดน้ำที่ดื่มแล้ว
        double total = 0;

        for (int i = 0; i < 5; i++) {
            total += drank[i];
        }

        bold(showTxt(pg,
                "ดื่มแล้ว " + WaterCalculate.L(total)
                        + " / " + WaterCalculate.L(targetL),
                20, 10, 62, 340, 40));

        bold(showTxt(pg, "รายละเอียดการดื่มแต่ละช่วง",
                20, 10, 105, 340, 40));

        // แสดง 5 ช่วงเวลา
        int nowOffset = getCurrentOffset();

        for (int i = 0; i < 5; i++) {

            String text = getEmoji(i, nowOffset)
                    + " ช่วงที่ " + (i + 1)
                    + " : " + timeText(start[i])
                    + "-" + timeText(end[i])
                    + " น.\nเป้าหมาย " + WaterCalculate.ml(goal[i])
                    + " ดื่มไป " + WaterCalculate.ml(drank[i]);

            Col itemBox = col();
            border(itemBox);

            Txt item = txt(text);
            item.setTextSize(18);
            item.setGravity(17);
            item.setPadding(5, 0, 5, 0);
            item.setIncludeFontPadding(false);

            add(itemBox, item, u(330), u(64));

            put(pg, itemBox,
                    u(10), u(152 + i * 72),
                    u(340), u(64));
        }

        // วิเคราะห์ย้อนหลัง 7 รอบล่าสุด
        double[] weeklyVolume = new double[5];
        int[] drankDays = new int[5];

        List<Calendar> last7Cycles = getLast7Cycles();

        boolean complete7Days = last7Cycles.size() >= 7;

        if (complete7Days) {

            for (int d = 0; d < 7; d++) {

                calculateDrinks(last7Cycles.get(d));

                for (int i = 0; i < 5; i++) {

                    weeklyVolume[i] += drank[i];

                    if (drank[i] > 0) {
                        drankDays[i]++;
                    }
                }
            }
        }

        // กลับมาคำนวณรอบปัจจุบัน
        calculateDrinks(cycleStart);

        int y = 152 + 5 * 72 + 6;

        bold(showTxt(pg, "วิเคราะห์การดื่มย้อนหลัง 7 วัน",
                20, 10, y, 340, 40));

        y += 45;

        if (!complete7Days) {

            showTxt(pg, "กรุณาใช้แอปนี้ถึง 7 วันก่อน",
                    18, 10, y, 340, 40);

            y += 50;

        } else {

            y = showWeekly(pg, weeklyVolume, drankDays, y);
        }

        // คำเตือน
        bold(showTxt(pg, "⚠️ คำเตือนเรื่องการดื่มน้ำ",
                20, 10, y, 340, 40));

        y += 42;

        String[] warn = {
                "ดื่มน้ำมากเกินไป วันละ 6-7 ลิตร",
                "อาจเป็นอันตรายถึงขั้นเสียชีวิตได้",
                "ดื่มน้ำน้อยเกินไป เสี่ยงเกิดโรคต่าง ๆ",
                "เช่น โรคนิ่ว และทางเดินปัสสาวะอักเสบ"
        };

        for (int i = 0; i < warn.length; i++) {
            showTxt(pg, warn[i], 18, 10, y, 340, 30);
            y += 30;
        }

        // ปุ่มกลับ
        Btn back = btn("กลับ");
        placeBtn(pg, back, 0, y + 15, 200, 50);
        onTap(back, () -> finish());
    }

    // แสดงข้อความ
    Txt showTxt(Box pg, String text, int size,
                int x, int y, int w, int h) {

        Txt v = txt(text);
        v.setTextSize(size);
        v.setGravity(17);

        placeTxt(pg, v, x, y, w, h);

        return v;
    }

    // ตรวจสอบ Emoji
    String getEmoji(int i, int now) {

        if (now >= start[i] && now < end[i]) {
            return "👉";
        }

        if (drank[i] == 0) {
            return "⬜";
        }

        if (drank[i] >= goal[i] * 1.2) {
            return "🟦";
        }

        if (drank[i] >= goal[i]) {
            return "🟩";
        }

        if (drank[i] >= goal[i] * 0.5) {
            return "🟨";
        }

        return "🟥";
    }

    // อ่านวันของรอบจากเวลาที่แสดงในประวัติ
    Calendar getDrinkCycle(String[] d) throws Exception {

        Date date = dateFormat.parse(d[1]);

        if (date == null) {
            return null;
        }

        Calendar drinkTime = Calendar.getInstance();
        drinkTime.setTime(date);

        int hour = Integer.parseInt(d[3]);
        int minute = Integer.parseInt(d[4]);

        drinkTime.set(Calendar.HOUR_OF_DAY, hour);
        drinkTime.set(Calendar.MINUTE, minute);

        int drinkMinute = hour * 60 + minute;

        if (drinkMinute < wakeMin) {
            drinkTime.add(Calendar.DAY_OF_MONTH, -1);
        }

        drinkTime.set(Calendar.HOUR_OF_DAY, 0);
        drinkTime.set(Calendar.MINUTE, 0);
        drinkTime.set(Calendar.SECOND, 0);
        drinkTime.set(Calendar.MILLISECOND, 0);

        return drinkTime;
    }

    // หารอบ 7 รอบล่าสุดที่มีประวัติการดื่ม
    List<Calendar> getLast7Cycles() {

        List<Calendar> cycles = new ArrayList<>();

        for (String[] d : allDrinks) {

            try {

                Calendar drinkTime = getDrinkCycle(d);

                if (drinkTime == null) continue;

                boolean exists = false;

                for (Calendar oldDay : cycles) {

                    if (oldDay.get(Calendar.YEAR)
                            == drinkTime.get(Calendar.YEAR)
                            && oldDay.get(Calendar.DAY_OF_YEAR)
                            == drinkTime.get(Calendar.DAY_OF_YEAR)) {

                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    cycles.add(drinkTime);
                }

            } catch (Exception e) {
                // ข้ามข้อมูลที่อ่านไม่ได้
            }
        }

        // เรียงจากใหม่ไปเก่า
        for (int i = 0; i < cycles.size(); i++) {

            for (int j = i + 1; j < cycles.size(); j++) {

                if (cycles.get(j).after(cycles.get(i))) {

                    Calendar temp = cycles.get(i);
                    cycles.set(i, cycles.get(j));
                    cycles.set(j, temp);
                }
            }
        }

        List<Calendar> result = new ArrayList<>();

        for (int i = 0; i < cycles.size() && i < 7; i++) {
            result.add(cycles.get(i));
        }

        return result;
    }

    // วิเคราะห์ย้อนหลัง 7 รอบ
    int showWeekly(Box pg, double[] volume, int[] days, int y) {

        double max = volume[0];
        double min = volume[0];

        int maxMissed = 7 - days[0];

        for (int i = 1; i < 5; i++) {

            if (volume[i] > max) max = volume[i];
            if (volume[i] < min) min = volume[i];

            if (7 - days[i] > maxMissed) {
                maxMissed = 7 - days[i];
            }
        }

        String most = "💧 ดื่มน้ำมากที่สุด: ";
        String least = "💧 ดื่มน้ำน้อยที่สุด: ";
        String missed = "ขาดการดื่มบ่อยที่สุด: ";

        for (int i = 0; i < 5; i++) {

            if (volume[i] == max) {
                most += periodName[i] + " (ดื่ม "
                        + days[i] + "/7 วัน) ";
            }

            if (volume[i] == min) {
                least += periodName[i] + " (ดื่ม "
                        + days[i] + "/7 วัน) ";
            }

            if (7 - days[i] == maxMissed) {
                missed += periodName[i] + " ("
                        + maxMissed + " วัน) ";
            }
        }

        showTxt(pg, most, 18, 10, y, 340, 60);
        showTxt(pg, least, 18, 10, y + 62, 340, 60);
        showTxt(pg, missed, 18, 10, y + 124, 340, 60);

        return y + 190;
    }

    // แปลงขนาดและวางตำแหน่ง
    int u(int value) {
        return value * getResources()
                .getDisplayMetrics().widthPixels / 360;
    }

    void placeTxt(Box p, Txt v, int x, int y, int w, int h) {
        int height = h == WRAP ? WRAP : u(h);
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
                end[i] = wakeMin
                        + (int) Math.round(total * accumulated);
            }

            current = end[i];
            goal[i] = targetL * ratio[i];
        }
    }

    // หาวันเริ่มต้นของรอบปัจจุบัน
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

    // คำนวณน้ำที่ดื่มในแต่ละช่วง
    void calculateDrinks(Calendar day) {

        for (int i = 0; i < 5; i++) {
            drank[i] = 0;
        }

        for (String[] d : allDrinks) {

            try {

                Calendar drinkTime = getDrinkCycle(d);

                if (drinkTime == null) continue;

                if (drinkTime.get(Calendar.YEAR)
                        != day.get(Calendar.YEAR)
                        || drinkTime.get(Calendar.DAY_OF_YEAR)
                        != day.get(Calendar.DAY_OF_YEAR)) {
                    continue;
                }

                int hour = Integer.parseInt(d[3]);
                int minute = Integer.parseInt(d[4]);

                int drinkMinute = hour * 60 + minute;
                int elapsed = drinkMinute - wakeMin;

                if (elapsed < 0) {
                    elapsed += 1440;
                }

                if (elapsed >= end[4] - wakeMin) continue;

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

        Calendar now = Calendar.getInstance();

        int currentMinute =
                now.get(Calendar.HOUR_OF_DAY) * 60
                        + now.get(Calendar.MINUTE);

        int elapsed = currentMinute - wakeMin;

        if (elapsed < 0) {
            elapsed += 1440;
        }

        int total = end[4] - wakeMin;

        if (elapsed > total) {
            elapsed = total;
        }

        return wakeMin + elapsed;
    }

    // แปลงนาทีเป็นเวลา
    String timeText(int minute) {

        int m = minute % 1440;

        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                m / 60,
                m % 60
        );
    }
}
