package com.example.jibnam;

/**
 * SampleHistory : ประวัติดื่มน้ำตัวอย่าง ไว้ดูหน้าจอตอนยังไม่มีข้อมูลจริงเท่านั้น
 * ตอนมีระบบเก็บข้อมูลจริงแล้ว ให้ลบบรรทัดที่เรียก SampleHistory.addTo(...) ใน Screen_2 ออก
 * อยากเพิ่ม/ลดตัวอย่าง แก้ในไฟล์นี้ไฟล์เดียว
 */
public class SampleHistory {

    /** ใส่ตัวอย่างลงประวัติของ wl — his[0] = ล่าสุด */
    public static void addTo(WaterLogic wl, long now) {
        wl.his.add(new WaterLogic.Ent(0.640, now - ago(1, 18)));
        wl.his.add(new WaterLogic.Ent(0.120, now - ago(4, 9)));
        wl.his.add(new WaterLogic.Ent(0.050, now - ago(18, 18)));
        wl.his.add(new WaterLogic.Ent(0.500, now - ago(20, 2)));
        wl.his.add(new WaterLogic.Ent(0.150, now - ago(22, 40)));
    }

    /** h ชั่วโมง m นาที → มิลลิวินาที */
    private static long ago(int h, int m) {
        return (h * 60L + m) * 60000L;
    }
}
