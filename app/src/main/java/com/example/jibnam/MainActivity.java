package com.example.jibnam;

public class MainActivity extends BaseClass {

    @Override
    void build(Box pg) {
        add(pg, txt("Hello1", 30, C_BLK, false));

        // ชั่วคราว: เปิด Screen_2 ทันทีเพื่อดูผล (ลบได้เมื่อมีปุ่มเปลี่ยนหน้าจริง)
        go(Screen_2.class);
    }
}
