package com.example.jibnam;

/** Screen_4 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_4 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, plainTxt("Screen_4"));
        Btn toScreen2 = plainBtn("ไป Screen_2");
        onTap(toScreen2, () -> go(Screen_2.class));
        add(pg, toScreen2, WRAP, WRAP);
        Btn toScreen6 = plainBtn("ไป Screen_6");
        onTap(toScreen6, () -> go(Screen_6.class));
        add(pg, toScreen6, WRAP, WRAP);
    }
}
