package com.example.jibnam.SCREEN;

import com.example.jibnam.BaseClass;

/** Screen_6 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_6 extends BaseClass {

    @Override
    protected void build(Box pg) {
        add(pg, txt("Screen_6"));
        Btn toScreen2 = btn("ไป Screen_2");
        onTap(toScreen2, () -> go(Screen_2.class));
        add(pg, toScreen2, WRAP, WRAP);
    }
}
