package com.example.jibnam;

import java.util.ArrayList;
import java.util.List;

/**
 * SampleHistory : ประวัติดื่มน้ำตัวอย่าง ไว้ทดสอบหน้าจอตอนยังไม่มีข้อมูลจริง
 * อยากเพิ่ม/ลดตัวอย่าง แก้ในไฟล์นี้ไฟล์เดียว
 */
public class SampleHistory {

    public static class Drink {          // ตัวอย่าง 1 แก้ว
        public final double l;           // ปริมาณ (ลิตร)
        public final long t;             // เวลาที่ดื่ม (มิลลิวินาที)
        public Drink(double l, long t) { this.l = l; this.t = t; }
    }

    public static List<Drink> make(long now) {   // ตัวอย่าง 5 แก้ว [0] = ล่าสุด
        List<Drink> list = new ArrayList<>();
        list.add(new Drink(0.640, now - ago(1, 18)));
        list.add(new Drink(0.120, now - ago(4, 9)));
        list.add(new Drink(0.050, now - ago(18, 18)));
        list.add(new Drink(0.500, now - ago(20, 2)));
        list.add(new Drink(0.150, now - ago(22, 40)));
        return list;
    }

    private static long ago(int h, int m) {      // h ชั่วโมง m นาที → มิลลิวินาที
        return (h * 60L + m) * 60000L;
    }
}
