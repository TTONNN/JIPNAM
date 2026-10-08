package com.example.jibnam;

/** Screen_2 : หน้าบันทึกดื่มน้ำ — หน้าจอใช้เมธอดจาก BaseClass, คำนวณใน WaterLogic (ไม่มี import android) */
public class Screen_2 extends BaseClass {

    WaterLogic wl = new WaterLogic(System.currentTimeMillis());
    int cup_Sel = 0;          // ภาชนะที่เลือก
    boolean cup_Opn = false;  // รายการภาชนะกางอยู่ไหม
    String unit = "ml";       // หน่วยช่องกรอก

    // รูปขวด 0%..100% (res/drawable/bottle_0 .. bottle_100)
    static final int[] BTL = {
            R.drawable.bottle_0, R.drawable.bottle_10, R.drawable.bottle_20, R.drawable.bottle_30,
            R.drawable.bottle_40, R.drawable.bottle_50, R.drawable.bottle_60, R.drawable.bottle_70,
            R.drawable.bottle_80, R.drawable.bottle_90, R.drawable.bottle_100,
    };

    Txt tv_Add, tv_Drk, tv_Unit;   // +x L, 0.6 / 2.1 L, หน่วย
    Img iv_Btl;                    // รูปขวด
    Inp et_Amt;                    // ช่องกรอก
    Box ll_Cup, ll_His;            // กล่องภาชนะ, กล่องประวัติ

    Runnable hide_Add = () -> show(tv_Add, false);

    @Override
    void build(Box pg) {
        // ดินสอขวาบน (ยังไม่ทำอะไร)
        Box r_Top = row();
        alg(r_Top, A_R);
        Txt b_Ed = txt("✎", 26, C_BLK, false);
        bg(b_Ed, C_WHT, 12, 0, 0);
        add(r_Top, b_Ed, 52, 52);
        add(pg, r_Top);

        // ข้อมูลด้านบน
        add(pg, txt("น้ำหนักตัว " + WaterLogic.fmt(wl.wt_Kg), 22, C_BLK, true));
        add(pg, txt("ปริมาณน้ำที่ควรดื่ม", 22, C_BLK, true));
        add(pg, txt(WaterLogic.fmt(wl.tg_L) + " L", 22, C_BLK, true));
        tv_Add = txt("", 26, C_GRN, true);
        show(tv_Add, false);
        add(pg, tv_Add);
        tv_Drk = txt("", 24, C_BLK, true);
        add(pg, tv_Drk);

        // รูปขวด
        iv_Btl = img();
        add(pg, iv_Btl, 130, 180);
        mar(iv_Btl, 0, 12, 0, 16);

        // แถวกลาง: ภาชนะ | ช่องกรอก + submit | ออกกำลังกาย
        Box r_Mid = row();
        ll_Cup = col();
        addW(r_Mid, ll_Cup, 1.1f);

        Box c_Mid = col();
        alg(c_Mid, A_CH);
        addW(r_Mid, c_Mid, 1.2f);
        mar(c_Mid, 8, 0, 8, 0);

        Box b_Inp = row();
        alg(b_Inp, A_CV);
        pad(b_Inp, 10, 0, 10, 0);
        bg(b_Inp, C_WHT, 10, 3, C_BLK);
        et_Amt = numInp(22);
        addW(b_Inp, et_Amt, 1f);
        tv_Unit = txt(unit, 22, C_BLK, true);
        pad(tv_Unit, 6, 0, 0, 0);
        add(b_Inp, tv_Unit);
        add(c_Mid, b_Inp, FILL, 56);

        Txt b_Sub = txt("submit", 18, C_WHT, true);
        bg(b_Sub, C_BLK, 28, 0, 0);
        onTap(b_Sub, this::submit);
        add(c_Mid, b_Sub, 110, 48);
        mar(b_Sub, 0, 14, 0, 0);

        Txt b_Ex = txt("ออกกำลังกาย", 13, C_BLK, true);   // ยังไม่ทำอะไร
        bg(b_Ex, C_LBL, 48, 0, 0);
        add(r_Mid, b_Ex, 96, 96);
        add(pg, r_Mid);

        // หัวข้อประวัติ + Read More (ยังไม่ทำอะไร)
        Box r_His = row();
        alg(r_His, A_CV);
        pad(r_His, 0, 24, 0, 4);
        Txt t_His = txt("ประวัติ (วันนี้)", 20, C_BLK, true);
        alg(t_His, A_L);
        addW(r_His, t_His, 1f);
        Txt b_More = txt("Read More", 13, C_DGY, true);
        bg(b_More, C_WHT, 16, 0, 0);
        pad(b_More, 14, 6, 14, 6);
        add(r_His, b_More);
        add(pg, r_His);

        ll_His = col();
        add(pg, ll_His);

        drawCup();
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
        setTxt(tv_Drk, WaterLogic.fmt(wl.dk_L) + " / " + WaterLogic.fmt(wl.tg_L) + " L");
        setImg(iv_Btl, BTL[wl.pct() / 10]);
        clr(ll_His);
        long now = System.currentTimeMillis();
        for (WaterLogic.Ent e : wl.his) card(WaterLogic.mlTxt(e.l), WaterLogic.ago(e.t, now));
    }

    // ปุ่มภาชนะ: ตัวที่เลือกอยู่บน (ฟ้า) ถ้ากางอยู่โชว์อีก 3 ตัว
    void drawCup() {
        clr(ll_Cup);
        cupBtn(cup_Sel, true);
        if (cup_Opn) {
            for (int i = 0; i < WaterLogic.CUPS.length; i++) {
                if (i != cup_Sel) cupBtn(i, false);
            }
        }
    }

    void cupBtn(int i, boolean top) {
        Txt b = txt(WaterLogic.cupLbl(i), 14, top ? C_WHT : C_BLK, true);
        bg(b, top ? C_BLU : C_WHT, 4, 2, C_BLK);
        add(ll_Cup, b, FILL, 34);
        mar(b, 0, 0, 0, 3);
        onTap(b, () -> {
            if (top) cup_Opn = !cup_Opn;              // กดตัวบน = กาง/หุบ
            else { cup_Sel = i; cup_Opn = false; }    // กดตัวล่าง = เลือก แล้วหุบ
            unit = WaterLogic.CUPS[cup_Sel][2];
            setTxt(et_Amt, WaterLogic.CUPS[cup_Sel][1]);
            setTxt(tv_Unit, unit);
            drawCup();
        });
    }

    // บล็อกประวัติ 1 อัน
    void card(String amt, String ago) {
        Box c = row();
        alg(c, A_CV);
        pad(c, 20, 8, 14, 8);
        bg(c, C_WHT, 18, 0, 0);

        Txt t_Amt = txt(amt, 24, C_BLK, true);
        alg(t_Amt, A_L);
        addW(c, t_Amt, 1f);

        Box c_R = col();
        add(c, c_R, WRAP, FILL);
        Txt t_Ed = txt("✎", 18, C_DGY, false);   // ยังไม่ทำอะไร
        alg(t_Ed, A_TR);
        addW(c_R, t_Ed, 1f);
        Txt t_Ago = txt(ago, 12, C_BLK, true);
        alg(t_Ago, A_R);
        add(c_R, t_Ago);

        add(ll_His, c, FILL, 72);
        mar(c, 0, 10, 0, 0);
    }
}
