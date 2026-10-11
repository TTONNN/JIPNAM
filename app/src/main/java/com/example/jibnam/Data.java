
package com.example.jibnam;

import android.content.Context;
import java.io.Serializable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Data {

    // ชื่อไฟล์ที่ใช้เก็บข้อมูล
    private String fileName;

    // Context สำหรับเข้าถึงไฟล์ภายในแอป
    private Context context;

    // Constructor
    public Data(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    // บันทึกข้อมูล
    public void saveData(DayRecord record) {
        List<DayRecord> list = loadData();

        // ถ้ามีข้อมูลวันที่นี้อยู่แล้ว ให้แทนที่ข้อมูลเดิม
        list.removeIf(item ->
                item.getDate().equals(record.getDate())
        );

        list.add(record);

        try (FileOutputStream fos =
                     context.openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream oos =
                     new ObjectOutputStream(fos)) {

            oos.writeObject(new ArrayList<>(list));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // อ่านข้อมูลทั้งหมด
    @SuppressWarnings("unchecked")
    public List<DayRecord> loadData() {
        List<DayRecord> list = new ArrayList<>();

        try (FileInputStream fis =
                     context.openFileInput(fileName);
             ObjectInputStream ois =
                     new ObjectInputStream(fis)) {

            list = (List<DayRecord>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            // ถ้ายังไม่มีไฟล์ ให้คืนรายการว่าง
        }

        return list;
    }

    // ดึงข้อมูลรายวัน
    public DayRecord getDay(String date) {
        for (DayRecord record : loadData()) {
            if (record.getDate().equals(date)) {
                return record;
            }
        }

        return null;
    }

    // ดึงข้อมูลรายเดือน
    public List<DayRecord> getMonth(int month, int year) {
        List<DayRecord> result = new ArrayList<>();

        for (DayRecord record : loadData()) {
            String[] parts = record.getDate().split("-");

            if (parts.length != 3) {
                continue;
            }

            int recordYear = Integer.parseInt(parts[0]);
            int recordMonth = Integer.parseInt(parts[1]);

            if (recordMonth == month && recordYear == year) {
                result.add(record);
            }
        }

        return result;
    }

    // ลบข้อมูลของวันที่กำหนด
    public void deleteData(String date) {
        List<DayRecord> list = loadData();

        list.removeIf(record ->
                record.getDate().equals(date)
        );

        try (FileOutputStream fos =
                     context.openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream oos =
                     new ObjectOutputStream(fos)) {

            oos.writeObject(new ArrayList<>(list));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
