package com.example.jibnam.SCREEN;

import com.example.jibnam.R;
import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;
import java.util.*;                     // List, ArrayList, Calendar, Random, Locale

/**
 * Screen_3 : เติมน้ำให้หยดน้ำ (Streak แบบเติมไฟ TikTok)
 *   วันไหนมีบันทึกน้ำอย่างน้อย 1 แก้ว = กรอกแล้ว 1 วัน
 *   ต่อเนื่องไม่ถึง 3 วัน → หยดน้ำร้องไห้
 *   ครบ 3 วันขึ้นไป     → หยดน้ำเปลี่ยนท่าทุกวัน (เปลี่ยนตอนกรอกแก้วแรกของวัน) สุ่มวนครบ 11 ตัวค่อยซ้ำ
 *   ขาด 1 วัน            → นับใหม่ กลับเป็นหยดน้ำร้องไห้
 *   วันนี้ยังไม่กรอก ยังไม่ถือว่าขาด (นับถึงเมื่อวาน)
 *
 * Screen_2 ใช้ด้วย:  Screen_3.calc(data);  แล้วอ่าน Screen_3.days  Screen_3.pic
 */
public class Screen_3 extends BaseClass {

    static final int NEED = 3;           // ต้องกรอกต่อเนื่องกี่วันหยดน้ำถึงหายเศร้า
    static final int[] CHAR = {          // หยดน้ำที่สุ่มวน (ทุกตัวยกเว้นร้องไห้)
            R.drawable.ch_1, R.drawable.ch_2, R.drawable.ch_3, R.drawable.ch_4, R.drawable.ch_5, R.drawable.ch_6,
            R.drawable.ch_7, R.drawable.ch_8, R.drawable.ch_9, R.drawable.ch_10, R.drawable.ch_11
    };

    static int days = 0;                 // วันต่อเนื่อง
    static int pic = R.drawable.ch_cry;  // รูปที่ต้องโชว์
    static boolean doneToday = false;    // วันนี้กรอกแล้วหรือยัง

    @Override
    protected void build(Box pg) {
        calc(new Data(DataApi.realDataFile(this)));

        Img back = imgBtn(R.drawable.b_back);                 // กลับ Screen_2
        onTap(back, () -> finish());
        put(pg, back, 30, 30, WRAP, WRAP);

        Txt title = txt("เติมน้ำให้หยดน้ำ");
        title.setTextSize(24);
        bold(title);
        centerX(pg, title, 0, 190, WRAP, WRAP);

        Img p = img();                                        // หยดน้ำตรงกลาง
        setImg(p, pic);
        centerX(pg, p, 0, 290, 640, 640);

        Txt count = txt("💧 " + days + " วันต่อเนื่อง");       // ยอดวันต่อเนื่อง
        count.setTextSize(24);
        bold(count);
        centerX(pg, count, 0, 960, WRAP, WRAP);

        Txt status = txt("");
        if (days < NEED) setTxt(status, "อีก " + (NEED - days) + " วัน หยดน้ำจะหายเศร้า");
        else if (doneToday) setTxt(status, "วันนี้เติมน้ำแล้ว เจอกันพรุ่งนี้นะ");
        else setTxt(status, "วันนี้ยังไม่ได้เติมน้ำ อย่าให้หยดน้ำร้องไห้นะ");
        centerX(pg, status, 0, 1050, WRAP, WRAP);

        // เงื่อนไขเลี้ยงหยดน้ำ
        Col rule = col();
        border(rule, 0xFFE8F6FF);                             // กรอบ พื้นฟ้าอ่อน
        rule.setPadding(40, 30, 40, 30);
        Txt rTitle = txt("💧 เลี้ยงหยดน้ำให้สดชื่นทุกวัน");
        bold(rTitle);
        add(rule, rTitle, WRAP, WRAP);
        add(rule, txt("• บันทึกการดื่มน้ำอย่างน้อยวันละ 1 ครั้ง"), WRAP, WRAP);
        put(pg, rule, 30, 1150, 1020, WRAP);
    }

    // ===== ระบบ streak =====

    static void calc(Data data) {        // คิด days, pic, doneToday จากไฟล์
        List<String> logged = new ArrayList<>();             // วันที่มีบันทึกน้ำ (ไม่ซ้ำ) เช่น "20261101"
        List<String[]> ds = data.getDrinks(null);
        for (int i = 0; i < ds.size(); i++) {
            String d = ds.get(i)[0].substring(0, 8);         // ช่องเวลาบันทึก 8 ตัวแรก = วัน
            if (!logged.contains(d)) logged.add(d);
        }

        String today = Data.today();
        doneToday = logged.contains(today);
        String last = today;                                 // วันล่าสุดที่นับ
        if (!doneToday) last = addDay(today, -1);            // วันนี้ยังไม่กรอก → เริ่มนับจากเมื่อวาน

        days = 0;
        String day = last;
        while (logged.contains(day)) {                       // นับย้อนหลังทีละวัน จนเจอวันที่ขาด
            days++;
            day = addDay(day, -1);
        }
        pic = R.drawable.ch_cry;
        if (days >= NEED) pic = CHAR[charOfDay(last)];
    }

    static String addDay(String day, int n) {               // "20261101", -1 → "20261031"
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8)), 12, 0, 0);
        c.add(Calendar.DAY_OF_MONTH, n);
        return String.format(Locale.US, "%04d%02d%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    static int charOfDay(String day) {                       // ตัวที่เท่าไหร่ของวันนั้น: สุ่มลำดับไว้ 1 ครั้ง แล้วไล่ทีละวัน ครบแล้ววนใหม่
        int[] order = new int[CHAR.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Random r = new Random(2026);                         // seed เดิม = ลำดับสุ่มเดิมทุกครั้ง
        for (int i = order.length - 1; i > 0; i--) {         // สลับไพ่
            int j = r.nextInt(i + 1);
            int t = order[i];
            order[i] = order[j];
            order[j] = t;
        }
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(day.substring(0, 4)), Integer.parseInt(day.substring(4, 6)) - 1, Integer.parseInt(day.substring(6, 8)), 12, 0, 0);
        long dayNo = c.getTimeInMillis() / 86400000L;        // เลขวัน (เพิ่มวันละ 1)
        return order[(int) (dayNo % CHAR.length)];
    }
}
