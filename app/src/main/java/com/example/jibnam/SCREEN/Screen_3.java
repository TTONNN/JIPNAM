package com.example.jibnam.SCREEN;

import com.example.jibnam.BaseClass;

/** Screen_3 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_3 extends BaseClass {

    @Override
    protected void build(Box pg) {
        put(pg, txt("Screen_3"), 10, 10, WRAP, WRAP);
        Btn toScreen2 = btn("ไป Screen_2");
        onTap(toScreen2, () -> go(Screen_2.class));
        put(pg, toScreen2, 10, 40, WRAP, WRAP);
    }
}
