package com.example.jibnam.DATA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Data : อ่าน/เขียนไฟล์ RealData.txt (Java ล้วน)
 *
 * 1 บรรทัด = 1 ครั้ง คั่นด้วย ;  (มีครบทั้งข้อมูลผู้ใช้ + การดื่ม)
 *   ช่อง  0       1        2       3        4       5        6         7      8      9           10                 11
 *        เวลาบันทึก;น้ำหนัก;ตื่นชม.;ตื่นนาที;นอนชม.;นอนนาที;ปริมาณ(L);ดื่มชม.;ดื่มนาที;เป้าวันนั้น(L);ดื่มรวมวันนั้น(L);แจ้งเตือน
 *   ปริมาณ 0 = กดบันทึกข้อมูลส่วนตัว (Screen_1) ไม่ได้ดื่ม → ไม่นับเป็นแก้ว
 *   บรรทัดว่าง = แก้วที่ถูกลบ
 *   บรรทัดล่างสุด = ข้อมูลผู้ใช้ล่าสุด + แจ้งเตือนล่าสุด
 *
 * เวลาบันทึก (sn) = yyyyMMddHHmmss เช่น 20261013091530 = เลขประจำบรรทัด ไม่ซ้ำ ไม่เปลี่ยน
 *
 * เพื่อนเรียกใช้ (ต้อง import DATA.Data และ DATA.DataApi):
 *   Data data = new Data(DataApi.realDataFile(this));
 *   เพิ่ม : saveUser(...)  ← Screen_1     addDrink(ลิตร, ต้องดื่ม)  ← Screen_2
 *   แก้   : editDrink(sn, ลิตร, ชม., นาที)  ← Screen_5
 *   ลบ   : deleteDrink(sn)                 ← Screen_5
 *   อ่าน  : getDrinks(Data.today())  getDrink(sn)  lastUser()  sumLiters(วัน)  drankBetween(...)
 *   แจ้งเตือน : setNoti(true/false)  notiOn()  ← ปุ่มใน Screen_2
 */
public class Data {

    private final File file;                // ไฟล์ RealData.txt

    public Data(File file) {
        this.file = file;
        List<String[]> all = readAll();          // เจอไฟล์แบบเก่า (มี U / D นำหน้า) → ล้างทิ้งเริ่มใหม่
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i)[0].equals("U") || all.get(i)[0].equals("D")) {
                writeAll(new ArrayList<>());
                break;
            }
        }
    }

    // ===== เพิ่ม (ต่อท้ายไฟล์) =====

    public void saveUser(double weightKg, int wakeH, int wakeM, int sleepH, int sleepM) {   // ข้อมูลผู้ใช้ ← Screen_1 (ปริมาณ 0)
        String sn = newSn();
        String user = weightKg + ";" + wakeH + ";" + wakeM + ";" + sleepH + ";" + sleepM;
        addLine(sn, user, 0, com.example.jibnam.CODE.WaterCalculate.targetL(weightKg));
    }

    public String addDrink(double liters, double targetL) {   // ดื่มน้ำ ← Screen_2 กด submit  คืนค่า sn ของแก้วนี้
        String sn = newSn();
        String user = "0;0;0;0;0";                             // ไม่มีข้อมูลผู้ใช้ก่อนหน้า = 0
        String[] u = lastUser();
        if (u != null) user = u[1] + ";" + u[2] + ";" + u[3] + ";" + u[4] + ";" + u[5];
        addLine(sn, user, liters, targetL);
        return sn;
    }

    private void addLine(String sn, String user, double liters, double targetL) {   // ต่อท้าย 1 บรรทัด ครบ 12 ช่อง
        int h = Integer.parseInt(sn.substring(8, 10));
        int m = Integer.parseInt(sn.substring(10, 12));
        double total = sumLiters(sn.substring(0, 8)) + liters;   // ดื่มรวมวันนั้น รวมแก้วนี้
        String noti = "off";                                    // แจ้งเตือน ใช้ค่าเดิมของบรรทัดก่อนหน้า (ไม่มี = off)
        if (notiOn()) noti = "on";
        addLine(sn + ";" + user + ";" + liters + ";" + h + ";" + m + ";" + targetL + ";" + r3(total) + ";" + noti);
    }

    private String newSn() {                // เวลาตอนนี้ กด 2 ครั้งในวินาทีเดียว → +1 กันซ้ำ
        String sn = now();
        while (getDrink(sn) != null) sn = String.valueOf(Long.parseLong(sn) + 1);
        return sn;
    }

    // ===== แก้ / ลบ (เขียนทับทั้งไฟล์) =====

    public void editDrink(String sn, double liters, int hour, int minute) {   // แก้แก้ว sn ← Screen_5 (เพื่อนต่อ)
        List<String[]> all = readAll();
        int i = find(all, sn);
        if (i < 0) return;                       // ไม่เจอ (ถูกลบไปแล้ว)
        String[] p = all.get(i);
        p[6] = String.valueOf(liters);           // เขียนทับค่า ลิตร ชม. นาที
        p[7] = String.valueOf(hour);
        p[8] = String.valueOf(minute);
        fixTotals(all, sn.substring(0, 8));
        writeAll(all);
    }

    public void deleteDrink(String sn) {    // ลบแก้ว sn ← Screen_5 (เพื่อนต่อ)
        List<String[]> all = readAll();
        int i = find(all, sn);
        if (i < 0) return;
        String noti = all.get(i)[11];
        all.set(i, new String[]{""});            // เขียนทับด้วยบรรทัดว่าง
        fixTotals(all, sn.substring(0, 8));
        int last = lastLine(all);                // ลบบรรทัดล่างสุด → ย้ายค่าแจ้งเตือนไปบรรทัดที่เป็นล่างสุดแทน
        if (last >= 0 && last < i) all.get(last)[11] = noti;
        writeAll(all);
    }

    public void setNoti(boolean on) {       // เปิด/ปิดแจ้งเตือน ← ปุ่มใน Screen_2: เขียนทับช่องท้ายสุดของบรรทัดล่างสุด
        List<String[]> all = readAll();
        int last = lastLine(all);
        if (last < 0) return;                    // ยังไม่มีบรรทัดเลย
        if (on) all.get(last)[11] = "on";
        else all.get(last)[11] = "off";
        writeAll(all);
    }

    // ===== อ่าน =====

    public List<String[]> getDrinks(String day) {   // แก้วของวัน day (yyyyMMdd, null = ทุกวัน) เรียงบน → ล่าง (ไม่เอาปริมาณ 0)
        List<String[]> out = new ArrayList<>();
        List<String[]> all = readAll();
        for (int i = 0; i < all.size(); i++) {
            String[] p = all.get(i);
            if (p.length < 12) continue;                                   // บรรทัดว่าง
            if (Double.parseDouble(p[6]) <= 0) continue;                   // บันทึกข้อมูลส่วนตัว ไม่ใช่แก้ว
            if (day == null || p[0].startsWith(day)) out.add(p);
        }
        return out;
    }

    public String[] getDrink(String sn) {   // บรรทัด sn ครบ 12 ช่อง ไม่มี = null
        List<String[]> all = readAll();
        int i = find(all, sn);
        if (i < 0) return null;
        return all.get(i);
    }

    public String[] lastUser() {            // บรรทัดล่างสุด (ใช้ช่อง 1-5 = น้ำหนัก ตื่น นอน) ไม่มี = null (ผู้ใช้ใหม่)
        List<String[]> all = readAll();
        int last = lastLine(all);
        if (last < 0) return null;
        return all.get(last);
    }

    public double sumLiters(String day) {   // ดื่มแล้วทั้งวัน day (ลิตร)
        double s = 0;
        List<String[]> ds = getDrinks(day);
        for (int i = 0; i < ds.size(); i++) s += Double.parseDouble(ds.get(i)[6]);
        return r3(s);
    }

    public double drankBetween(String day, int fromMin, int toMin) {   // ดื่มกี่ลิตรในช่วง [fromMin, toMin) นาทีของวัน (7:30 = 450)
        double s = 0;                                                   // ← Notification / Screen_6 ใช้ดูแต่ละช่วง
        List<String[]> ds = getDrinks(day);
        for (int i = 0; i < ds.size(); i++) {
            String[] d = ds.get(i);
            int t = Integer.parseInt(d[7]) * 60 + Integer.parseInt(d[8]);
            if (t >= fromMin && t < toMin) s += Double.parseDouble(d[6]);
        }
        return r3(s);
    }

    public boolean notiOn() {               // แจ้งเตือนเปิดอยู่ไหม = ช่องท้ายสุดของบรรทัดล่างสุด (ไม่มี = ปิด)
        String[] p = lastUser();
        if (p == null) return false;
        return p[11].equals("on");
    }

    public static String today() {          // วันนี้ เช่น "20261013"
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
    }

    public static String now() {            // sn ตอนนี้ เช่น "20261013091530"
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
    }

    // ===== ภายใน =====

    private int find(List<String[]> all, String sn) {   // เลขบรรทัดของ sn, ไม่เจอ = -1
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i)[0].equals(sn)) return i;
        }
        return -1;
    }

    private int lastLine(List<String[]> all) {   // เลขบรรทัดล่างสุดที่ไม่ว่าง, ไม่มี = -1
        for (int i = all.size() - 1; i >= 0; i--) {
            if (all.get(i).length >= 12) return i;
        }
        return -1;
    }

    private void fixTotals(List<String[]> all, String day) {   // คิด "ดื่มรวมวันนั้น" ของทุกบรรทัดวัน day ใหม่ ตามลำดับบน → ล่าง
        double s = 0;
        for (int i = 0; i < all.size(); i++) {
            String[] p = all.get(i);
            if (p.length < 12 || !p[0].startsWith(day)) continue;
            s += Double.parseDouble(p[6]);
            p[10] = String.valueOf(r3(s));
        }
    }

    private static double r3(double v) {    // ปัดทศนิยม 3 ตำแหน่ง (กัน 0.30000000000000004)
        return Math.round(v * 1000) / 1000.0;
    }

    private void addLine(String line) {     // ต่อท้ายไฟล์ 1 บรรทัด
        try (FileWriter w = new FileWriter(file, true)) {
            w.write(line + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeAll(List<String[]> all) {   // เขียนทับทั้งไฟล์ด้วย all (บรรทัดว่างก็เขียนกลับเป็นบรรทัดว่าง)
        try (FileWriter w = new FileWriter(file, false)) {
            for (int i = 0; i < all.size(); i++) w.write(String.join(";", all.get(i)) + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String[]> readAll() {      // อ่านทุกบรรทัด แยกด้วย ;  (บรรทัดว่าง = { "" } เก็บไว้ ให้ตำแหน่งไม่เลื่อน)
        List<String[]> all = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) all.add(line.split(";"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return all;
    }
}
