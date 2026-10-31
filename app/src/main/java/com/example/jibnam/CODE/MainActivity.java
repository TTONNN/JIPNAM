package com.example.jibnam.CODE;

import com.example.jibnam.DATA.Data;
import com.example.jibnam.DATA.DataApi;
import com.example.jibnam.SCREEN.Screen_1;
import com.example.jibnam.SCREEN.Screen_2;

/** MainActivity : หน้าแรกตอนเปิดแอป (ไม่มีอะไรให้เห็น) เช็กว่าเป็นผู้ใช้ใหม่ไหม แล้วส่งไปหน้าที่ถูก */
public class MainActivity extends BaseClass {

    @Override
    protected void build(Box pg) {
        Data data = new Data(DataApi.realDataFile(this));   // ไม่มีไฟล์ = สร้างไฟล์เปล่าให้
        if (data.lastUser() == null) go(Screen_1.class);    // ผู้ใช้ใหม่ (ยังไม่มีบรรทัด U) → หน้า 1 กรอกข้อมูล
        else go(Screen_2.class);                             // เคยกรอกแล้ว → หน้า 2 เลย
        finish();                                            // ปิดหน้านี้ กด back จะได้ไม่กลับมาหน้าว่าง
    }
}
