package com.example.jibnam;

/** Screen_2 : ตอนนี้มีแค่ชื่อหน้า + ปุ่มเปลี่ยนหน้า */
public class Screen_2 extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, txt("Screen_2"));
        Btn toS1 = btn("ไป Screen_1");
        onTap(toS1, () -> go(Screen_1.class));
        add(pg, toS1, WRAP, WRAP);
        Btn toS3 = btn("ไป Screen_3");
        onTap(toS3, () -> go(Screen_3.class));
        add(pg, toS3, WRAP, WRAP);
        Btn toS4 = btn("ไป Screen_4");
        onTap(toS4, () -> go(Screen_4.class));
        add(pg, toS4, WRAP, WRAP);
        Btn toS5 = btn("ไป Screen_5");
        onTap(toS5, () -> go(Screen_5.class));
        add(pg, toS5, WRAP, WRAP);
        Btn toS6 = btn("ไป Screen_6");
        onTap(toS6, () -> go(Screen_6.class));
        add(pg, toS6, WRAP, WRAP);
    }
}
