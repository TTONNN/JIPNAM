package com.example.jibnam.CODE;

import java.util.Locale;

/** WaterCalculate : คำนวณน้ำ — เป้าที่ควรดื่ม, ดื่มแล้ว/ยังขาด, แปลงหน่วยตอนแสดงผล */
public class WaterCalculate {

    /*
     * น้ำที่ควรดื่มต่อวัน = น้ำหนัก (กิโลกรัม) x 2.2 x 30 / 2  (มิลลิลิตร)
     * เช่น 60 กก. → 60 x 2.2 x 30 / 2 = 1980 ml = 1.98 L
     * เทียบค่าแนะนำ: กรมอนามัย ~2 ลิตร/วัน
     */
    public static double targetL(double weightKg) {          // คืนค่าเป็นลิตร (ไฟล์กับหน้าจอใช้ลิตร)
        double ml = weightKg * 2.2 * 30 / 2;                    // สูตร (มล.)
        return Math.round(ml) / 1000.0;                         // ปัดเป็น ml เต็ม แล้วแปลงเป็นลิตร
    }

    // ===== แสดงผล (ส่งค่าไปมาเป็นลิตรเสมอ แปลงตอนโชว์เท่านั้น) =====

    public static String L(double l) {                          // ยอดรวม/เป้า: 1.98 → "1.98 L"
        return String.format(Locale.US, "%.2f", l) + " L";
    }

    public static String ml(double l) {                         // ทีละแก้ว/ช่วง: 0.15 → "150 ml"
        return Math.round(l * 1000) + " ml";
    }
}
