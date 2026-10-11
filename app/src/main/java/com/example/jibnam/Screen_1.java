package com.example.jibnam;

/** Screen_1 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_1 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, plainTxt("Screen_1"));
        Btn toScreen2 = plainBtn("ไป Screen_2");
        onTap(toScreen2, () -> go(Screen_2.class));
        add(pg, toScreen2, WRAP, WRAP);
    }
}
