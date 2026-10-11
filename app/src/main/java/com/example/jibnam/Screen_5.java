package com.example.jibnam;

/** Screen_5 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_5 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, txt("Screen_5"));
        Btn toScreen2 = btn("ไป Screen_2");
        onTap(toScreen2, () -> go(Screen_2.class));
        add(pg, toScreen2, WRAP, WRAP);
    }
}
