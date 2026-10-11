
package com.example.jibnam;

public class Screen_1 extends BaseClass {

    private Inp weightInput;
    private Txt targetText;

    private Inp wakeHourInput;
    private Inp wakeMinuteInput;
    private Inp sleepHourInput;
    private Inp sleepMinuteInput;

    @Override
    void build(Box pg) {

        // ระยะขอบของหน้า
        pad(pg, 16, 16, 16, 24);

        // ชื่อแอป
        Txt title = txt("JIBNAM");
        title.setTextSize(48);
        alg(title, A_C);
        add(pg, title);
        mar(title, 0, 8, 0, 4);

        // ข้อความใต้ชื่อแอป
        Txt subtitle = txt("ตั้งค่าเริ่มต้น");
        subtitle.setTextSize(24);
        alg(subtitle, A_C);
        add(pg, subtitle);
        mar(subtitle, 0, 0, 0, 24);

        // =========================
        // น้ำหนักตัว
        // =========================
        add(pg, txt("น้ำหนักตัว (กิโลกรัม)"));

        weightInput = numInp();
        add(pg, weightInput, 150, 48);
        mar(weightInput, 0, 6, 0, 12);

        // =========================
        // ปุ่มคำนวณปริมาณน้ำ
        // =========================
        Btn calculateButton = btn("คำนวณปริมาณน้ำ");
        add(pg, calculateButton, 250, 48);
        mar(calculateButton, 0, 0, 0, 12);
        onTap(calculateButton, this::calculateTarget);

        // =========================
        // ผลการคำนวณ
        // =========================
        targetText = txt("ปริมาณน้ำที่แนะนำจะแสดงที่นี่");
        targetText.setTextSize(20);
        alg(targetText, A_C);
        add(pg, targetText);
        mar(targetText, 0, 24, 0, 48);

        // =========================
        // เวลาตื่นนอน
        // =========================
        add(pg, txt("เวลาตื่นนอน"));

        Box wakeRow = row();

        wakeHourInput = numInp();
        addW(wakeRow, wakeHourInput, 1);

        Txt wakeHourLabel = txt("ชั่วโมง");
        add(wakeRow, wakeHourLabel, WRAP, WRAP);
        mar(wakeHourLabel, 4, 0, 12, 0);

        wakeMinuteInput = numInp();
        addW(wakeRow, wakeMinuteInput, 1);

        Txt wakeMinuteLabel = txt("นาที");
        add(wakeRow, wakeMinuteLabel, WRAP, WRAP);
        mar(wakeMinuteLabel, 4, 0, 0, 0);

        add(pg, wakeRow, 200, 48);
        mar(wakeRow, 0, 6, 0, 20);

        // =========================
        // เวลาเข้านอน
        // =========================
        add(pg, txt("เวลาเข้านอน"));

        Box sleepRow = row();

        sleepHourInput = numInp();
        addW(sleepRow, sleepHourInput, 1);

        Txt sleepHourLabel = txt("ชั่วโมง");
        add(sleepRow, sleepHourLabel, WRAP, WRAP);
        mar(sleepHourLabel, 4, 0, 12, 0);

        sleepMinuteInput = numInp();
        addW(sleepRow, sleepMinuteInput, 1);

        Txt sleepMinuteLabel = txt("นาที");
        add(sleepRow, sleepMinuteLabel, WRAP, WRAP);
        mar(sleepMinuteLabel, 4, 0, 0, 0);

        add(pg, sleepRow, 200, 48);
        mar(sleepRow, 0, 6, 0, 24);

        // =========================
        // ปุ่มเริ่มต้นใช้งาน
        // =========================
        Btn submitButton = btn("เริ่มต้นใช้งาน");
        add(pg, submitButton, 250, 48);
        onTap(submitButton, this::submit);
    }

    // คำนวณปริมาณน้ำโดยประมาณ
    private void calculateTarget() {

        Double weight = readWeight();

        if (weight == null) {
            return;
        }

        double liters = weight * 33.0 / 1000.0;
        double rounded = Math.round(liters * 100.0) / 100.0;

        setTxt(
                targetText,
                "ปริมาณน้ำโดยประมาณ "
                        + rounded + " ลิตร/วัน"
        );
    }

    // อ่านและตรวจสอบน้ำหนัก
    private Double readWeight() {

        String value = getTxt(weightInput);

        if (value.isEmpty()) {
            setTxt(targetText, "กรุณากรอกน้ำหนัก");
            return null;
        }

        try {
            double weight = Double.parseDouble(value);

            if (weight <= 0 || weight > 500) {
                setTxt(targetText, "กรุณาตรวจสอบน้ำหนัก");
                return null;
            }

            return weight;

        } catch (NumberFormatException e) {
            setTxt(targetText, "กรุณากรอกน้ำหนักเป็นตัวเลข");
            return null;
        }
    }

    // อ่านค่าตัวเลขจากช่องกรอก
    private Integer readInt(Inp input) {

        String value = getTxt(input);

        if (value.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(value);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ตรวจสอบชั่วโมงและนาที
    private boolean validTime(int hour, int minute) {

        return hour >= 0 && hour <= 23
                && minute >= 0 && minute <= 59;
    }

    // ตรวจสอบข้อมูลก่อนเปิด Screen_2
    private void submit() {

        Double weight = readWeight();

        if (weight == null) {
            return;
        }

        Integer wakeHour = readInt(wakeHourInput);
        Integer wakeMinute = readInt(wakeMinuteInput);
        Integer sleepHour = readInt(sleepHourInput);
        Integer sleepMinute = readInt(sleepMinuteInput);

        if (wakeHour == null || wakeMinute == null
                || sleepHour == null || sleepMinute == null) {

            setTxt(
                    targetText,
                    "กรุณากรอกเวลาตื่นนอนและเข้านอน"
            );
            return;
        }

        if (!validTime(wakeHour, wakeMinute)
                || !validTime(sleepHour, sleepMinute)) {

            setTxt(targetText, "กรุณากรอกเวลาให้ถูกต้อง");
            return;
        }

        // รวมข้อมูลเพื่อส่งไป Screen_2
        String data = weight + "|"
                + wakeHour + "|"
                + wakeMinute + "|"
                + sleepHour + "|"
                + sleepMinute;

        goWith(Screen_2.class, "screen1Data", data);
    }
}
