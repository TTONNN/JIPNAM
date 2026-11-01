package com.example.jibnam.DATA;

import android.content.ContentValues;     // ข้อมูลไฟล์ (ชื่อ, ชนิด)
import android.content.Context;
import android.net.Uri;                   // ที่อยู่ไฟล์ปลายทาง
import android.os.Build;                  // เช็กเวอร์ชัน Android
import android.provider.MediaStore;       // โฟลเดอร์ Download ของเครื่อง
import android.widget.Toast;              // ข้อความเด้งสั้น ๆ

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

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

    public static void download(Context c) {            // copy RealData.txt ไปไว้ที่ Download ของเครื่อง ← ปุ่มใน Screen_4 (ปฏิทิน)
        if (Build.VERSION.SDK_INT < 29) {                // Android 9 ลงไปใช้วิธีนี้ไม่ได้
            Toast.makeText(c, "ต้องใช้ Android 10 ขึ้นไป", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ContentValues v = new ContentValues();
            v.put(MediaStore.Downloads.DISPLAY_NAME, "RealData.txt");
            v.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            Uri uri = c.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
            OutputStream out = c.getContentResolver().openOutputStream(uri);
            out.write(Files.readAllBytes(realDataFile(c).toPath()));   // เขียนข้อมูลทั้งไฟล์
            out.close();
            Toast.makeText(c, "บันทึกไว้ที่ Download แล้ว", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(c, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
        }
    }
}
