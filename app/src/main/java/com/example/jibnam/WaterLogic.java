package com.example.jibnam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** WaterLogic : ข้อมูล + คำนวณของ Screen_2 — Java ล้วน ไม่มี Android API */
public class WaterLogic {

    // ตัวแปรรอรับจากคนอื่น (ตอนนี้ค่าตัวอย่าง)
    public double wt_Kg = 60;   // น้ำหนักตัว
    public double tg_L = 2.1;   // ควรดื่ม (L)

    // ภาชนะ { ชื่อ, ปริมาณ, หน่วย } — ตัวแรก = ค่าเริ่มต้น
    public static final String[][] CUPS = {
            {"กรวย", "50", "ml"},
            {"แก้ว", "150", "ml"},
            {"ขวด", "500", "ml"},
            {"ขวดใหญ่", "1.5", "L"},
    };

    // 1 รายการประวัติ: l = ลิตร, t = เวลาที่ดื่ม (ms)
    public static class Ent {
        public final double l;
        public final long t;
        public Ent(double l, long t) { this.l = l; this.t = t; }
    }

    public final List<Ent> his = new ArrayList<>();   // his[0] = ล่าสุด

    /** ดื่มแล้วกี่ลิตร = รวมทุกแก้วในประวัติ (ประวัติเปลี่ยน ยอดเปลี่ยนตามเอง) */
    public double drankL() {
        double sum = 0;
        for (Ent e : his) sum += e.l;
        return sum;
    }

    /** กด submit: แปลงเป็น L, ใส่ประวัติบนสุด → คืนค่าที่เพิ่ม (L) */
    public double add(double v, String u, long now) {
        double l = toL(v, u);
        his.add(0, new Ent(l, now));
        return l;
    }

    /** % ที่ดื่มแล้ว ปัดลงทีละ 10 (0..100) */
    public int pct() {
        if (tg_L <= 0) return 0;
        int p = (int) Math.floor(drankL() / tg_L * 10 + 1e-9) * 10;
        return Math.max(0, Math.min(100, p));
    }

    /** % ที่ดื่มแล้วแบบละเอียด ทศนิยม 2 ตำแหน่ง เช่น "30.95%" (เกิน 100 ได้) */
    public String pctTxt() {
        double p = tg_L <= 0 ? 0 : drankL() / tg_L * 100;
        return String.format(Locale.US, "%.2f%%", p);
    }

    /** ml → L (ถ้าเป็น L อยู่แล้วไม่แปลง) */
    public static double toL(double v, String u) {
        return "L".equals(u) ? v : v / 1000.0;
    }

    /** ตัวเลขสวยๆ: 60, 2.1, 0.65 */
    public static String fmt(double v) {
        long r = Math.round(v * 100);
        if (r % 100 == 0) return String.valueOf(r / 100);
        if (r % 10 == 0) return String.format(Locale.US, "%.1f", r / 100.0);
        return String.format(Locale.US, "%.2f", r / 100.0);
    }

    /** 0.64 → "640 ml" */
    public static String mlTxt(double l) {
        return Math.round(l * 1000) + " ml";
    }

    /** ผ่านมานานแค่ไหน → "1 ชั่วโมง 18 นาที" */
    public static String ago(long t, long now) {
        long m = Math.max(0, (now - t) / 60000);
        return (m / 60) + " ชั่วโมง " + String.format(Locale.US, "%02d", m % 60) + " นาที";
    }

    /** "กรวย 50 ml" */
    public static String cupLbl(int i) {
        return CUPS[i][0] + " " + CUPS[i][1] + " " + CUPS[i][2];
    }
}
