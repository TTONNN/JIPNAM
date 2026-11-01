package com.example.jibnam.CODE;

import android.app.AlarmManager;                           // ตั้งปลุก
import android.app.PendingIntent;                          // งานที่ฝากระบบไว้ทำตอนถึงเวลา
import android.content.BroadcastReceiver;                  // ตัวรับ ตอนถึงเวลาปลุก
import android.content.Context;                            // ตัวแทนแอป
import android.content.Intent;                             // ใบสั่ง (ปลุกตัวนี้ / เปิดแอป)
import androidx.core.app.NotificationChannelCompat;        // ช่องแจ้งเตือน
import androidx.core.app.NotificationCompat;               // สร้างแจ้งเตือน
import androidx.core.app.NotificationManagerCompat;        // ส่งแจ้งเตือน

import com.example.jibnam.DATA.Data;
import com.example.jibnam.DATA.DataApi;

import java.util.Calendar;
import java.util.List;

/*
 * Notification : เตือนดื่มน้ำ
 *   เอาเวลาตื่น - นอน (บรรทัดล่างสุด) แบ่ง 5 ช่วง ตามสัดส่วน RATIO (เหมือน Screen_6)
 *   ทุกครั้งที่ขึ้นช่วงใหม่ (ตอนตื่น + จบแต่ละช่วง) ระบบจะปลุก onReceive → check() → เด้งแจ้งเตือน → ตั้งปลุกรอบถัดไป
 *
 *   check() คิดแบบนี้ (เรียงตามลำดับ)
 *     1. แจ้งเตือนปิดอยู่ / ยังไม่มีผู้ใช้                 → ไม่เตือน
 *     2. หายไปเต็มวัน (ไม่ได้บันทึกอะไรเลย) เป็นวันที่ 1 3 7 14 28  → BACK (วันละครั้ง ตอนจบช่วง 1)
 *     3. ตอนตื่น (เริ่มช่วง 1)                           → NEW ช่วงที่ 1
 *     4. วันนี้ดื่มเกิน เป้า × OVER_X                     → OVER
 *     5. นับช่วงที่ไม่ได้ดื่มติดกัน ย้อนจากช่วงที่เพิ่งจบ   → STACK[จำนวนช่วง]
 *     6. ช่วงที่เพิ่งจบดื่มแล้ว                          → NEW ช่วงถัดไป / DONE (ถึงเวลานอน)
 *
 * เรียกใช้ : Notification.start(this);   ← ใส่ใน Screen_2 แล้ว (ขอสิทธิ์ + ตั้งปลุกรอบแรก)
 */
public class Notification extends BroadcastReceiver {

    // ===== ข้อความ (แก้ได้ตรงนี้) =====
    static final String[] STACK = {
            "",
            "คุณขาดการดื่มน้ำมา 1 ช่วงแล้ว อย่าลืมดื่มน้ำนะ!",            // ขาด 1 ช่วง
            "คุณไม่ได้ดื่มน้ำซักพักแล้วนะ คอแห้งอยู่รึเปล่า",              // ขาด 2 ช่วง
            "คุณขาดน้ำมานานแล้ว ควรดื่มน้ำได้แล้วนะ",                    // ขาด 3 ช่วง
            "คุณแทบไม่ได้ดื่มน้ำมาทั้งวันแล้ว ควรดื่มน้ำเดี๋ยวนี้",            // ขาด 4 ช่วง
            "คุณไม่ได้ดื่มน้ำมาทั้งวันแล้วนะ อาจขาดน้ำได้ ควรดื่มน้ำ",          // ขาด 5 ช่วง
    };
    static final String OVER = "คุณดื่มน้ำมากเกินไปแล้ว ควรค่อยๆดื่มนะ";      // ดื่มเกิน
    static final String BACK = "อย่าลืมดื่มน้ำและบันทึกข้อมูลของคุณด้วยนะ";    // หายไปทั้งวัน
    static final String NEW = "เข้าช่วงที่ %d แล้ว ช่วงนี้ควรดื่มน้ำประมาณ %d ml";   // ขึ้นช่วงใหม่ (%d = เลขช่วง, ml)
    static final String DONE = "ครบทุกช่วงของวันนี้แล้ว เก่งมาก พักผ่อนนะ";       // ถึงเวลานอน + ช่วงสุดท้ายดื่มแล้ว
    static final int[] BACK_DAYS = {1, 3, 7, 14, 28};                    // หายไปวันที่เท่าไหร่ถึงเตือน
    static final double OVER_X = 1.5;                                    // ดื่มเกินเป้ากี่เท่า = มากเกินไป
    static final double[] RATIO = {0.2, 0.2, 0.2, 0.2, 0.2};                // สัดส่วน 5 ช่วง (ต้องตรงกับ ratio ใน Screen_6)

    public static String[] allMsgs() {              // ข้อความทุกแบบ (ไว้ปุ่มทดสอบใน Screen_2 วนดู)
        return new String[]{
                "[ขาด 1 ช่วง] " + STACK[1],
                "[ขาด 2 ช่วง] " + STACK[2],
                "[ขาด 3 ช่วง] " + STACK[3],
                "[ขาด 4 ช่วง] " + STACK[4],
                "[ขาด 5 ช่วง] " + STACK[5],
                "[ดื่มเกิน] " + OVER,
                "[หายไป 1 3 7 14 28 วัน] " + BACK,
                "[ขึ้นช่วงใหม่] " + String.format(NEW, 2, 422),
                "[ถึงเวลานอน] " + DONE,
        };
    }

    // ===== เรียกจากหน้าจอ =====
    public static void start(BaseClass a) {         // ขอสิทธิ์แจ้งเตือน แล้วตั้งปลุกรอบถัดไป
        if (!allowed(a)) a.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        next(a);
    }

    @Override
    public void onReceive(Context c, Intent i) {    // ถึงเวลา (จบ 1 ช่วง): เช็ก → เตือน → ตั้งรอบถัดไป
        Data data = new Data(DataApi.realDataFile(c));
        String msg = check(data, Data.today(), nowMin());
        if (msg != null) show(c, msg);
        next(c);
    }

    // ===== คิดว่าจะเตือนอะไร (null = ไม่เตือน) =====
    public static String check(Data data, String today, int now) {   // now = นาทีของวัน เช่น 14:30 = 870
        String[] u = data.lastUser();
        if (u == null || !data.notiOn()) return null;                // 1. ไม่มีผู้ใช้ / ปิดแจ้งเตือน
        int[] b = slots(u);
        if (now < b[0]) return null;                                 // ยังไม่ตื่น
        int k = 0;                                                   // จบไปแล้วกี่ช่วง (0..5)
        for (int j = 1; j <= 5; j++) if (now >= b[j]) k = j;
        double tg = WaterCalculate.targetL(Double.parseDouble(u[1]));
        int slotMl = (int) Math.round(tg * 1000 * RATIO[0]);         // ควรดื่มช่วงละกี่ ml

        int away = awayDays(data, today);                            // 2. หายไปกี่วันเต็ม
        if (away >= 1) {
            if (k != 1) return null;                                 //    วันละครั้ง (ตอนจบช่วง 1)
            for (int i = 0; i < BACK_DAYS.length; i++) if (BACK_DAYS[i] == away) return BACK;
            return null;
        }
        if (k == 0) return String.format(NEW, 1, slotMl);            // 3. เพิ่งตื่น = เริ่มช่วง 1
        if (data.sumLiters(today) > tg * OVER_X) return OVER;        // 4. ดื่มเกิน

        int miss = 0;                                                // 5. ขาดติดกันกี่ช่วง
        for (int j = k; j >= 1; j--) {
            if (data.drankBetween(today, b[j - 1], b[j]) > 0) break; //    เจอช่วงที่ดื่ม = หยุดนับ
            miss++;
        }
        if (miss > 0) return STACK[miss];
        if (k == 5) return DONE;                                     // 6. ถึงเวลานอน ช่วงสุดท้ายดื่มแล้ว
        return String.format(NEW, k + 1, slotMl);                    //    ช่วงล่าสุดดื่มแล้ว = บอกว่าขึ้นช่วงใหม่
    }

    public static int[] slots(String[] u) {         // { ตื่น, จบช่วง1, จบช่วง2, จบช่วง3, จบช่วง4, นอน } หน่วยนาทีของวัน
        int wake = Integer.parseInt(u[2]) * 60 + Integer.parseInt(u[3]);
        int sleep = Integer.parseInt(u[4]) * 60 + Integer.parseInt(u[5]);
        if (sleep <= wake) sleep += 24 * 60;                          // นอนหลังเที่ยงคืน
        int total = sleep - wake;                                    // ตื่นอยู่กี่นาที
        int[] b = new int[6];
        b[0] = wake;
        double acc = 0;
        for (int i = 0; i < 5; i++) {
            acc += RATIO[i];                                         // สัดส่วนสะสม 0.2, 0.4, 0.6, 0.8, 1.0
            b[i + 1] = wake + (int) Math.round(total * acc);
        }
        b[5] = sleep;
        return b;
    }

    static int awayDays(Data data, String today) {  // หายไปกี่วันเต็ม (0 = เมื่อวาน/วันนี้ยังบันทึกอยู่)
        String last = data.lastUser()[0].substring(0, 8);            // วันล่าสุดที่ทำอะไรในแอป (บรรทัดล่างสุด)
        List<String[]> ds = data.getDrinks(null);
        if (ds.size() > 0) {
            String d = ds.get(ds.size() - 1)[0].substring(0, 8);
            if (d.compareTo(last) > 0) last = d;
        }
        long diff = dayMs(today) - dayMs(last);
        return (int) Math.round(diff / 86400000.0) - 1;              // 86400000 ms = 1 วัน
    }

    static long dayMs(String day) {                 // "20261031" → เวลาเที่ยงคืนของวันนั้น (ms)
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8)));
        return cal.getTimeInMillis();
    }

    // ===== ตั้งปลุก / เด้งแจ้งเตือน =====
    static void next(Context c) {                   // ตั้งปลุกตอนขึ้นช่วงถัดไป (ตื่น + จบแต่ละช่วง วันนี้ไม่เหลือ = ตอนตื่นพรุ่งนี้)
        String[] u = new Data(DataApi.realDataFile(c)).lastUser();
        if (u == null) return;
        int[] b = slots(u);
        long t = 0;
        for (int day = 0; day <= 1 && t == 0; day++) {
            for (int j = 0; j <= 5; j++) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.DAY_OF_MONTH, day);
                cal.add(Calendar.MINUTE, b[j]);
                if (cal.getTimeInMillis() > System.currentTimeMillis() + 30000) { t = cal.getTimeInMillis(); break; }
            }
        }
        PendingIntent pi = PendingIntent.getBroadcast(c, 0, new Intent(c, Notification.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi);    // ไม่เป๊ะ อาจช้าไปบ้าง (ไม่ต้องขอสิทธิ์เพิ่ม)
    }

    public static void show(Context c, String msg) {   // เด้งแจ้งเตือน กดแล้วเปิดแอป
        if (!allowed(c)) return;
        NotificationManagerCompat nm = NotificationManagerCompat.from(c);
        nm.createNotificationChannel(new NotificationChannelCompat.Builder("water", NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName("เตือนดื่มน้ำ").build());
        PendingIntent open = PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        nm.notify(1, new NotificationCompat.Builder(c, "water")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("JIBNAM")
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(open)
                .setAutoCancel(true)
                .build());
    }

    static boolean allowed(Context c) {             // ได้สิทธิ์แจ้งเตือนไหม
        return NotificationManagerCompat.from(c).areNotificationsEnabled();
    }

    public static int nowMin() {                           // ตอนนี้กี่นาทีของวัน
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
    }
}
