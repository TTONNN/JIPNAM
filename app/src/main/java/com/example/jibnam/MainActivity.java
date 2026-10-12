package com.example.jibnam;

import com.example.jibnam.SCREEN.Screen_2;

public class MainActivity extends BaseClass {

    @Override
    protected void build(Box pg) {
        add(pg, txt("Hello1"));

        // ชั่วคราว: เปิด Screen_2 ทันทีเพื่อดูผล (ลบได้เมื่อมีปุ่มเปลี่ยนหน้าจริง)
        go(Screen_2.class);
    }
}
