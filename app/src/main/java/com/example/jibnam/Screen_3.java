package com.example.jibnam;

public class Screen_3 extends BaseClass {

    // ตัวแปรเก็บระดับการออกกำลังกายและเวลา
    private int level = 0;
    private int timeH = 0;
    private int timeM = 0;
    private int waterplus = 0;

    // ตัวแปรเชื่อมกับช่องกรอกและข้อความ
    private Inp hourInput;
    private Inp minuteInput;
    private Txt levelText;
    private Txt resultText;

    @Override
    void build(Box pg) {

        // ===== หัวข้อเลือกระดับการออกกำลังกาย =====
        levelText = txt("เลือกระดับการออกกำลังกาย");
        levelText.setTextSize(18);
        alg(levelText, A_C);

        add(pg, levelText);
        mar(levelText, 0, 0, 0, 16);

        // ===== ปุ่มระดับน้อย =====
        Btn lowButton = btn("น้อย");
        add(pg, lowButton, 220, 48);
        alg(lowButton, A_C);
        mar(lowButton, 0, 0, 0, 10);

        // ===== ปุ่มระดับกลาง =====
        Btn mediumButton = btn("กลาง");
        add(pg, mediumButton, 220, 48);
        alg(mediumButton, A_C);
        mar(mediumButton, 0, 0, 0, 10);

        // ===== ปุ่มระดับมาก =====
        Btn highButton = btn("มาก");
        add(pg, highButton, 220, 48);
        alg(highButton, A_C);
        mar(highButton, 0, 0, 0, 24);

        // เมื่อกดปุ่ม ให้บันทึกระดับ
        onTap(lowButton, () -> {
            level = 1;
            setTxt(levelText, "ระดับการออกกำลังกาย: น้อย");
        });

        onTap(mediumButton, () -> {
            level = 2;
            setTxt(levelText, "ระดับการออกกำลังกาย: กลาง");
        });

        onTap(highButton, () -> {
            level = 3;
            setTxt(levelText, "ระดับการออกกำลังกาย: มาก");
        });

        // ===== หัวข้อเวลาออกกำลังกาย =====
        Txt timeTitle = txt("เวลาออกกำลังกาย");
        timeTitle.setTextSize(18);
        alg(timeTitle, A_C);

        add(pg, timeTitle);
        mar(timeTitle, 0, 0, 0, 12);

        // ===== ช่องกรอกชั่วโมงและนาที =====
        hourInput = numInp();
        minuteInput = numInp();

        hourInput.setTextSize(16);
        minuteInput.setTextSize(16);

        // สร้างข้อความกำกับช่องกรอก
        Txt hourLabel = txt("ชั่วโมง");
        Txt minuteLabel = txt("นาที");

        hourLabel.setTextSize(16);
        minuteLabel.setTextSize(16);

        // ===== แถวชั่วโมง =====
        Box hourRow = row();
        hourRow.setGravity(A_C);

        add(hourRow, hourInput, 120, WRAP);
        add(hourRow, hourLabel, 80, WRAP);

        // ===== แถวนาที =====
        Box minuteRow = row();
        minuteRow.setGravity(A_C);

        add(minuteRow, minuteInput, 120, WRAP);
        add(minuteRow, minuteLabel, 80, WRAP);

        // ===== รวมสองแถวและจัดให้อยู่กึ่งกลาง =====
        Box timeRow = col();
        timeRow.setGravity(A_C);

        add(timeRow, hourRow);
        add(timeRow, minuteRow);
        add(pg, timeRow);

        // ===== ปุ่ม Submit =====
        Btn submitButton = btn("Submit");
        add(pg, submitButton, 220, 48);
        alg(submitButton, A_C);
        mar(submitButton, 0, 0, 0, 12);

        // ===== ข้อความแสดงผล =====
        resultText = txt("");
        resultText.setTextSize(16);
        alg(resultText, A_C);

        add(pg, resultText);
        mar(resultText, 0, 8, 0, 0);

        // เมื่อกด Submit ให้คำนวณปริมาณน้ำ
        onTap(submitButton, () -> calculateWater());

        // ===== ปุ่มย้อนกลับไป Screen_2 =====
        Btn backButton = btn("ย้อนกลับ");
        add(pg, backButton, 220, 48);
        alg(backButton, A_C);
        mar(backButton, 0, 12, 0, 0);

// เมื่อกดปุ่ม ให้กลับไป Screen_2
        onTap(backButton, () -> go(Screen_2.class));
    }

    // ===== คำนวณปริมาณน้ำที่ต้องดื่มเพิ่ม =====
    private void calculateWater() {

        String hourText = getTxt(hourInput).trim();
        String minuteText = getTxt(minuteInput).trim();

        // ตรวจสอบว่ากรอกเวลาอย่างน้อยหนึ่งช่อง
        if (hourText.isEmpty() && minuteText.isEmpty()) {
            setTxt(resultText, "กรุณากรอกเวลา");
            return;
        }

        try {

            // แปลงข้อความเป็นตัวเลข
            if (hourText.isEmpty()) {
                timeH = 0;
            } else {
                timeH = Integer.parseInt(hourText);
            }

            if (minuteText.isEmpty()) {
                timeM = 0;
            } else {
                timeM = Integer.parseInt(minuteText);
            }

            // ตรวจสอบความถูกต้องของเวลา
            if (timeH < 0 || timeM < 0 || timeM >= 60) {
                setTxt(resultText, "กรุณากรอกเวลาให้ถูกต้อง");
                return;
            }

            // ตรวจสอบว่าเลือกระดับการออกกำลังกายแล้วหรือยัง
            if (level == 0) {
                setTxt(resultText, "กรุณาเลือกระดับการออกกำลังกาย");
                return;
            }

            // คำนวณปริมาณน้ำ
            waterplus = CalWaterPlus(level, timeH, timeM);

            setTxt(
                    resultText, "\nปริมาณน้ำตามที่ต้องดื่มเพิ่มประมาณ " + waterplus + " ml"
            );

        } catch (NumberFormatException e) {
            setTxt(resultText, "กรุณากรอกตัวเลขให้ถูกต้อง");
        }
    }

    // ===== สูตรคำนวณปริมาณน้ำ =====
    private int CalWaterPlus(int level, int timeH, int timeM) {

        // แปลงเวลาเป็นนาทีทั้งหมด
        int totalMinutes = (timeH * 60) + timeM;

        // อัตราน้ำที่ใช้คำนวณตามระดับ
        int waterRate = level * 5;

        // ปริมาณน้ำที่ต้องดื่มเพิ่ม
        int waterplus = waterRate * totalMinutes;

        return waterplus;
    }
}
