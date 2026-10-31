package com.example.jibnam.CODE;

import android.content.Context;                          // ตัวแทนแอป ต้องส่งให้ตอนสร้างชิ้นส่วนหน้าจอ
import android.content.Intent;                           // ใบสั่งเปิดหน้าอื่น (go / goWith / arg)
import android.graphics.Color;                           // สี เช่น Color.BLACK
import android.graphics.drawable.GradientDrawable;       // พื้นหลังที่มีเส้นขอบ (border)
import android.os.Bundle;                                // ข้อมูลตอนเปิดหน้า (onCreate ต้องรับ)
import android.os.Handler;                               // ตั้งเวลาให้ทำทีหลัง (later / cancel)
import android.os.Looper;                                // บอก Handler ให้ทำงานบนหน้าจอหลัก
import android.text.InputType;                           // ชนิดช่องกรอก (ตัวเลข)
import android.view.View;                                // ชิ้นส่วนหน้าจอทุกชนิด
import android.widget.FrameLayout;                       // กล่องที่วางของตามพิกัด x, y
import android.widget.ScrollView;                        // ทำให้หน้าเลื่อนขึ้นลงได้
import android.widget.TextView;                          // ตัวหนังสือ (แม่ของ Txt Btn Inp)

import androidx.appcompat.app.AppCompatActivity;         // หน้าจอ 1 หน้า
import androidx.appcompat.widget.AppCompatButton;        // ปุ่ม
import androidx.appcompat.widget.AppCompatEditText;      // ช่องกรอก
import androidx.appcompat.widget.AppCompatImageView;     // รูป
import androidx.appcompat.widget.AppCompatTextView;      // ตัวหนังสือ

/*
 * BaseClass : คลาสแม่ของทุกหน้าจอ เก็บโค้ด Android ไว้ที่นี่ที่เดียว
 * หน้าลูก extends BaseClass แล้วเขียน build() อย่างเดียว
 *
 * วางของแบบนับพิกัด : put(กล่อง, ของ, x, y, กว้าง, สูง)
 *   จอกว้าง 360 หน่วยเสมอ (ซ้ายสุด 0 ขวาสุด 360) จอใหญ่/เล็ก ยืดหด ให้เต็มจอเอง
 *   x = ห่างจากซ้าย  y = ห่างจากบน  กว้าง/สูง ใส่ WRAP = พอดีของข้างใน
 *
 * สร้าง : txt  btn  numInp  img  box
 * วาง   : put  clr  border
 * ค่า   : setTxt  getTxt  setImg  show
 * กด    : onTap  later  cancel
 * หน้า  : go  goWith  arg
 */
public abstract class BaseClass extends AppCompatActivity {

    // ===== ชนิดชิ้นส่วน =====
    public static class Box extends FrameLayout {           // กล่อง วางของตามพิกัด
        public Box(Context c) { super(c); }
    }
    public static class Txt extends AppCompatTextView {     // ตัวหนังสือ
        public Txt(Context c) { super(c); }
    }
    public static class Btn extends AppCompatButton {       // ปุ่ม
        public Btn(Context c) { super(c); }
    }
    public static class Inp extends AppCompatEditText {     // ช่องกรอก
        public Inp(Context c) { super(c); }
    }
    public static class Img extends AppCompatImageView {    // รูป
        public Img(Context c) { super(c); }
    }

    public static final int WRAP = FrameLayout.LayoutParams.WRAP_CONTENT;   // ขนาดพอดีของข้างใน

    private final Handler hdl = new Handler(Looper.getMainLooper());      // ตัวจับเวลา

    protected abstract void build(Box pg);   // หน้าลูกต้องเขียน: วางของลงใน pg

    @Override
    protected void onCreate(Bundle b) {          // เปิดหน้า: กล่อง pg อยู่ในหน้าที่เลื่อนได้
        super.onCreate(b);
        ScrollView sv = new ScrollView(this);
        sv.setFitsSystemWindows(true);           // ไม่ให้ทับแถบนาฬิกาด้านบน
        Box pg = new Box(this);
        sv.addView(pg);
        build(pg);
        setContentView(sv);
    }

    @Override
    protected void onDestroy() {                 // ปิดหน้า: ยกเลิกตัวจับเวลาที่ค้าง
        hdl.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    // ===== สร้าง =====

    protected Txt txt(String s) {                // ตัวหนังสือ ข้อความ s
        Txt t = new Txt(this);
        t.setText(s);
        return t;
    }

    protected Btn btn(String s) {                // ปุ่ม ข้อความ s
        Btn b = new Btn(this);
        b.setText(s);
        return b;
    }

    protected Inp numInp() {                     // ช่องกรอกตัวเลข (มีทศนิยมได้)
        Inp e = new Inp(this);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return e;
    }

    protected Img img() {                        // ช่องรูป
        return new Img(this);
    }

    protected Box box() {                        // กล่องเปล่า (ไว้รวมของหลายชิ้น)
        return new Box(this);
    }

    // ===== วาง =====

    protected void put(Box p, View v, int x, int y, int w, int h) {   // วาง v ในกล่อง p ที่ (x, y) ขนาด w × h
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(sz(w), sz(h));
        lp.leftMargin = dp(x);
        lp.topMargin = dp(y);
        p.addView(v, lp);
    }

    protected void clr(Box b) {                  // ลบของในกล่องทิ้งทั้งหมด (ไว้วาดใหม่)
        b.removeAllViews();
    }

    protected void border(View v) {              // กรอบเส้นดำบาง พื้นขาว
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setStroke(dp(1), Color.BLACK);
        v.setBackground(g);
    }

    // ===== ค่า =====

    protected void setTxt(TextView t, String s) {   // เปลี่ยนข้อความ (ใช้กับ Txt Btn Inp)
        t.setText(s);
    }

    protected String getTxt(TextView t) {        // อ่านข้อความ (ตัดช่องว่างหัวท้าย)
        return t.getText().toString().trim();
    }

    protected void setImg(Img i, int id) {       // เปลี่ยนรูป เช่น R.drawable.bottle_0
        i.setImageResource(id);
    }

    protected void show(View v, boolean s) {     // โชว์ / ซ่อน
        v.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
    }

    // ===== กด / เวลา =====

    protected void onTap(View v, Runnable r) {   // กด v แล้วทำ r  เช่น onTap(b, () -> go(Screen_3.class))
        v.setOnClickListener(x -> r.run());
    }

    protected void later(Runnable r, long ms) {  // ทำ r หลังผ่านไป ms มิลลิวินาที (1000 = 1 วิ)
        hdl.postDelayed(r, ms);
    }

    protected void cancel(Runnable r) {          // ยกเลิก r ที่ตั้งเวลาไว้
        hdl.removeCallbacks(r);
    }

    // ===== หน้า =====

    protected void go(Class<?> c) {              // เปิดหน้าอื่น เช่น go(Screen_3.class)
        startActivity(new Intent(this, c));
    }

    protected void goWith(Class<?> c, String k, String v) {   // เปิดหน้าอื่นพร้อมส่งค่า k = ชื่อ, v = ค่า
        startActivity(new Intent(this, c).putExtra(k, v));
    }

    protected String arg(String k) {             // อ่านค่าที่หน้าก่อนส่งมากับ goWith (ไม่มี = null)
        return getIntent().getStringExtra(k);
    }

    // ===== ภายใน =====

    private int dp(int v) {                      // แปลงหน่วย เป็น pixel ของจอนี้ (จอกว้าง = 360 หน่วย)
        return v * getResources().getDisplayMetrics().widthPixels / 360;
    }

    private int sz(int v) {                      // WRAP ใช้ตรงๆ, ตัวเลขแปลงเป็น pixel
        return v == WRAP ? v : dp(v);
    }
}
