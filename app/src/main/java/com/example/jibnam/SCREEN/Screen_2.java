package com.example.jibnam.SCREEN;

import com.example.jibnam.R;            // รูปใน res/drawable เช่น R.drawable.bottle_0
import com.example.jibnam.CODE.*;       // BaseClass, WaterCalculate
import com.example.jibnam.DATA.*;       // Data, DataApi
import java.util.*;                     // List, Locale

/** Screen_2 : หน้าหลัก บันทึกดื่มน้ำ — หน้าจอ + ข้อมูล + คำนวณ อยู่ในไฟล์นี้ */
public class Screen_2 extends BaseClass {

    // ===== ข้อมูล (ดึงจากไฟล์ตอนเปิดหน้า) =====
    double wtKg;                         // น้ำหนักตัว (กก.) จากบรรทัดล่างสุด
    double tgL;                          // เป้าน้ำที่ควรดื่ม (ลิตร) = WaterCalculate.targetL(wtKg)
    static final int[] CUP_IMG = {R.drawable.b_cone, R.drawable.b_glass, R.drawable.b_bottle_s};   // รูปปุ่ม กรวย 115ml แก้ว 250ml ขวดเล็ก 600ml
    static final int[] CUP_ML = {115, 250, 600};                                                     // กดแล้วใส่ช่องกรอก (ml)

    Data data;                           // ไฟล์ RealData.txt (ข้อมูลจริงอยู่ในไฟล์ หน้าจอดึงมาโชว์อย่างเดียว)
    double drkL;                         // ดื่มแล้ววันนี้ (ลิตร) รวมจากไฟล์ทุกครั้งที่ refresh

    static final int[] BTL = {           // รูปขวด 0% ถึง 100% ทีละ 10
            R.drawable.bottle_0, R.drawable.bottle_10, R.drawable.bottle_20, R.drawable.bottle_30,
            R.drawable.bottle_40, R.drawable.bottle_50, R.drawable.bottle_60, R.drawable.bottle_70,
            R.drawable.bottle_80, R.drawable.bottle_90, R.drawable.bottle_100,
    };

    // ===== ชิ้นส่วนหน้าจอ =====
    Txt plusTx;     // "เพิ่มน้ำ : +115 ml สำเร็จ" ว่างตอนแรก กด submit แล้วค้างไว้
    Txt drankTx;    // "ดื่มแล้ว 1.46 L / 2.10 L"
    Txt pctTx;      // % บนรูปขวด
    Img btlImg;     // รูปขวด
    Img streakImg;  // หยดน้ำ Streak (รูปเปลี่ยนตามวันต่อเนื่อง) กดแล้วไป Screen_3
    Txt streakTx;   // จำนวนวันต่อเนื่อง "3💧" ใต้รูป
    Inp amtIn;      // ช่องกรอกปริมาณ
    Box hisBox;     // กล่องประวัติ (วาดใหม่ทุกครั้งที่ refresh)
    Img notiBtn;    // ปุ่มรูป แจ้งเตือน : เปิด / ปิด

    int testI = 0;                                    // ปุ่มทดสอบ: กดครั้งถัดไปจะโชว์ข้อความที่เท่าไหร่

    @Override
    protected void build(Box pg) {          // วางของแบบนับพิกัด put(pg, ของ, x, y, กว้าง, สูง) หน่วย px
        // ดึงข้อมูลผู้ใช้จากไฟล์ RealData.txt
        data = new Data(DataApi.realDataFile(this));
        if (data.lastUser() == null)                                   // ชั่วคราว: ยังไม่มีข้อมูลผู้ใช้ ใส่ค่าตัวอย่างไว้ก่อน
            data.saveUser(60, 7, 0, 23, 0);                            //   (ลบได้เมื่อ Screen_1 เซฟข้อมูลผู้ใช้จริง)
        wtKg = Double.parseDouble(data.lastUser()[1]);
        tgL = WaterCalculate.targetL(wtKg);

        // ===== ปุ่มด้านบน 2 แถว แถวละ 2 ปุ่ม กว้างเท่ากัน =====
        Img toS1 = imgBtn(R.drawable.b_profile);         // แก้ไขข้อมูลส่วนตัว
        onTap(toS1, () -> go(Screen_1.class));
        put(pg, toS1, 30, 30, WRAP, WRAP);
        Img toS6 = imgBtn(R.drawable.b_summary);         // สรุปการดื่มน้ำ
        onTap(toS6, () -> go(Screen_6.class));
        put(pg, toS6, 550, 30, WRAP, WRAP);

        notiBtn = imgBtn(R.drawable.b_noti_on);                       // กดสลับ เปิด/ปิด แล้วเซฟลงไฟล์
        onTap(notiBtn, () -> {
            data.setNoti(!data.notiOn());
            showNoti();
        });
        put(pg, notiBtn, 30, 175, WRAP, WRAP);
        showNoti();
        Img testBtn = imgBtn(R.drawable.b_test);      // กด 1 ครั้ง = เด้ง 1 ข้อความ ไล่ครบแล้ววนใหม่
        onTap(testBtn, () -> testNoti());
        put(pg, testBtn, 550, 175, WRAP, WRAP);

        // ===== ข้อมูล + รูปขวด อยู่กลางจอ =====
        centerX(pg, txt("น้ำหนัก " + fmt(wtKg) + " กก.    ควรดื่ม " + WaterCalculate.L(tgL)), 0, 335, WRAP, WRAP);
        drankTx = txt("");
        drankTx.setTextSize(20);
        bold(drankTx);
        centerX(pg, drankTx, 0, 405, WRAP, WRAP);

        btlImg = img();
        centerX(pg, btlImg, 0, 490, 390, 540);
        pctTx = txt("");
        bold(pctTx);
        centerX(pg, pctTx, 0, 790, WRAP, WRAP);  // ทับกลางขวด

        streakImg = imgBtn(R.drawable.ch_cry);           // หยดน้ำ Streak ขวาของขวด กดแล้วไป Screen_3
        onTap(streakImg, () -> go(Screen_3.class));
        put(pg, streakImg, 810, 560, 230, 230);
        streakTx = txt("");                              // "3💧" ใต้รูป
        streakTx.setTextSize(24);
        bold(streakTx);
        streakTx.setGravity(1);                          // 1 = ตรงกลางแนวนอน
        onTap(streakTx, () -> go(Screen_3.class));
        put(pg, streakTx, 810, 800, 230, WRAP);

        plusTx = txt("");                        // "เพิ่มน้ำ : +115 ml สำเร็จ" ว่างตอนแรก
        bold(plusTx);
        centerX(pg, plusTx, 0, 1045, WRAP, WRAP);

        // ===== ปุ่มภาชนะ 3 ปุ่มแถวเดียว กว้าง 240 ห่างกัน 60 อยู่กลางจอ =====
        for (int i = 0; i < CUP_ML.length; i++) {
            cupBtn(pg, i, 120 + i * 300, 1120);
        }

        // ===== ช่องกรอก ml + submit แถวเดียวกัน =====
        amtIn = numInp();
        put(pg, amtIn, 150, 1415, 360, WRAP);
        put(pg, txt("ml"), 525, 1455, WRAP, WRAP);
        Img subBtn = imgBtn(R.drawable.b_save);         // บันทึก
        onTap(subBtn, () -> submit());
        put(pg, subBtn, 650, 1400, WRAP, WRAP);

        // ===== ประวัติวันนี้ =====
        Txt hisTitle = txt("ประวัติ (วันนี้)");
        bold(hisTitle);
        put(pg, hisTitle, 30, 1610, WRAP, WRAP);
        Img moreBtn = imgBtn(R.drawable.b_history);     // Water History
        onTap(moreBtn, () -> go(Screen_4.class));
        put(pg, moreBtn, 600, 1580, WRAP, WRAP);

        hisBox = box();                          // กล่องประวัติ (วาดใหม่ใน refresh)
        put(pg, hisBox, 0, 1730, WRAP, WRAP);

        amtIn.setText("" + CUP_ML[0]);  // เริ่มต้นใส่ปริมาณภาชนะแรก
        refresh();
        Notification.start(this);        // ขอสิทธิ์แจ้งเตือน + ตั้งเตือนดื่มน้ำรอบถัดไป (CODE/Notification)
    }

    // ===== หน้าจอ =====

    void submit() {                      // กด submit: เขียนลงไฟล์ → ดึงจากไฟล์มาโชว์ใหม่ → โชว์ เพิ่มน้ำ +x ml
        double v;
        try {
            v = Double.parseDouble(amtIn.getText().toString().trim());
        } catch (NumberFormatException e) {
            return;                      // ว่าง / ไม่ใช่ตัวเลข → ไม่ทำอะไร
        }
        if (v <= 0) return;

        double l = v / 1000.0;           // ml → ลิตร (ในไฟล์เก็บเป็นลิตร)
        data.addDrink(l, tgL);           // เก็บลงไฟล์ (Data ใส่ serial เวลาตอนนี้ + ยอดดื่มทั้งวันให้เอง)
        refresh();
        plusTx.setText("เพิ่มน้ำ : +" + WaterCalculate.ml(l) + " สำเร็จ");   // ค้างไว้จนกด submit ครั้งต่อไป
    }

    void refresh() {                     // ดึงแก้ววันนี้จากไฟล์ แล้ววาดประวัติ ยอดดื่ม รูปขวด %
        List<String[]> ds = data.getDrinks(Data.today());          // d = { เวลาบันทึก, น้ำหนัก, ตื่น ชม., นาที, นอน ชม., นาที, ลิตร, ชม., นาที, เป้า, รวมวันนั้น, แจ้งเตือน }
        clr(hisBox);
        drkL = 0;
        int row = 0;                                               // แถวที่ 0 อยู่บนสุด
        for (int k = ds.size() - 1; k >= 0; k--) {                 // ท้ายไฟล์ = แก้วล่าสุด อยู่บนสุด
            String[] d = ds.get(k);
            double l = Double.parseDouble(d[6]);
            drkL += l;
            hisCard(row, d[0], WaterCalculate.ml(l) + "   เวลา " + hm(d[7], d[8]));
            row++;
        }
        drankTx.setText("ดื่มแล้ว " + WaterCalculate.L(drkL) + " / " + WaterCalculate.L(tgL));
        btlImg.setImageResource(BTL[pct() / 10]);  // รูปเปลี่ยนทีละ 10%
        pctTx.setText(pctTxt());          // % ทศนิยม 2 ตำแหน่ง
        Screen_3.calc(data);              // วันต่อเนื่อง (ระบบ streak อยู่ใน Screen_3)
        setImg(streakImg, Screen_3.pic);
        setTxt(streakTx, Screen_3.days + "💧");
    }

    void testNoti() {                    // กดปุ่มทดสอบ: เด้งข้อความที่ testI แล้วเลื่อนไปข้อความถัดไป (ครบแล้วกลับไปแบบแรก)
        String[] all = Notification.allMsgs();
        Notification.show(this, "ทดสอบ " + (testI + 1) + "/" + all.length + " " + all[testI]);
        testI++;
        if (testI >= all.length) testI = 0;
    }

    @Override
    protected void onResume() {          // กลับมาหน้านี้ (จาก Screen_1 4 5 6) = ดึงข้อมูลจากไฟล์ใหม่ ประวัติที่แก้/ลบจะอัปเดตเอง
        super.onResume();
        if (data != null) {
            refresh();
            showNoti();
        }
    }

    void showNoti() {                    // ข้อความบนปุ่มแจ้งเตือน ดึงจากไฟล์
        if (data.notiOn()) {
            setImg(notiBtn, R.drawable.b_noti_on);
        } else {
            setImg(notiBtn, R.drawable.b_noti_off);
        }
    }

    void cupBtn(Box pg, int i, int x, int y) {   // ปุ่มภาชนะที่ i ที่ (x, y): กดแล้วเติมปริมาณ (ml) ในช่องกรอก
        Img b = imgBtn(CUP_IMG[i]);
        put(pg, b, x, y, WRAP, WRAP);
        onTap(b, () -> amtIn.setText("" + CUP_ML[i]));
    }

    /*
     * ปุ่ม "แก้ไข" → ส่ง serial ของแก้วนี้ ไป Screen_5
     * Screen_5 รับแล้วใช้ต่อ:
     *   Data data = new Data(DataApi.realDataFile(this));
     *   String sn = arg("serial");                     // แก้วที่ถูกกดแก้ไข
     *   String[] d = data.getDrink(sn);                // d[6]=ลิตร  d[7]=ชม.  d[8]=นาที
     *   data.editDrink(sn, ลิตร, ชม., นาที);           // แก้ = เขียนทับค่าในบรรทัดเดิม
     *   data.deleteDrink(sn);                          // ลบ = เขียนทับบรรทัดนั้นด้วยบรรทัดว่าง
     *   go(Screen_2.class);                            // กลับมา Screen_2 จะดึงจากไฟล์ใหม่เอง
     */
    void hisCard(int row, String sn, String s) {   // ประวัติแถวที่ row: กรอบสูง 150 ห่างกันแถวละ 168 (sn = serial ของแก้ว)
        Box c = box();
        border(c);
        put(c, txt(s), 30, 42, WRAP, WRAP);                     // ข้อความซ้าย
        Img edBtn = imgBtn(R.drawable.b_edit);
        onTap(edBtn, () -> goWith(Screen_5.class, "serial", sn));   // ส่ง serial ไปหน้า 5
        put(c, edBtn, 760, 20, WRAP, WRAP);                      // ปุ่มแก้ไขขวา
        put(hisBox, c, 30, row * 168, 1020, 150);
    }

    // ===== คำนวณ =====

    int pct() {                          // % ที่ดื่มแล้ว ปัดลงทีละ 10 (0..100) ไว้เลือกรูปขวด
        if (tgL <= 0) return 0;
        int p = (int) Math.floor(drkL / tgL * 10 + 1e-9) * 10;
        return Math.max(0, Math.min(100, p));
    }

    String pctTxt() {                    // % แบบละเอียด ทศนิยม 2 ตำแหน่ง เช่น "52.38%"
        double p = 0;
        if (tgL > 0) p = drkL / tgL * 100;
        return String.format(Locale.US, "%.2f%%", p);
    }


    // ===== จัดข้อความ =====

    static String fmt(double v) {        // ตัวเลขอ่านง่าย: 60, 2.1, 0.65
        long r = Math.round(v * 100);
        if (r % 100 == 0) return String.valueOf(r / 100);
        if (r % 10 == 0) return String.format(Locale.US, "%.1f", r / 100.0);
        return String.format(Locale.US, "%.2f", r / 100.0);
    }

    static String hm(String h, String m) {   // "9","5" → "09:05"
        return String.format(Locale.US, "%02d:%02d", Integer.parseInt(h), Integer.parseInt(m));
    }

}
