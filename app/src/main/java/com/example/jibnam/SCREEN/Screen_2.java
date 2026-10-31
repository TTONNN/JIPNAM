package com.example.jibnam.SCREEN;

import com.example.jibnam.R;            // รูปใน res/drawable เช่น R.drawable.bottle_0
import com.example.jibnam.CODE.*;       // BaseClass, WaterCalculate
import com.example.jibnam.DATA.*;       // Data, DataApi
import java.util.*;                     // List, Locale

/** Screen_2 : หน้าหลัก บันทึกดื่มน้ำ — หน้าจอ + ข้อมูล + คำนวณ อยู่ในไฟล์นี้ */
public class Screen_2 extends BaseClass {

    // ===== ข้อมูล (ดึงจากไฟล์ตอนเปิดหน้า) =====
    double wtKg;                         // น้ำหนักตัว (กก.) จากบรรทัด U ล่าสุด
    double tgL;                          // เป้าน้ำที่ควรดื่ม (ลิตร) = WaterCalculate.targetL(wtKg)
    String unit = "ml";                  // หน่วยของช่องกรอก

    static final String[][] CUPS = {     // ภาชนะ { ชื่อ, ปริมาณ, หน่วย }
            {"กรวย", "50", "ml"},
            {"แก้ว", "150", "ml"},
            {"ขวด", "500", "ml"},
            {"ขวดใหญ่", "1.5", "L"},
    };
    static final int CUP_W = 420;        // ความกว้างปุ่มภาชนะ (px) พอดีคำยาวสุด "ขวดใหญ่ 1.5 L"

    Data data;                           // ไฟล์ RealData.txt (ข้อมูลจริงอยู่ในไฟล์ หน้าจอดึงมาโชว์อย่างเดียว)
    double drkL;                         // ดื่มแล้ววันนี้ (ลิตร) รวมจากไฟล์ทุกครั้งที่ refresh

    static final int[] BTL = {           // รูปขวด 0% ถึง 100% ทีละ 10
            R.drawable.bottle_0, R.drawable.bottle_10, R.drawable.bottle_20, R.drawable.bottle_30,
            R.drawable.bottle_40, R.drawable.bottle_50, R.drawable.bottle_60, R.drawable.bottle_70,
            R.drawable.bottle_80, R.drawable.bottle_90, R.drawable.bottle_100,
    };

    // ===== ชิ้นส่วนหน้าจอ =====
    Txt plusTx;     // "+0.15 L" โชว์ 1 วิหลังกด submit
    Txt drankTx;    // "ดื่มแล้ว 1.46 / 2.1 L"
    Txt pctTx;      // % บนรูปขวด
    Txt unitTx;     // หน่วยข้างช่องกรอก
    Img btlImg;     // รูปขวด
    Inp amtIn;      // ช่องกรอกปริมาณ
    Box hisBox;     // กล่องประวัติ (วาดใหม่ทุกครั้งที่ refresh)
    Btn notiBtn;    // ปุ่ม การแจ้งเตือน (เปิด)/(ปิด)

    Runnable hidePlus = () -> show(plusTx, false);   // ซ่อน "+x L"

    @Override
    protected void build(Box pg) {          // วางของแบบนับพิกัด put(pg, ของ, x, y, กว้าง, สูง) หน่วย px
        // ดึงข้อมูลผู้ใช้จากไฟล์ RealData.txt
        data = new Data(DataApi.realDataFile(this));
        if (data.lastUser() == null)                                   // ชั่วคราว: ยังไม่มีข้อมูลผู้ใช้ ใส่ค่าตัวอย่างไว้ก่อน
            data.saveUser(60, 7, 0, 23, 0);                            //   (ลบได้เมื่อ Screen_1 เซฟข้อมูลผู้ใช้จริง)
        wtKg = Double.parseDouble(data.lastUser()[2]);
        tgL = WaterCalculate.targetL(wtKg);

        // ปุ่มทดสอบ ไปหน้า 1 และหน้า 6
        put(pg, txt("Screen_2"), 30, 30, WRAP, WRAP);
        Btn toS1 = btn("ไป Screen_1");
        onTap(toS1, () -> go(Screen_1.class));
        put(pg, toS1, 30, 90, 450, WRAP);
        Btn toS6 = btn("ไป Screen_6");
        onTap(toS6, () -> go(Screen_6.class));
        put(pg, toS6, 510, 90, 450, WRAP);

        // ปุ่มแจ้งเตือน: กดสลับ เปิด/ปิด แล้วเซฟลงไฟล์ (ตัวท้ายสุดของบรรทัด D ล่างสุด)
        notiBtn = btn("");
        onTap(notiBtn, () -> {
            data.setNoti(!data.notiOn());
            showNoti();
        });
        put(pg, notiBtn, 30, 255, 600, WRAP);
        showNoti();

        // ข้อมูลด้านบน
        put(pg, txt("น้ำหนักตัว " + fmt(wtKg)), 30, 420, WRAP, WRAP);
        put(pg, txt("ปริมาณน้ำที่ควรดื่ม " + fmt(tgL) + " L"), 30, 480, WRAP, WRAP);
        drankTx = txt("");
        put(pg, drankTx, 30, 540, WRAP, WRAP);
        plusTx = txt("");
        show(plusTx, false);
        put(pg, plusTx, 30, 600, WRAP, WRAP);

        // รูปขวด กว้าง 390 สูง 540 + % ทับกลางขวด
        btlImg = img();
        put(pg, btlImg, 345, 675, 390, 540);
        pctTx = txt("");
        put(pg, pctTx, 465, 930, WRAP, WRAP);

        // ปุ่มภาชนะ 2 แถว แถวละ 2 ปุ่ม
        for (int i = 0; i < CUPS.length; i++) {
            int x = (i % 2 == 0) ? 105 : 555;      // ซ้าย / ขวา
            int y = (i < 2) ? 1245 : 1395;          // แถวบน / แถวล่าง
            cupBtn(pg, i, x, y);
        }

        // ช่องกรอก + หน่วย
        amtIn = numInp();
        put(pg, amtIn, 360, 1560, 360, WRAP);
        unitTx = txt(unit);
        put(pg, unitTx, 735, 1605, WRAP, WRAP);

        Btn subBtn = btn("submit");
        onTap(subBtn, this::submit);
        put(pg, subBtn, 390, 1725, 300, WRAP);

        // ออกกำลังกาย ฝั่งขวา
        Btn exBtn = btn("ออกกำลังกาย");
        onTap(exBtn, () -> go(Screen_3.class));
        put(pg, exBtn, 630, 1890, 420, WRAP);

        // หัวข้อประวัติซ้าย, Read More ขวา
        put(pg, txt("ประวัติ (วันนี้)"), 30, 2100, WRAP, WRAP);
        Btn moreBtn = btn("Read More");
        onTap(moreBtn, () -> go(Screen_4.class));
        put(pg, moreBtn, 630, 2055, 420, WRAP);

        hisBox = box();                          // กล่องประวัติ (วาดใหม่ใน refresh)
        put(pg, hisBox, 0, 2220, WRAP, WRAP);

        amtIn.setText(CUPS[0][1]);       // เริ่มต้นใส่ปริมาณภาชนะแรก
        refresh();
    }

    // ===== หน้าจอ =====

    void submit() {                      // กด submit: เขียนลงไฟล์ → ดึงจากไฟล์มาโชว์ใหม่ → โชว์ +x L 1 วิ
        double v;
        try {
            v = Double.parseDouble(amtIn.getText().toString().trim());
        } catch (NumberFormatException e) {
            return;                      // ว่าง / ไม่ใช่ตัวเลข → ไม่ทำอะไร
        }
        if (v <= 0) return;

        double l = toL(v, unit);
        data.addDrink(l, tgL);           // เก็บลงไฟล์ (Data ใส่ serial เวลาตอนนี้ + ยอดดื่มทั้งวันให้เอง)
        refresh();
        plusTx.setText("+" + fmt(l) + " L");
        show(plusTx, true);
        cancel(hidePlus);
        later(hidePlus, 1000);
    }

    void refresh() {                     // ดึงแก้ววันนี้จากไฟล์ แล้ววาดประวัติ ยอดดื่ม รูปขวด %
        List<String[]> ds = data.getDrinks(Data.today());          // d = { "D", serial, ลิตร, ชม., นาที, ต้องดื่ม, ดื่มแล้วทั้งวัน, แจ้งเตือน }
        clr(hisBox);
        drkL = 0;
        int row = 0;                                               // แถวที่ 0 อยู่บนสุด
        for (int k = ds.size() - 1; k >= 0; k--) {                 // ท้ายไฟล์ = แก้วล่าสุด อยู่บนสุด
            String[] d = ds.get(k);
            double l = Double.parseDouble(d[2]);
            drkL += l;
            hisCard(row, d[1], mlTxt(l) + "   เวลา " + hm(d[3], d[4]));
            row++;
        }
        drankTx.setText("ดื่มแล้ว " + fmt(drkL) + " / " + fmt(tgL) + " L");
        btlImg.setImageResource(BTL[pct() / 10]);  // รูปเปลี่ยนทีละ 10%
        pctTx.setText(pctTxt());          // % ทศนิยม 2 ตำแหน่ง
    }

    void showNoti() {                    // ข้อความบนปุ่มแจ้งเตือน ดึงจากไฟล์
        notiBtn.setText("การแจ้งเตือน (" + (data.notiOn() ? "เปิด" : "ปิด") + ")");
    }

    void cupBtn(Box pg, int i, int x, int y) {   // ปุ่มภาชนะที่ i ที่ (x, y): กดแล้วเติมปริมาณ + หน่วยในช่องกรอก
        Btn b = btn(cupLbl(i));
        put(pg, b, x, y, CUP_W, WRAP);
        onTap(b, () -> {
            unit = CUPS[i][2];
            amtIn.setText(CUPS[i][1]);
            unitTx.setText(unit);
        });
    }

    /*
     * ปุ่ม "แก้ไข" → ส่ง serial ของแก้วนี้ ไป Screen_5
     * Screen_5 รับแล้วใช้ต่อ:
     *   Data data = new Data(DataApi.realDataFile(this));
     *   String sn = arg("serial");                     // แก้วที่ถูกกดแก้ไข
     *   String[] d = data.getDrink(sn);                // d[2]=ลิตร  d[3]=ชม.  d[4]=นาที
     *   data.editDrink(sn, ลิตร, ชม., นาที);           // แก้ = เขียนทับค่าในบรรทัดเดิม
     *   data.deleteDrink(sn);                          // ลบ = เขียนทับบรรทัดนั้นด้วยบรรทัดว่าง
     *   go(Screen_2.class);                            // กลับมา Screen_2 จะดึงจากไฟล์ใหม่เอง
     */
    void hisCard(int row, String sn, String s) {   // ประวัติแถวที่ row: กรอบสูง 150 ห่างกันแถวละ 168 (sn = serial ของแก้ว)
        Box c = box();
        border(c);
        put(c, txt(s), 30, 45, WRAP, WRAP);                     // ข้อความซ้าย
        Btn edBtn = btn("แก้ไข");
        onTap(edBtn, () -> goWith(Screen_5.class, "serial", sn));   // ส่ง serial ไปหน้า 5
        put(c, edBtn, 750, 3, 240, WRAP);                        // ปุ่มแก้ไขขวา
        put(hisBox, c, 30, row * 168, 1020, 150);
    }

    // ===== คำนวณ =====

    int pct() {                          // % ที่ดื่มแล้ว ปัดลงทีละ 10 (0..100) ไว้เลือกรูปขวด
        if (tgL <= 0) return 0;
        int p = (int) Math.floor(drkL / tgL * 10 + 1e-9) * 10;
        return Math.max(0, Math.min(100, p));
    }

    String pctTxt() {                    // % แบบละเอียด ทศนิยม 2 ตำแหน่ง เช่น "52.38%"
        double p = tgL <= 0 ? 0 : drkL / tgL * 100;
        return String.format(Locale.US, "%.2f%%", p);
    }

    static double toL(double v, String u) {   // แปลงเป็นลิตร (ml ÷ 1000, L ใช้ตรงๆ)
        return "L".equals(u) ? v : v / 1000.0;
    }

    // ===== จัดข้อความ =====

    static String fmt(double v) {        // ตัวเลขอ่านง่าย: 60, 2.1, 0.65
        long r = Math.round(v * 100);
        if (r % 100 == 0) return String.valueOf(r / 100);
        if (r % 10 == 0) return String.format(Locale.US, "%.1f", r / 100.0);
        return String.format(Locale.US, "%.2f", r / 100.0);
    }

    static String mlTxt(double l) {      // 0.64 → "640 ml"
        return Math.round(l * 1000) + " ml";
    }

    static String hm(String h, String m) {   // "9","5" → "09:05"
        return String.format(Locale.US, "%02d:%02d", Integer.parseInt(h), Integer.parseInt(m));
    }

    static String cupLbl(int i) {        // ชื่อบนปุ่มภาชนะ เช่น "กรวย 50 ml"
        return CUPS[i][0] + " " + CUPS[i][1] + " " + CUPS[i][2];
    }
}
