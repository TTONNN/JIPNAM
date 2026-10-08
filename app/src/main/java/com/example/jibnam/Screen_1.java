package com.example.jibnam;

/** Screen_1 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า (ไว้ทดสอบ) */
public class Screen_1 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, txt("นี่คือ Screen_1", 24, C_BLK, true));

        Txt toScreen2 = btn("ไป Screen_2", C_BLU, C_WHT);
        onTap(toScreen2, () -> go(Screen_2.class));
        add(pg, toScreen2, FILL, 48);
        mar(toScreen2, 0, 12, 0, 0);
    }
}
