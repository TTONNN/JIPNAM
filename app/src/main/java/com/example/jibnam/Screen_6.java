package com.example.jibnam;

/** Screen_6 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_6 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, txt("นี่คือ Screen_6", 24, C_BLK, true));

        Txt toScreen4 = btn("ไป Screen_4", C_BLU, C_WHT);
        onTap(toScreen4, () -> go(Screen_4.class));
        add(pg, toScreen4, FILL, 48);
        mar(toScreen4, 0, 12, 0, 0);
    }
}
