
package com.example.jibnam.SCREEN;
import android.graphics.Color;
import com.example.jibnam.BaseClass;
import com.example.jibnam.CalendarMonth;
import com.example.jibnam.DATA.Data;
import com.example.jibnam.DATA.DataApi;

import java.util.Calendar;

public class Screen_4 extends BaseClass {

    CalendarMonth cal ;
    Data data ;
    Txt title ;
    Box calenderBox ;

    Txt detail ;

    @Override
    protected void build(Box pg)
    {
        Calendar today = Calendar.getInstance() ;
        int month = today.get(Calendar.MONTH) + 1 ;
        int year = today.get(Calendar.YEAR) ;

         cal = new CalendarMonth(month, year) ; // แสดงที่ปัจจุบันเสมอ
         data = new Data(DataApi.realDataFile(this)) ; //อ่านไฟล์

        title = txt(""); //เอาไว้แสดงเดือนและปี
        title.setTextSize(20) ;
        add(pg, title) ;

        Box botton = row() ; //กล่องแนวนอน
        Btn prev = btn("<"); // มาจากคลาสเบส
        Btn next = btn(">");
        addW(botton, prev , 1f);
        addW(botton, next , 1f);
        add(pg, botton);

        Btn back = btn("ย้อนกลับ");
        add(pg, back) ;
        onTap(back, () -> go(Screen_2.class));
        onTap(prev, () -> {
            cal.previousMonth();
            updateCalendar();
        });

    }

    void updateCalendar() {

    }
}
