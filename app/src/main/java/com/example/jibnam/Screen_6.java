package com.example.jibnam;

/** Screen_6 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_6 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, plainTxt("Screen_6"));
        Btn toScreen4 = plainBtn("ไป Screen_4");
        onTap(toScreen4, () -> go(Screen_4.class));
        add(pg, toScreen4, WRAP, WRAP);
    }
}
