package com.example.jibnam.DATA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Data : อ่าน/เขียนไฟล์ RealData.txt (Java ล้วน)
 *
 * 1 บรรทัด = 1 ข้อมูล คั่นด้วย ;  ตัวแรกบอกชนิด
 *   U;sn;น้ำหนัก;ตื่นชม.;ตื่นนาที;นอนชม.;นอนนาที                 ข้อมูลผู้ใช้ (บรรทัด U ล่างสุด = ค่าปัจจุบัน)
 *   D;sn;ลิตร;ชม.;นาที;ต้องดื่ม(L);ดื่มแล้วทั้งวัน(L);แจ้งเตือน;ตื่นชม.;ตื่นนาที;นอนชม.;นอนนาที
 *                                                                 ดื่มน้ำ 1 แก้ว (ตื่น/นอน = ค่าจาก U ล่าสุดตอนที่ดื่ม)
 *   (บรรทัดว่าง)                                                  แก้วที่ถูกลบ
 *
 * sn (serial) = วันเวลาที่สร้าง yyyyMMddHHmmss เช่น 20261013091530 = เลขประจำแก้ว ไม่ซ้ำ ไม่เปลี่ยน
 * แจ้งเตือน (on/off) = ตัวท้ายสุดของบรรทัด D ล่างสุด
 *
 * วิธีเขียน
 *   เพิ่ม = ต่อท้ายไฟล์ 1 บรรทัด                    (addLine)
 *   แก้   = อ่านทั้งไฟล์ → หาบรรทัด sn → เปลี่ยนค่า → เขียนทับทั้งไฟล์   (readAll / writeAll)
 *   ลบ   = อ่านทั้งไฟล์ → หาบรรทัด sn → เปลี่ยนเป็นบรรทัดว่าง → เขียนทับทั้งไฟล์
 *   แก้/ลบ เสร็จ คิดยอด "ดื่มแล้วทั้งวัน" ของวันนั้นใหม่ทุกแก้ว (fixTotals) ตัวเลขในไฟล์จึงถูกตลอด
 *
 * เพื่อนเรียกใช้ (ต้อง import DATA.Data และ DATA.DataApi):
 *   Data data = new Data(DataApi.realDataFile(this));
 *   เพิ่ม : saveUser(...)  addDrink(ลิตร, ต้องดื่ม)
 *   แก้   : editDrink(sn, ลิตร, ชม., นาที)          ← Screen_5
 *   ลบ   : deleteDrink(sn)                         ← Screen_5
 *   อ่าน  : getDrinks(Data.today())  getDrink(sn)  lastUser()  sumLiters(วัน)  drankBetween(...)
 *   แจ้งเตือน : setNoti(true/false)  notiOn()        ← ปุ่มใน Screen_2
 */
public class Data {

    private final File file;                // ไฟล์ RealData.txt

    public Data(File file) {
        this.file = file;
    }

    // ===== เพิ่ม (ต่อท้ายไฟล์) =====

    public void saveUser(double weightKg, int wakeH, int wakeM, int sleepH, int sleepM) {   // ข้อมูลผู้ใช้ ← Screen_1
        addLine("U;" + now() + ";" + weightKg + ";" + wakeH + ";" + wakeM + ";" + sleepH + ";" + sleepM);
    }

    public String addDrink(double liters, double targetL) {   // ดื่มน้ำ ← Screen_2 กด submit  คืนค่า sn ของแก้วนี้
        String sn = now();
        while (getDrink(sn) != null) sn = String.valueOf(Long.parseLong(sn) + 1);   // กด 2 ครั้งในวินาทีเดียว → +1 กันซ้ำ
        int h = Integer.parseInt(sn.substring(8, 10));
        int m = Integer.parseInt(sn.substring(10, 12));
        double total = sumLiters(sn.substring(0, 8)) + liters;                    // ดื่มแล้วทั้งวัน รวมแก้วนี้
        String[] u = lastUser();                                                   // เวลาตื่น/นอน จาก Screen_1 (บรรทัด U ล่าสุด)
        String wakeSleep = "0;0;0;0";                                              // ยังไม่มี U = 0
        if (u != null) wakeSleep = u[3] + ";" + u[4] + ";" + u[5] + ";" + u[6];
        String noti = "off";                                                       // แจ้งเตือน ใช้ค่าเดิมของแก้วก่อนหน้า
        if (notiOn()) noti = "on";
        addLine("D;" + sn + ";" + liters + ";" + h + ";" + m + ";" + targetL + ";" + r3(total) + ";" + noti
                + ";" + wakeSleep);                                               // ต่อท้าย: ตื่นชม.;ตื่นนาที;นอนชม.;นอนนาที
        return sn;
    }

    // ===== แก้ / ลบ (เขียนทับทั้งไฟล์) =====

    public void editDrink(String sn, double liters, int hour, int minute) {   // แก้แก้ว sn ← Screen_5 (เพื่อนต่อ)
        List<String[]> all = readAll();
        int i = find(all, sn);
        if (i < 0) return;                       // ไม่เจอ (ถูกลบไปแล้ว)
        String[] p = all.get(i);
        p[2] = String.valueOf(liters);           // เขียนทับค่า ลิตร ชม. นาที
        p[3] = String.valueOf(hour);
        p[4] = String.valueOf(minute);
        fixTotals(all, sn.substring(0, 8));
        writeAll(all);
    }

    public void deleteDrink(String sn) {    // ลบแก้ว sn ← Screen_5 (เพื่อนต่อ)
        List<String[]> all = readAll();
        int i = find(all, sn);
        if (i < 0) return;
        String noti = "off";
        if (all.get(i).length > 7) noti = all.get(i)[7];
        all.set(i, new String[]{""});            // เขียนทับด้วยบรรทัดว่าง
        fixTotals(all, sn.substring(0, 8));
        int last = lastD(all);                   // ลบแก้วล่างสุด → ย้ายค่าแจ้งเตือนไปแก้วที่เป็นล่างสุดแทน
        if (last >= 0 && last < i) setField(all, last, 7, noti);
        writeAll(all);
    }

    public void setNoti(boolean on) {       // เปิด/ปิดแจ้งเตือน ← ปุ่มใน Screen_2: เขียนทับตัวท้ายสุดของบรรทัด D ล่างสุด
        List<String[]> all = readAll();
        int last = lastD(all);
        if (last < 0) return;                    // ยังไม่เคยดื่ม = ยังไม่มีบรรทัดให้เก็บ
        if (on) setField(all, last, 7, "on");
        else setField(all, last, 7, "off");
        writeAll(all);
    }

    // ===== อ่าน =====

    public List<String[]> getDrinks(String day) {   // แก้วของวัน day (yyyyMMdd, null = ทุกวัน) เรียงบน → ล่าง
        List<String[]> out = new ArrayList<>();
        List<String[]> all = readAll();
        for (int i = 0; i < all.size(); i++) {
            String[] p = all.get(i);
            if (p[0].equals("D") && (day == null || p[1].startsWith(day))) out.add(p);
        }
        return out;
    }

    public String[] getDrink(String sn) {   // แก้ว sn = { "D", sn, ลิตร, ชม., นาที, ต้องดื่ม, ดื่มแล้วทั้งวัน, แจ้งเตือน, ตื่นชม., ตื่นนาที, นอนชม., นอนนาที } ไม่มี = null
        List<String[]> all = readAll();
        int i = find(all, sn);
        if (i < 0) return null;
        return all.get(i);
    }

    public String[] lastUser() {            // บรรทัด U ล่างสุด = { "U", sn, น้ำหนัก, ตื่นชม., ตื่นนาที, นอนชม., นอนนาที } ไม่มี = null
        String[] u = null;
        List<String[]> all = readAll();
        for (int i = 0; i < all.size(); i++) if (all.get(i)[0].equals("U")) u = all.get(i);
        return u;
    }

    public List<String[]> getLines(String type, String day) {   // บรรทัดชนิด type ของวัน day (null = ทุกวัน) เช่น "D"
        List<String[]> out = new ArrayList<>();
        List<String[]> all = readAll();
        for (int i = 0; i < all.size(); i++) {
            String[] p = all.get(i);
            if (p[0].equals(type) && (day == null || p[1].startsWith(day))) out.add(p);
        }
        return out;
    }

    public double sumLiters(String day) {   // ดื่มแล้วทั้งวัน day (ลิตร)
        double s = 0;
        List<String[]> ds = getDrinks(day);
        for (int i = 0; i < ds.size(); i++) s += Double.parseDouble(ds.get(i)[2]);
        return r3(s);
    }

    public double drankBetween(String day, int fromMin, int toMin) {   // ดื่มกี่ลิตรในช่วง [fromMin, toMin) นาทีของวัน (7:30 = 450)
        double s = 0;                                                   // ← Notification / Screen_6 ใช้ดูแต่ละช่วง
        List<String[]> ds = getDrinks(day);
        for (int i = 0; i < ds.size(); i++) {
            String[] d = ds.get(i);
            int t = Integer.parseInt(d[3]) * 60 + Integer.parseInt(d[4]);
            if (t >= fromMin && t < toMin) s += Double.parseDouble(d[2]);
        }
        return r3(s);
    }

    public boolean notiOn() {               // แจ้งเตือนเปิดอยู่ไหม = ตัวท้ายสุดของบรรทัด D ล่างสุด (ไม่มี = ปิด)
        List<String[]> all = readAll();
        int last = lastD(all);
        if (last < 0) return false;
        String[] p = all.get(last);
        return p.length > 7 && p[7].equals("on");
    }

    public static String today() {          // วันนี้ เช่น "20261013"
        return new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
    }

    public static String now() {            // sn ตอนนี้ เช่น "20261013091530"
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
    }

    // ===== ภายใน =====

    private int find(List<String[]> all, String sn) {   // เลขบรรทัดของแก้ว sn, ไม่เจอ = -1
        for (int i = 0; i < all.size(); i++) {
            String[] p = all.get(i);
            if (p[0].equals("D") && p[1].equals(sn)) return i;
        }
        return -1;
    }

    private int lastD(List<String[]> all) {   // เลขบรรทัด D ล่างสุด, ไม่มี = -1
        for (int i = all.size() - 1; i >= 0; i--) {
            if (all.get(i)[0].equals("D")) return i;
        }
        return -1;
    }

    private void setField(List<String[]> all, int i, int k, String v) {   // เขียนทับช่องที่ k ของบรรทัด i (ช่องไม่พอ = ขยายให้)
        String[] p = all.get(i);
        if (p.length <= k) {
            p = Arrays.copyOf(p, k + 1);
            for (int j = 0; j < p.length; j++) if (p[j] == null) p[j] = "0";
            all.set(i, p);
        }
        p[k] = v;
    }

    private void fixTotals(List<String[]> all, String day) {   // คิด "ดื่มแล้วทั้งวัน" ของทุกแก้ววัน day ใหม่ ตามลำดับบน → ล่าง
        double s = 0;
        for (int i = 0; i < all.size(); i++) {
            String[] p = all.get(i);
            if (!p[0].equals("D") || !p[1].startsWith(day)) continue;
            s += Double.parseDouble(p[2]);
            setField(all, i, 6, String.valueOf(r3(s)));
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
