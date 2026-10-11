package com.example.jibnam;

public class Screen_3 extends BaseClass {

    // ===== ตัวแปรเก็บข้อมูล =====
    private int level = 0;
    private int timeH = 0;
    private int timeM = 0;

    private int waterplus = 0;

    // ===== ตัวแปร UI =====
    private Inp hourInput;
    private Inp minuteInput;
    private Txt levelText;
    private Txt resultText;

    @Override
    void build(Box pg) {

        // ===== ข้อความแสดงระดับ =====
        levelText = txt("เลือกระดับการออกกำลังกาย");
        levelText.setTextSize(18);

        alg(levelText, A_C);
        add(pg, levelText);
        mar(levelText, 0, 0, 0, 16);

        // ===== ปุ่มน้อย =====
        Btn lowButton = btn("น้อย");
        add(pg, lowButton, 220, 48);
        alg(lowButton, A_C);
        mar(lowButton, 0, 0, 0, 10);

        // ===== ปุ่มกลาง =====
        Btn mediumButton = btn("กลาง");
        add(pg, mediumButton, 220, 48);
        alg(mediumButton, A_C);
        mar(mediumButton, 0, 0, 0, 10);

        // ===== ปุ่มมาก =====
        Btn highButton = btn("มาก");
        add(pg, highButton, 220, 48);
        alg(highButton, A_C);
        mar(highButton, 0, 0, 0, 24);

        // ===== เมื่อกดเลือกระดับ =====
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

        // ===== หัวข้อเวลา =====
        Txt timeTitle = txt("เวลาออกกำลังกาย");
        timeTitle.setTextSize(18);

        alg(timeTitle, A_C);
        add(pg, timeTitle);
        mar(timeTitle, 0, 0, 0, 12);

        // ===== ช่องกรอกชั่วโมงและนาที =====
        Box timeRow = row();

        hourInput = numInp();
        hourInput.setHint("ชั่วโมง");
        hourInput.setSingleLine(true);

        Txt colon = txt(":");
        colon.setTextSize(22);


        minuteInput = numInp();
        minuteInput.setHint("นาที");
        minuteInput.setSingleLine(true);

        addW(timeRow, hourInput, 1);
        add(timeRow, colon, 24, 48);
        addW(timeRow, minuteInput, 1);

        alg(timeRow, A_C);
        add(pg, timeRow, 260, 52);
        mar(timeRow, 0, 0, 0, 20);

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

        onTap(submitButton, () -> calculateWater());
    }

    // ===== อ่านเวลาและคำนวณ =====
    private void calculateWater() {

        String hourText = getTxt(hourInput).trim();
        String minuteText = getTxt(minuteInput).trim();

        // ไม่กรอกทั้งชั่วโมงและนาที
        if (hourText.isEmpty() && minuteText.isEmpty()) {
            setTxt(resultText, "กรุณากรอกเวลา");
            return;
        }

        try {
            // ไม่กรอกช่องไหน ให้ค่านั้นเป็น 0

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

            // ตรวจสอบเวลา
            if (timeH < 0 || timeM < 0 || timeM >= 60) {
                setTxt(resultText,
                        "กรุณากรอกเวลาให้ถูกต้อง");
                return;
            }

            // ต้องเลือกระดับก่อนคำนวณ
            if (level == 0) {
                setTxt(resultText,
                        "กรุณาเลือกระดับการออกกำลังกาย");
                return;
            }

            // คำนวณปริมาณน้ำ
            waterplus = CalWaterPlus(level, timeH, timeM);

            // แสดงผล
            setTxt(resultText, "\nปริมาณน้ำตามที่ต้องดื่มเพิ่ม: " + waterplus + " ml");

        } catch (NumberFormatException e) {
            setTxt(resultText, "กรุณากรอกตัวเลขให้ถูกต้อง");
        }
    }

    // ===== ฟังก์ชันคำนวณปริมาณน้ำ =====
    private int CalWaterPlus(int level, int timeH, int timeM) {

        // แปลงชั่วโมงเป็นนาทีแล้วรวมกับนาทีที่กรอก
        int totalMinutes = (timeH * 60) + timeM;

        // ระดับ 1 = 5, ระดับ 2 = 10, ระดับ 3 = 15 มล./นาที
        int waterRate = level * 5;

        // ปริมาณน้ำที่คำนวณได้
        int waterplus = waterRate * totalMinutes;

        return waterplus;
    }
}
