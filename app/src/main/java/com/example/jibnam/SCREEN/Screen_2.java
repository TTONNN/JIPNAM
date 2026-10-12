package com.example.jibnam.SCREEN;

import com.example.jibnam.BaseClass;
import com.example.jibnam.R;
import com.example.jibnam.WaterCalculate;
import com.example.jibnam.DATA.Data;
import com.example.jibnam.DATA.DataApi;

import java.util.List;
import java.util.Locale;

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
    static final int CUP_W = 140;        // ความกว้างปุ่มภาชนะ (dp) พอดีคำยาวสุด "ขวดใหญ่ 1.5 L"

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
    Box cupBox;     // กล่องปุ่มภาชนะ
    Box hisBox;     // กล่องประวัติ
    Btn notiBtn;    // ปุ่ม การแจ้งเตือน (เปิด)/(ปิด)

    Runnable hidePlus = () -> show(plusTx, false);   // ซ่อน "+x L"

    @Override
    protected void build(Box pg) {
        // ปุ่มทดสอบ ไปหน้า 1 และหน้า 6
        add(pg, txt("Screen_2"));
        Btn toS1 = btn("ไป Screen_1");
        onTap(toS1, () -> go(Screen_1.class));
        add(pg, toS1, WRAP, WRAP);
        Btn toS6 = btn("ไป Screen_6");
        onTap(toS6, () -> go(Screen_6.class));
        add(pg, toS6, WRAP, WRAP);

        // ดึงข้อมูลผู้ใช้จากไฟล์ RealData.txt
        data = new Data(DataApi.realDataFile(this));
        if (data.lastUser() == null)                                   // ชั่วคราว: ยังไม่มีข้อมูลผู้ใช้ ใส่ค่าตัวอย่างไว้ก่อน
            data.saveUser(60, 7, 0, 23, 0);                            //   (ลบได้เมื่อ Screen_1 เซฟข้อมูลผู้ใช้จริง)
        wtKg = Double.parseDouble(data.lastUser()[2]);
        tgL = WaterCalculate.targetL(wtKg);

        // ปุ่มแจ้งเตือน: กดสลับ เปิด/ปิด แล้วเซฟลงไฟล์ (ตัวท้ายสุดของบรรทัด D ล่างสุด)
        notiBtn = btn("");
        onTap(notiBtn, () -> {
            data.setNoti(!data.notiOn());
            showNoti();
        });
        add(pg, notiBtn, WRAP, WRAP);
        showNoti();

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

        drawCup();
        setTxt(amtIn, CUPS[0][1]);       // เริ่มต้นใส่ปริมาณภาชนะแรก
        refresh();
    }

    // ===== หน้าจอ =====

    void submit() {                      // กด submit: เขียนลงไฟล์ → ดึงจากไฟล์มาโชว์ใหม่ → โชว์ +x L 1 วิ
        double v;
        try {
            v = Double.parseDouble(getTxt(amtIn));
        } catch (NumberFormatException e) {
            return;                      // ว่าง / ไม่ใช่ตัวเลข → ไม่ทำอะไร
        }
        if (v <= 0) return;

        double l = toL(v, unit);
        data.addDrink(l, tgL);           // เก็บลงไฟล์ (Data ใส่ serial เวลาตอนนี้ + ยอดดื่มทั้งวันให้เอง)
        refresh();
        setTxt(plusTx, "+" + fmt(l) + " L");
        show(plusTx, true);
        cancel(hidePlus);
        later(hidePlus, 1000);
    }

    void refresh() {                     // ดึงแก้ววันนี้จากไฟล์ แล้ววาดประวัติ ยอดดื่ม รูปขวด %
        List<String[]> ds = data.getDrinks(Data.today());          // d = { "D", serial, ลิตร, ชม., นาที, ต้องดื่ม, ดื่มแล้วทั้งวัน, แจ้งเตือน }
        clr(hisBox);
        drkL = 0;
        for (int k = ds.size() - 1; k >= 0; k--) {                 // ท้ายไฟล์ = แก้วล่าสุด อยู่บนสุด
            String[] d = ds.get(k);
            double l = Double.parseDouble(d[2]);
            drkL += l;
            hisCard(d[1], mlTxt(l) + "   เวลา " + hm(d[3], d[4]));
        }
        setTxt(drankTx, "ดื่มแล้ว " + fmt(drkL) + " / " + fmt(tgL) + " L");
        setImg(btlImg, BTL[pct() / 10]);  // รูปเปลี่ยนทีละ 10%
        setTxt(pctTx, pctTxt());          // % ทศนิยม 2 ตำแหน่ง
    }

    void showNoti() {                    // ข้อความบนปุ่มแจ้งเตือน ดึงจากไฟล์
        setTxt(notiBtn, "การแจ้งเตือน (" + (data.notiOn() ? "เปิด" : "ปิด") + ")");
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
    void hisCard(String sn, String s) {  // ประวัติ 1 แถว: กรอบ, ข้อความซ้าย, ปุ่มแก้ไขขวาสุด (sn = serial ของแก้ว)
        Box c = row();
        alg(c, A_CV);
        border(c);
        pad(c, 8, 4, 8, 4);
        addW(c, txt(s), 1f);
        Btn edBtn = btn("แก้ไข");
        onTap(edBtn, () -> goWith(Screen_5.class, "serial", sn));   // ส่ง serial ไปหน้า 5
        add(c, edBtn);
        add(hisBox, c);
        mar(c, 0, 4, 0, 0);
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
