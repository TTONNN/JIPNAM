package com.example.jibnam.CODE;

import com.example.jibnam.SCREEN.Screen_2;

public class MainActivity extends BaseClass {

    @Override
    protected void build(Box pg) {
        put(pg, txt("Hello1"), 10, 10, WRAP, WRAP);

        // ชั่วคราว: เปิด Screen_2 ทันทีเพื่อดูผล (ลบได้เมื่อมีปุ่มเปลี่ยนหน้าจริง)
        go(Screen_2.class);
    }


}
