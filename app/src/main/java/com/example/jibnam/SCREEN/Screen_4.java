package com.example.jibnam.SCREEN;

import com.example.jibnam.R;
import com.example.jibnam.CODE.*;
import com.example.jibnam.DATA.*;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.graphics.Color;

/** Screen_4 : ปฏิทินรายเดือน สีตาม % ที่ดื่มของแต่ละวัน กดวันที่ดูรายละเอียด */
public class Screen_4 extends BaseClass {

    CalendarMonth cal;
    Data data;
    Txt title;
    Box calenderBox;

    Txt summary;                                          // สรุปทั้งเดือน (โชว์ตลอด)
    Txt detail;                                           // รายละเอียดวันที่กด (ว่างตอนแรก กดแล้วขึ้นตรงนี้)

    // หน่วย px ทั้งหมด (จอกว้าง 1080 เว้นขอบซ้ายขวาข้างละ 30)
    static final int CELL_W = 145;                        // ความกว้างช่องวันที่ × 7 = 1015
    static final int CELL_H = 135;                        // ความสูงช่องวันที่
    static final int HEAD_H = 75;                         // ความสูงแถบชื่อวัน (อา จ อ ...)

    static final int BLUE = Color.rgb(173, 216, 255);     // 🟦 ดื่มเกิน 120% ขึ้นไป
    static final int GREEN = Color.rgb(190, 230, 190);    // 🟩 ครบ 100-119%
    static final int YELLOW = Color.rgb(255, 235, 160);   // 🟨 50-99%
    static final int RED = Color.rgb(245, 180, 180);      // 🟥 ต่ำกว่า 50%

    @Override
    protected void build(Box pg) {
        Calendar today = Calendar.getInstance();
        int month = today.get(Calendar.MONTH) + 1;
        int year = today.get(Calendar.YEAR);

        Txt head = txt("Water History");                  // หัวบนสุดของหน้า
        head.setTextSize(24);
        bold(head);
        centerX(pg, head, 0, 40, WRAP, WRAP);

        Img Back = imgBtn(R.drawable.b_back);
        onTap(Back, () -> finish());                      // ปิดหน้านี้ กลับ Screen_2 หน้าเดิม
        put(pg, Back, 30, 170, WRAP, WRAP);

        cal = new CalendarMonth(month, year); // แสดงที่ปัจจุบันเสมอ
        data = new Data(DataApi.realDataFile(this)); //อ่านไฟล์

        title = txt(" "); //เอาไว้แสดงเดือนและปี
        title.setTextSize(20);
        bold(title);
        title.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
        put(pg, title, 160, 330, 760, 110);

        Img prev = imgBtn(R.drawable.b_prev);             // ปุ่มรูปลูกศร
        Img next = imgBtn(R.drawable.b_next);
        put(pg, prev, 30, 325, WRAP, WRAP);
        put(pg, next, 930, 325, WRAP, WRAP);

        onTap(prev, () -> {
            cal.previousMonth();
            updateCalendar();
        });
        onTap(next, () -> {
            cal.nextMonth();
            updateCalendar();
        });

        int calH = HEAD_H + CELL_H * 6;                   // ปฏิทินสูงสุด 6 แถว
        calenderBox = box();
        put(pg, calenderBox, 30, 460, CELL_W * 7, calH);

        // คำอธิบายสี (ใต้ปฏิทิน)
        int ly = 460 + calH + 30;
        Txt lt = txt("สีในปฏิทินหมายถึง");
        lt.setTextSize(20);
        bold(lt);
        put(pg, lt, 30, ly, WRAP, WRAP);
        legend(pg, ly + 75, "🟦 สีฟ้า = ดื่มเยอะ (120% ขึ้นไป)");
        legend(pg, ly + 145, "🟩 สีเขียว = ดื่มครบแล้ว (100% - 119%)");
        legend(pg, ly + 215, "🟨 สีเหลือง = ดื่มไปแล้วครึ่งนึง (50% - 99%)");
        legend(pg, ly + 285, "🟥 สีแดง = ดื่มน้อยมาก (ต่ำกว่า 50%)");
        legend(pg, ly + 355, "⬜ สีขาว = ไม่ได้บันทึก");

        summary = txt("");                                // สรุปเดือน ต่อบรรทัดจากคำอธิบายสี อยู่ตลอด
        summary.setTextSize(18);
        put(pg, summary, 40, ly + 430, 1000, WRAP);

        detail = txt("");                                 // วันที่กด ว่างไว้ก่อน
        detail.setTextSize(18);
        bold(detail);
        detail.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
        put(pg, detail, 30, ly + 720, 1020, 280);

        // ===== ล่างสุดชิดซ้าย: ปุ่มดาวน์โหลด + กล่องวิธีอ่านไฟล์ =====
        Img dlBtn = imgBtn(R.drawable.b_download);
        onTap(dlBtn, () -> DataApi.download(this));       // copy RealData.txt ไปที่ Download (DATA/DataApi)
        put(pg, dlBtn, 30, ly + 1030, WRAP, WRAP);

        Col how = col();                                  // กล่องวิธีอ่านไฟล์ ตัวเล็ก 16
        border(how, 0xFFF4F9FF);                          // กรอบ พื้นฟ้าอ่อนมาก
        how.setPadding(36, 24, 36, 24);
        Txt hTitle = txt("📄 วิธีอ่านไฟล์ RealData.txt");
        hTitle.setTextSize(16);
        bold(hTitle);
        add(how, hTitle, WRAP, WRAP);
        String[] lines = {                                // 1 ข้อความ = 1 บรรทัดใหม่เสมอ (Col เรียงลง)
                "[ บรรทัด = บันทึก 1 ครั้ง ข้อมูลคั่นด้วย ; ]",
                "1. เวลาบันทึก (ปี เดือน วัน ชั่วโมง นาที วินาที)",
                "2. น้ำหนัก (กิโลกรัม)",
                "3. เวลาตื่น (ชั่วโมง ; นาที)",
                "4. เวลานอน (ชั่วโมง ; นาที)",
                "5. ปริมาณที่ดื่ม (ลิตร) ถ้าเป็น 0 = บันทึกข้อมูลส่วนตัว",
                "6. เวลาที่ดื่ม (ชั่วโมง ; นาที)",
                "7. เป้าที่ควรดื่มวันนั้น (ลิตร)",
                "8. ดื่มรวมวันนั้น (ลิตร)",
                "9. แจ้งเตือน (on = เปิด, off = ปิด)",
                "บรรทัดว่าง = รายการที่ลบไปแล้ว"
        };
        for (int i = 0; i < lines.length; i++) {
            Txt t = txt(lines[i]);
            t.setTextSize(16);
            add(how, t, WRAP, WRAP);
        }
        put(pg, how, 30, ly + 1190, 1020, WRAP);

        updateCalendar();
    }

    boolean isOutside(String[] d) {                      // แก้วนี้ดื่มนอกเวลาตื่น-นอนไหม (ใช้เวลาตื่น/นอนที่เก็บไว้ในบรรทัดนั้น)
        int t = toMin(d[7], d[8]);                       // เวลาที่ดื่ม  (ช่อง 8-9)
        int wake = toMin(d[2], d[3]);                    // เวลาตื่น   (ช่อง 3-4)
        int sleep = toMin(d[4], d[5]);                   // เวลานอน   (ช่อง 5-6)
        if (wake < sleep) return t < wake || t >= sleep; // ปกติ เช่น ตื่น 7:00 นอน 23:00
        return t >= sleep && t < wake;                   // นอนหลังเที่ยงคืน เช่น ตื่น 7:00 นอน 1:00
    }

    int toMin(String h, String m) {                      // "7","30" → 450 นาที
        return Integer.parseInt(h) * 60 + Integer.parseInt(m);
    }

    void legend(Box pg, int y, String text) {             // คำอธิบายสี 1 แถว: อีโมจิสี + ข้อความ
        Txt t = txt(text);
        t.setTextSize(18);
        put(pg, t, 40, y, WRAP, WRAP);
    }

    void updateCalendar() {
        String[] month = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
                "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
        title.setText(month[cal.getMonth() - 1] + " " + cal.getYear());
        clr(calenderBox); // ลบวันที่ในเดือนเก่า
        setTxt(detail, ""); // เปลี่ยนเดือน = ล้างรายละเอียดวันที่กดไว้

        int cellW = CELL_W;
        int cellH = CELL_H;
        int headH = HEAD_H;

        String[] days = {"อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"};
        for (int i = 0; i < 7; i++) {
            Txt day = txt(days[i]);
            day.setTextSize(18);
            bold(day);
            day.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
            put(calenderBox, day, i * cellW, 0, cellW, headH);
        }

        int firstDay = cal.getFirstDayOfMonth();
        int daysInMonth = cal.howManyDayInMonth();

        int currentCell = firstDay;
        if (currentCell < 0) {
            currentCell = 0;
        }

        int startY = headH; // เริ่มต่อจากแถบชื่อวัน
        double totalMonthLiters = 0;
        int activeDays = 0;

        for (int i = 1; i <= daysInMonth; i++) {

            int selectDay = i;
            int col = currentCell % 7; // แนวนอน
            int row = currentCell / 7; // แนวตั้ง

            int x = col * cellW;
            int y = row * cellH + startY;

            String dayFormatted = String.format(Locale.US, "%04d%02d%02d",
                    cal.getYear(), cal.getMonth(), i);

            List<String[]> drinks = data.getDrinks(dayFormatted);
            double targetLiters = 2.0; // ค่าเริ่มต้นสำรอง (ยังไม่มีผู้ใช้)
            if (!drinks.isEmpty()) {
                targetLiters = Double.parseDouble(drinks.get(0)[9]);        // เป้าที่เก็บไว้ในวันนั้น
            } else {
                String[] userInfo = data.lastUser();
                if (userInfo != null) {
                    targetLiters = WaterCalculate.targetL(Double.parseDouble(userInfo[1]));   // คิดจากน้ำหนักล่าสุด
                }
            }

            double drankLiters = data.sumLiters(dayFormatted);
            int bgColor = Color.WHITE;
            String percen = "-";

            if (drankLiters > 0 && targetLiters > 0) {
                totalMonthLiters += drankLiters;
                activeDays++;

                double value = (drankLiters / targetLiters) * 100;
                int percentInt = (int) Math.round(value);
                percen = percentInt + "%";

                if (value >= 120) {
                    bgColor = BLUE;
                } else if (value >= 100) {
                    bgColor = GREEN;
                } else if (value >= 50) {
                    bgColor = YELLOW;
                } else {
                    bgColor = RED;
                }
            }

            Box dayBox = box();
            border(dayBox, bgColor);                  // กรอบดำ + สีพื้นตาม %
            put(calenderBox, dayBox, x, y, cellW, cellH);

            Txt dayNum = txt(String.valueOf(i));
            dayNum.setTextSize(18);
            dayNum.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
            put(dayBox, dayNum, 0, 8, cellW, 64);

            Txt percentText = txt(percen);
            percentText.setTextSize(13);
            percentText.setTextAlignment(Txt.TEXT_ALIGNMENT_CENTER);
            put(dayBox, percentText, 0, 74, cellW, 52);

            setDayTap(dayBox, dayFormatted, selectDay, month[cal.getMonth() - 1], targetLiters);

            currentCell++;
        }

        if (activeDays > 0) {
            setTxt(summary, "ข้อมูลเดือน " + month[cal.getMonth() - 1] + " " + cal.getYear() +
                    "\nมีประวัติการดื่มน้ำรวม " + activeDays + " วัน" +
                    "\n(ปริมาณรวม " + WaterCalculate.L(totalMonthLiters) + ")\nคลิกที่วันที่เพื่อดูรายละเอียด");
        } else {
            setTxt(summary, "ข้อมูลเดือน " + month[cal.getMonth() - 1] + " " + cal.getYear() +
                    "\nไม่มีประวัติการดื่มน้ำ");
        }
    }

    void setDayTap(Box dayBox, String dayFormatted, int selectDay, String monthName, double targetLiters) {
        onTap(dayBox, () -> {
            List<String[]> selDrinks = data.getDrinks(dayFormatted);
            double totalDrank = data.sumLiters(dayFormatted);

            if (selDrinks.isEmpty()) {
                setTxt(detail, "วันที่ " + selectDay + " " + monthName
                        + "\nไม่มีประวัติการดื่มน้ำ");
            } else {
                int pct = (int) Math.round((totalDrank / targetLiters) * 100);
                int outside = 0;                                    // ดื่มนอกช่วงเวลาตื่น-นอน กี่ครั้ง
                for (int i = 0; i < selDrinks.size(); i++) {
                    if (isOutside(selDrinks.get(i))) outside++;
                }
                setTxt(detail, "วันที่ " + selectDay + " " + monthName
                        + "\nดื่มน้ำรวม " + WaterCalculate.L(totalDrank) + " / " + WaterCalculate.L(targetLiters) + " (" + pct + "%)"
                        + "\nดื่มน้ำ " + selDrinks.size() + " ครั้ง ในช่วง " + (selDrinks.size() - outside) + " ครั้ง และนอกช่วง " + outside + " ครั้ง");
            }
        });
    }
}
