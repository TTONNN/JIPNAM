package com.example.jibnam;

public class Screen_2 extends BaseClass {

    WaterLogic wl = new WaterLogic(System.currentTimeMillis());
    int cup_Sel = 0;
    boolean cup_Opn = false;
    String unit = "ml";

    static final int[] BTL = {
            R.drawable.bottle_0, R.drawable.bottle_10, R.drawable.bottle_20, R.drawable.bottle_30,
            R.drawable.bottle_40, R.drawable.bottle_50, R.drawable.bottle_60, R.drawable.bottle_70,
            R.drawable.bottle_80, R.drawable.bottle_90, R.drawable.bottle_100,
    };

    Txt tv_Add, tv_Drk, tv_Unit;
    Img iv_Btl;
    Inp et_Amt;
    Box ll_Cup, ll_His;

    @Override
    void build(Box pg) {
        add(pg, txt("น้ำหนักตัว " + WaterLogic.fmt(wl.wt_Kg)));
        add(pg, txt("ปริมาณน้ำที่ควรดื่ม"));
        add(pg, txt(WaterLogic.fmt(wl.tg_L) + " L"));
        tv_Add = txt("");
        add(pg, tv_Add);
        tv_Drk = txt("");
        add(pg, tv_Drk);

        iv_Btl = new Img(this);
        iv_Btl.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        add(pg, iv_Btl);

        Box r_Mid = new Box(this);
        r_Mid.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        ll_Cup = new Box(this);
        ll_Cup.setOrientation(android.widget.LinearLayout.VERTICAL);
        r_Mid.addView(ll_Cup);

        Box c_Mid = new Box(this);
        c_Mid.setOrientation(android.widget.LinearLayout.VERTICAL);
        r_Mid.addView(c_Mid);

        Box b_Inp = new Box(this);
        b_Inp.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        et_Amt = new Inp(this);
        et_Amt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        b_Inp.addView(et_Amt);
        tv_Unit = txt(unit);
        b_Inp.addView(tv_Unit);
        c_Mid.addView(b_Inp);

        Txt b_Sub = txt("submit");
        onTap(b_Sub, this::submit);
        c_Mid.addView(b_Sub);

        Txt b_Ex = txt("ออกกำลังกาย");
        r_Mid.addView(b_Ex);
        add(pg, r_Mid);

        add(pg, txt("ประวัติ (วันนี้)"));
        ll_His = new Box(this);
        ll_His.setOrientation(android.widget.LinearLayout.VERTICAL);
        add(pg, ll_His);

        drawCup();
        refresh();
    }

    void submit() {
        double v;
        try {
            v = Double.parseDouble(getTxt(et_Amt).trim());
        } catch (NumberFormatException e) {
            return;
        }
        if (v <= 0) return;
        double a = wl.add(v, unit, System.currentTimeMillis());
        refresh();
        setTxt(tv_Add, "+" + WaterLogic.fmt(a) + " L");
    }

    void refresh() {
        setTxt(tv_Drk, WaterLogic.fmt(wl.dk_L) + " / " + WaterLogic.fmt(wl.tg_L) + " L");
        setImg(iv_Btl, BTL[wl.pct() / 10]);
        ll_His.removeAllViews();
        long now = System.currentTimeMillis();
        for (WaterLogic.Ent e : wl.his) card(WaterLogic.mlTxt(e.l), WaterLogic.ago(e.t, now));
    }

    void drawCup() {
        ll_Cup.removeAllViews();
        cupBtn(cup_Sel, true);
        if (cup_Opn) for (int i = 0; i < WaterLogic.CUPS.length; i++)
            if (i != cup_Sel) cupBtn(i, false);
    }

    void cupBtn(int i, boolean top) {
        Txt b = txt(WaterLogic.cupLbl(i));
        ll_Cup.addView(b);
        onTap(b, () -> {
            if (top) cup_Opn = !cup_Opn;
            else { cup_Sel = i; cup_Opn = false; }
            unit = WaterLogic.CUPS[cup_Sel][2];
            setTxt(et_Amt, WaterLogic.CUPS[cup_Sel][1]);
            setTxt(tv_Unit, unit);
            drawCup();
        });
    }

    void card(String amt, String ago) {
        Box c = new Box(this);
        c.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        Txt t_Amt = txt(amt);
        c.addView(t_Amt);
        Txt t_Ago = txt(ago);
        c.addView(t_Ago);
        ll_His.addView(c);
    }

    Txt txt(String s) {
        Txt t = new Txt(this);
        t.setText(s);
        return t;
    }
}
