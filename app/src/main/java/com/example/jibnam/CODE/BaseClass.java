package com.example.jibnam.CODE;

import android.content.Context;                     // ตัวแทนแอป
import android.content.Intent;                      // เปิดหน้าอื่น
import android.graphics.Color;                      // สี
import android.graphics.drawable.GradientDrawable;  // กรอบ
import android.os.Bundle;                           // ตอนเปิดหน้า
import android.text.InputType;                      // ช่องกรอกตัวเลข
import android.view.Gravity;                        // ตำแหน่งกลาง
import android.view.View;                           // ชิ้นส่วนทุกชนิด
import android.view.ViewGroup;                      // กล่องทุกชนิด
import android.widget.FrameLayout;                  // กล่องวางตาม x y
import android.widget.LinearLayout;                 // กล่องเรียงต่อกัน
import android.widget.ScrollView;                   // เลื่อนขึ้นลง
import androidx.appcompat.app.AppCompatActivity;    // หน้าจอ
import androidx.appcompat.widget.AppCompatButton;   // ปุ่ม
import androidx.appcompat.widget.AppCompatEditText; // ช่องกรอก
import androidx.appcompat.widget.AppCompatImageView;// รูป
import androidx.appcompat.widget.AppCompatTextView; // ตัวหนังสือ

// คลาสแม่ของทุกหน้า  หน่วยทุกอย่างเป็น px  WRAP = พอดีของข้างใน
public abstract class BaseClass extends AppCompatActivity {

    public static class Box extends FrameLayout { public Box(Context c) { super(c); } }              // กล่อง x y
    public static class Col extends LinearLayout { public Col(Context c) { super(c); setOrientation(VERTICAL); } }   // เรียงลง
    public static class Row extends LinearLayout { public Row(Context c) { super(c); } }            // เรียงขวา
    public static class Txt extends AppCompatTextView { public Txt(Context c) { super(c); } }       // ตัวหนังสือ
    public static class Btn extends AppCompatButton { public Btn(Context c) { super(c); } }         // ปุ่ม
    public static class Inp extends AppCompatEditText { public Inp(Context c) { super(c); } }       // ช่องกรอก
    public static class Img extends AppCompatImageView { public Img(Context c) { super(c); } }      // รูป

    public static final int WRAP = -2;   // พอดีของข้างใน
    Box pg;                              // กล่องทั้งหน้า

    protected abstract void build(Box pg);   // หน้าลูกเขียนตรงนี้

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView sv = new ScrollView(this);
        sv.setFitsSystemWindows(true);   // ไม่ทับแถบนาฬิกา
        sv.setFillViewport(true);        // หน้าสูงเท่าจอ (center ใช้ได้)
        pg = new Box(this);
        sv.addView(pg);
        build(pg);
        setContentView(sv);
    }

    // สร้าง
    protected Txt txt(String s) { Txt t = new Txt(this); t.setText(s); return t; }
    protected Btn btn(String s) { Btn t = new Btn(this); t.setText(s); return t; }
    protected Inp numInp() { Inp t = new Inp(this); t.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); return t; }
    protected Img img() { return new Img(this); }
    protected Box box() { return new Box(this); }
    protected Col col() { return new Col(this); }
    protected Row row() { return new Row(this); }

    // วาง
    protected void put(Box p, View v, int x, int y, int w, int h) {   // ห่างซ้าย x ห่างบน y
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.leftMargin = x;
        lp.topMargin = y;
        p.addView(v, lp);
    }
    protected void center(Box p, View v, int x, int y, int w, int h) {   // กลางจอ แล้วเลื่อน x y (ลบ = ซ้าย/ขึ้น)
        p.addView(v, new FrameLayout.LayoutParams(w, h, Gravity.CENTER));
        v.setTranslationX(x);
        v.setTranslationY(y);
    }
    protected void centerX(Box p, View v, int x, int y, int w, int h) {  // กลางแนวนอน เลื่อน x  y = ห่างบน
        p.addView(v, new FrameLayout.LayoutParams(w, h, Gravity.CENTER_HORIZONTAL));
        v.setTranslationX(x);
        v.setTranslationY(y);
    }
    protected void centerY(Box p, View v, int x, int y, int w, int h) {  // กลางแนวตั้ง เลื่อน y  x = ห่างซ้าย
        p.addView(v, new FrameLayout.LayoutParams(w, h, Gravity.CENTER_VERTICAL));
        v.setTranslationX(x);
        v.setTranslationY(y);
    }
    protected void add(LinearLayout p, View v, int w, int h) { p.addView(v, new LinearLayout.LayoutParams(w, h)); }   // ต่อท้ายใน Col / Row
    protected void center(LinearLayout p) { p.setGravity(Gravity.CENTER); }   // ของใน Col / Row อยู่กลาง
    protected void clr(ViewGroup p) { p.removeAllViews(); }                   // ล้างกล่อง
    protected void border(View v) {                                           // กรอบดำ พื้นขาว
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setStroke(3, Color.BLACK);
        v.setBackground(g);
    }
    protected void show(View v, boolean s) { v.setVisibility(s ? View.VISIBLE : View.INVISIBLE); }   // โชว์ / ซ่อน

    // กด / เวลา
    protected void onTap(View v, Runnable r) { v.setOnClickListener(x -> r.run()); }   // กดแล้วทำ r
    protected void later(Runnable r, long ms) { pg.postDelayed(r, ms); }              // ทำ r อีก ms (1000 = 1 วิ)
    protected void cancel(Runnable r) { pg.removeCallbacks(r); }                      // ยกเลิก later

    // หน้า
    protected void go(Class<?> c) { startActivity(new Intent(this, c)); }                                 // ไปหน้า c
    protected void goWith(Class<?> c, String k, String v) { startActivity(new Intent(this, c).putExtra(k, v)); }   // ไปพร้อมส่งค่า
    protected String arg(String k) { return getIntent().getStringExtra(k); }                             // รับค่า (ไม่มี = null)
}
