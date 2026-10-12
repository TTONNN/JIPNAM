package com.example.jibnam.DATA;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/** DataApi : ของ Android ที่ระบบเก็บข้อมูลต้องใช้ (Data ใช้ไฟล์ที่ได้จากที่นี่) */
public class DataApi {

    public static File realDataFile(Context c) {        // ไฟล์ RealData.txt ในพื้นที่ของแอป
        File f = new File(c.getFilesDir(), "RealData.txt");
        try {
            f.createNewFile();                           // สร้างไฟล์ตอนเริ่ม (มีอยู่แล้วจะไม่ทำอะไร)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return f;
    }
}
