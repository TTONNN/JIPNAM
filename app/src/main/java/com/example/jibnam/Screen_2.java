package com.example.jibnam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Screen_2 : หน้าหลัก บันทึกดื่มน้ำ — หน้าจอ + ข้อมูล + คำนวณ อยู่ในไฟล์นี้ */
public class Screen_2 extends BaseClass {

    // ===== ข้อมูล (ตอนนี้ค่าตัวอย่าง) =====
    double wtKg = 60;                    // น้ำหนักตัว (กก.)
    double tgL = 2.1;                    // เป้าน้ำที่ควรดื่ม (ลิตร)
    String unit = "ml";                  // หน่วยของช่องกรอก

    static final String[][] CUPS = {     // ภาชนะ { ชื่อ, ปริมาณ, หน่วย }
            {"กรวย", "50", "ml"},
            {"แก้ว", "150", "ml"},
            {"ขวด", "500", "ml"},
            {"ขวดใหญ่", "1.5", "L"},
    };
    static final int CUP_W = 140;        // ความกว้างปุ่มภาชนะ (dp) พอดีคำยาวสุด "ขวดใหญ่ 1.5 L"

    static class Ent {                   // แก้วที่ดื่ม 1 แก้ว
        final double l;                  // ปริมาณ (ลิตร)
        final long t;                    // เวลาที่ดื่ม (มิลลิวินาที)
        Ent(double l, long t) { this.l = l; this.t = t; }
    }
    List<Ent> his = new ArrayList<>();   // ประวัติวันนี้ his[0] = ล่าสุด

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
    Box cupBox;     // กล่องปุ่มภาชนะ
    Box hisBox;     // กล่องประวัติ

    Runnable hidePlus = () -> show(plusTx, false);   // ซ่อน "+x L"

    @Override
    void build(Box pg) {
        // ปุ่มทดสอบ ไปหน้า 1 และหน้า 6
        add(pg, txt("Screen_2"));
        Btn toS1 = btn("ไป Screen_1");
        onTap(toS1, () -> go(Screen_1.class));
        add(pg, toS1, WRAP, WRAP);
        Btn toS6 = btn("ไป Screen_6");
        onTap(toS6, () -> go(Screen_6.class));
        add(pg, toS6, WRAP, WRAP);

        // ข้อมูลด้านบน
        add(pg, txt("น้ำหนักตัว " + fmt(wtKg)));
        add(pg, txt("ปริมาณน้ำที่ควรดื่ม " + fmt(tgL) + " L"));
        plusTx = txt("");
        show(plusTx, false);
        add(pg, plusTx);
        drankTx = txt("");
        add(pg, drankTx);

        // รูปขวด + % ทับตรงกลาง
        Stack btlSt = stack();
        btlImg = img();
        addFill(btlSt, btlImg);
        pctTx = txt("");
        addMid(btlSt, pctTx);
        add(pg, btlSt, 130, 180);

        // ปุ่มภาชนะ (วาดใน drawCup)
        cupBox = col();
        add(pg, cupBox);

        // ช่องกรอก + หน่วย อยู่กลางจอ
        Box inRow = row();
        amtIn = numInp();
        alg(amtIn, A_C);
        add(inRow, amtIn, 120, WRAP);
        unitTx = txt(unit);
        add(inRow, unitTx);
        add(pg, inRow, WRAP, WRAP);

        Btn subBtn = btn("submit");
        onTap(subBtn, this::submit);
        add(pg, subBtn, WRAP, WRAP);

        // ออกกำลังกาย ชิดขวา ตรงกับ Read More
        Box exRow = row();
        alg(exRow, A_R);
        Btn exBtn = btn("ออกกำลังกาย");
        onTap(exBtn, () -> go(Screen_3.class));
        add(exRow, exBtn);
        add(pg, exRow);

        // หัวข้อประวัติซ้าย, Read More ขวาสุด
        Box hisRow = row();
        alg(hisRow, A_CV);
        addW(hisRow, txt("ประวัติ (วันนี้)"), 1f);
        Btn moreBtn = btn("Read More");
        onTap(moreBtn, () -> go(Screen_4.class));
        add(hisRow, moreBtn);
        add(pg, hisRow);

        hisBox = col();
        add(pg, hisBox);

        // ข้อมูลตัวอย่าง: ลบ 2 บรรทัดนี้เมื่อมีข้อมูลจริง
        long now = System.currentTimeMillis();
        for (SampleHistory.Drink d : SampleHistory.make(now)) his.add(new Ent(d.l, d.t));

        drawCup();
        setTxt(amtIn, CUPS[0][1]);       // เริ่มต้นใส่ปริมาณภาชนะแรก
        refresh();
    }

    // ===== หน้าจอ =====

    void submit() {                      // กด submit: เพิ่มแก้ว → อัปเดตหน้า → โชว์ +x L 1 วิ
        double v;
        try {
            v = Double.parseDouble(getTxt(amtIn));
        } catch (NumberFormatException e) {
            return;                      // ว่าง / ไม่ใช่ตัวเลข → ไม่ทำอะไร
        }
        if (v <= 0) return;

        double l = toL(v, unit);
        his.add(0, new Ent(l, System.currentTimeMillis()));   // ใส่บนสุดของประวัติ
        refresh();
        setTxt(plusTx, "+" + fmt(l) + " L");
        show(plusTx, true);
        cancel(hidePlus);
        later(hidePlus, 1000);
    }

    void refresh() {                     // อัปเดตยอดดื่ม รูปขวด % และประวัติ
        setTxt(drankTx, "ดื่มแล้ว " + fmt(drankL()) + " / " + fmt(tgL) + " L");
        setImg(btlImg, BTL[pct() / 10]);  // รูปเปลี่ยนทีละ 10%
        setTxt(pctTx, pctTxt());          // % ทศนิยม 2 ตำแหน่ง
        clr(hisBox);
        long now = System.currentTimeMillis();
        for (int i = 0; i < his.size(); i++) {
            Ent e = his.get(i);
            hisCard(i, mlTxt(e.l) + "   " + ago(e.t, now));
        }
    }

    void drawCup() {                     // วางปุ่มภาชนะครบทุกอัน แถวละ 2 ปุ่ม
        clr(cupBox);
        Box r = null;
        for (int i = 0; i < CUPS.length; i++) {
            if (i % 2 == 0) {            // ครบ 2 ปุ่ม ขึ้นแถวใหม่
                r = row();
                alg(r, A_CH);
                add(cupBox, r);
            }
            cupBtn(r, i);
        }
    }

    void cupBtn(Box r, int i) {          // ปุ่มภาชนะที่ i: กดแล้วเติมปริมาณ + หน่วยในช่องกรอก
        Btn b = btn(cupLbl(i));
        add(r, b, CUP_W, WRAP);
        onTap(b, () -> {
            unit = CUPS[i][2];
            setTxt(amtIn, CUPS[i][1]);
            setTxt(unitTx, unit);
        });
    }

    void hisCard(int i, String s) {      // ประวัติ 1 แถว: กรอบ, ข้อความซ้าย, ปุ่มแก้ไขขวาสุด
        Box c = row();
        alg(c, A_CV);
        border(c);
        pad(c, 8, 4, 8, 4);
        addW(c, txt(s), 1f);
        Btn edBtn = btn("แก้ไข");
        onTap(edBtn, () -> goWith(Screen_5.class, "drinkIndex", String.valueOf(i)));   // ส่งลำดับแก้วไปหน้า 5
        add(c, edBtn);
        add(hisBox, c);
        mar(c, 0, 4, 0, 0);
    }

    // ===== คำนวณ =====

    double drankL() {                    // ดื่มแล้วกี่ลิตร = รวมทุกแก้วในประวัติ
        double sum = 0;
        for (Ent e : his) sum += e.l;
        return sum;
    }

    int pct() {                          // % ที่ดื่มแล้ว ปัดลงทีละ 10 (0..100) ไว้เลือกรูปขวด
        if (tgL <= 0) return 0;
        int p = (int) Math.floor(drankL() / tgL * 10 + 1e-9) * 10;
        return Math.max(0, Math.min(100, p));
    }

    String pctTxt() {                    // % แบบละเอียด ทศนิยม 2 ตำแหน่ง เช่น "52.38%"
        double p = tgL <= 0 ? 0 : drankL() / tgL * 100;
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

    static String ago(long t, long now) {   // ผ่านมานานแค่ไหน → "1 ชั่วโมง 18 นาที"
        long m = Math.max(0, (now - t) / 60000);
        return (m / 60) + " ชั่วโมง " + String.format(Locale.US, "%02d", m % 60) + " นาที";
    }

    static String cupLbl(int i) {        // ชื่อบนปุ่มภาชนะ เช่น "กรวย 50 ml"
        return CUPS[i][0] + " " + CUPS[i][1] + " " + CUPS[i][2];
    }
}
