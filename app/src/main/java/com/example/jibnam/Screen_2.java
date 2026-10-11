package com.example.jibnam;

/** Screen_2 : หน้าบันทึกดื่มน้ำ — หน้าจอใช้เมธอดจาก BaseClass, คำนวณใน WaterLogic (ไม่มี import android) */
public class Screen_2 extends BaseClass {

    WaterLogic wl = new WaterLogic();
    int cup_Sel = 0;          // ภาชนะที่เลือก
    String unit = "ml";       // หน่วยช่องกรอก
    static final int CUP_W = 140;   // ความกว้างปุ่มภาชนะ (dp) พอดีคำยาวสุด "ขวดใหญ่ 1.5 L"

    // รูปขวด 0%..100% (res/drawable/bottle_0 .. bottle_100)
    static final int[] BTL = {
            R.drawable.bottle_0, R.drawable.bottle_10, R.drawable.bottle_20, R.drawable.bottle_30,
            R.drawable.bottle_40, R.drawable.bottle_50, R.drawable.bottle_60, R.drawable.bottle_70,
            R.drawable.bottle_80, R.drawable.bottle_90, R.drawable.bottle_100,
    };

    Txt tv_Add, tv_Drk, tv_Unit, tv_Pct;   // +x L, 0.6 / 2.1 L, หน่วย, % บนขวด
    Img iv_Btl;                    // รูปขวด
    Inp et_Amt;                    // ช่องกรอก
    Box ll_Cup, ll_His;            // กล่องภาชนะ, กล่องประวัติ

    Runnable hide_Add = () -> show(tv_Add, false);

    @Override
    void build(Box pg) {
        // ===== ทดสอบเปลี่ยนหน้า (เพิ่มชั่วคราว) =====
        add(pg, plainTxt("Screen_2"));
        Btn toScreen1 = plainBtn("ไป Screen_1");
        onTap(toScreen1, () -> go(Screen_1.class));
        add(pg, toScreen1, WRAP, WRAP);
        // ===== จบส่วนทดสอบ =====

        // ข้อมูลด้านบน
        add(pg, plainTxt("น้ำหนักตัว " + WaterLogic.fmt(wl.wt_Kg)));
        add(pg, plainTxt("ปริมาณน้ำที่ควรดื่ม " + WaterLogic.fmt(wl.tg_L) + " L"));
        tv_Add = plainTxt("");
        show(tv_Add, false);
        add(pg, tv_Add);
        tv_Drk = plainTxt("");
        add(pg, tv_Drk);

        // รูปขวด + % ทับตรงกลาง
        Stack st_Btl = stack();
        iv_Btl = img();
        addFill(st_Btl, iv_Btl);
        tv_Pct = plainTxt("");
        addCenter(st_Btl, tv_Pct);
        add(pg, st_Btl, 130, 180);

        // ภาชนะ: 2 แถว แถวละ 2 ปุ่ม ขนาดเท่ากัน อยู่กลางจอ
        ll_Cup = col();
        add(pg, ll_Cup);

        // ช่องกรอก + หน่วย อยู่ตรงกลาง
        Box r_Inp = row();
        et_Amt = plainNumInp();
        alg(et_Amt, A_C);
        add(r_Inp, et_Amt, 120, WRAP);
        tv_Unit = plainTxt(unit);
        add(r_Inp, tv_Unit);
        add(pg, r_Inp, WRAP, WRAP);

        Btn b_Sub = plainBtn("submit");
        onTap(b_Sub, this::submit);
        add(pg, b_Sub, WRAP, WRAP);

        // ออกกำลังกาย: ชิดขวา ตรงกับ Read More ด้านล่าง
        Box r_Ex = row();
        alg(r_Ex, A_R);
        Btn b_Ex = plainBtn("ออกกำลังกาย");
        onTap(b_Ex, () -> go(Screen_3.class));
        add(r_Ex, b_Ex);
        add(pg, r_Ex);

        // ประวัติ: หัวข้อซ้าย, Read More ขวาสุด แถวเดียวกัน
        Box r_His = row();
        alg(r_His, A_CV);
        addW(r_His, plainTxt("ประวัติ (วันนี้)"), 1f);
        Btn b_More = plainBtn("Read More");
        onTap(b_More, () -> go(Screen_4.class));
        add(r_His, b_More);
        add(pg, r_His);

        ll_His = col();
        add(pg, ll_His);

        SampleHistory.addTo(wl, System.currentTimeMillis());   // ข้อมูลตัวอย่าง: ลบบรรทัดนี้เมื่อมีข้อมูลจริง
        drawCup();
        setTxt(et_Amt, WaterLogic.CUPS[cup_Sel][1]);
        refresh();
    }

    // กด submit: เพิ่มน้ำ → อัปเดตหน้า → โชว์ +x L 1 วิ
    void submit() {
        double v;
        try {
            v = Double.parseDouble(getTxt(et_Amt).trim());
        } catch (NumberFormatException e) {
            return;   // ว่าง / ไม่ใช่ตัวเลข → ไม่ทำอะไร
        }
        if (v <= 0) return;

        double a = wl.add(v, unit, System.currentTimeMillis());
        refresh();
        setTxt(tv_Add, "+" + WaterLogic.fmt(a) + " L");
        show(tv_Add, true);
        cancel(hide_Add);
        later(hide_Add, 1000);
    }

    // อัปเดตตัวเลข รูปขวด และประวัติ
    void refresh() {
        setTxt(tv_Drk, "ดื่มแล้ว " + WaterLogic.fmt(wl.drankL()) + " / " + WaterLogic.fmt(wl.tg_L) + " L");
        setImg(iv_Btl, BTL[wl.pct() / 10]);   // รูปเปลี่ยนทีละ 10%
        setTxt(tv_Pct, wl.pctTxt());           // ตัวเลขละเอียด ทศนิยม 2 ตำแหน่ง
        clr(ll_His);
        long now = System.currentTimeMillis();
        for (int i = 0; i < wl.his.size(); i++) {
            WaterLogic.Ent e = wl.his.get(i);
            card(i, WaterLogic.mlTxt(e.l), WaterLogic.ago(e.t, now));
        }
    }

    // ปุ่มภาชนะ: โชว์ครบทุกอัน แถวละ 2 ปุ่ม กดแล้วเติมปริมาณในช่องกรอก
    void drawCup() {
        clr(ll_Cup);
        Box r = null;
        for (int i = 0; i < WaterLogic.CUPS.length; i++) {
            if (i % 2 == 0) {          // ครบ 2 ปุ่ม ขึ้นแถวใหม่
                r = row();
                alg(r, A_CH);
                add(ll_Cup, r);
            }
            cupBtn(r, i);
        }
    }

    void cupBtn(Box r, int i) {
        Btn b = plainBtn(WaterLogic.cupLbl(i));
        add(r, b, CUP_W, WRAP);
        onTap(b, () -> {
            cup_Sel = i;
            unit = WaterLogic.CUPS[i][2];
            setTxt(et_Amt, WaterLogic.CUPS[i][1]);
            setTxt(tv_Unit, unit);
        });
    }

    // ประวัติ 1 แถว: กรอบเส้นดำธรรมดา ข้อความซ้าย ปุ่มแก้ไขขวาสุด
    void card(int i, String amt, String ago) {
        Box c = row();
        alg(c, A_CV);
        bg(c, C_WHT, 0, 1, C_BLK);
        pad(c, 8, 4, 8, 4);
        addW(c, plainTxt(amt + "   " + ago), 1f);
        Btn b_Ed = plainBtn("แก้ไข");
        onTap(b_Ed, () -> goWith(Screen_5.class, "drinkIndex", String.valueOf(i)));   // ส่งลำดับแก้วไปหน้า 5
        add(c, b_Ed);
        add(ll_His, c);
        mar(c, 0, 4, 0, 0);
    }
}
