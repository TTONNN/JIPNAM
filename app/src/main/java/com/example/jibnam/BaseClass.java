package com.example.jibnam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/*
 * BaseClass : คลาสแม่ของทุกหน้าจอ เก็บโค้ด Android ไว้ที่นี่ที่เดียว
 * หน้าลูก extends BaseClass แล้วเขียน build() อย่างเดียว ไม่ต้อง import android
 * (เมธอดเป็น protected เพื่อให้หน้าในโฟลเดอร์ SCREEN เรียกใช้ได้)
 *
 * สร้าง : txt  btn  numInp  img  col  row  stack
 * วาง   : add  addW  addFill  addMid  mar  pad  alg  border  clr
 * ค่า   : setTxt  getTxt  setImg  show
 * กด    : onTap  later  cancel
 * หน้า  : go  goWith  arg
 */
public abstract class BaseClass extends AppCompatActivity {

    // ===== ชนิดชิ้นส่วน =====
    public static class Box extends LinearLayout {          // กล่องเรียงของ (บนลงล่าง หรือ ซ้ายไปขวา)
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
    public static class Stack extends FrameLayout {         // กล่องวางของซ้อนกัน (ตัวหนังสือทับรูป)
        public Stack(Context c) { super(c); }
    }

    // ===== ค่าคงที่ =====
    public static final int FILL = ViewGroup.LayoutParams.MATCH_PARENT;  // กว้าง/สูงเต็มกล่องแม่
    public static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;  // กว้าง/สูงพอดีของข้างใน

    public static final int A_C = Gravity.CENTER;                          // จัดกลาง
    public static final int A_CH = Gravity.CENTER_HORIZONTAL;              // จัดกลางแนวนอน
    public static final int A_CV = Gravity.CENTER_VERTICAL;                // จัดกลางแนวตั้ง
    public static final int A_R = Gravity.END | Gravity.CENTER_VERTICAL;   // ชิดขวา

    private final Handler hdl = new Handler(Looper.getMainLooper());      // ตัวจับเวลา ใช้กับ later/cancel

    protected abstract void build(Box pg);   // หน้าลูกต้องเขียน: สร้างหน้าจอลงใน pg

    @Override
    protected void onCreate(Bundle b) {          // เปิดหน้า: ทำหน้าเลื่อนขึ้นลงได้ แล้วให้หน้าลูก build
        super.onCreate(b);
        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        sv.setFitsSystemWindows(true);           // ไม่ให้เนื้อหาไปซ้อนใต้แถบนาฬิกาด้านบน
        Box pg = col();
        sv.addView(pg);
        build(pg);
        setContentView(sv);
    }

    @Override
    protected void onDestroy() {                 // ปิดหน้า: ยกเลิกตัวจับเวลาที่ค้าง
        hdl.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    // ===== สร้างชิ้นส่วน =====

    protected Txt txt(String s) {                          // ตัวหนังสือ ข้อความ s
        Txt t = new Txt(this);
        t.setText(s);
        return t;
    }

    protected Btn btn(String s) {                          // ปุ่ม ข้อความ s
        Btn b = new Btn(this);
        b.setText(s);
        return b;
    }

    protected Inp numInp() {                               // ช่องกรอกตัวเลข (มีทศนิยมได้)
        Inp e = new Inp(this);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return e;
    }

    protected Img img() {                                  // ช่องรูป
        return new Img(this);
    }

    protected Box col() {                                  // กล่องเรียงบนลงล่าง
        Box b = new Box(this);
        b.setOrientation(LinearLayout.VERTICAL);
        return b;
    }

    protected Box row() {                                  // กล่องเรียงซ้ายไปขวา
        Box b = new Box(this);
        b.setOrientation(LinearLayout.HORIZONTAL);
        return b;
    }

    protected Stack stack() {                              // กล่องวางของซ้อนกัน
        return new Stack(this);
    }

    // ===== วางของ =====

    protected void add(Box p, View v) {                    // ใส่ v ลงกล่อง p ขนาดอัตโนมัติ
        add(p, v, p.getOrientation() == LinearLayout.VERTICAL ? FILL : WRAP, WRAP);
    }

    protected void add(Box p, View v, int w, int h) {      // ใส่ v ลงกล่อง p กว้าง w สูง h (dp, FILL หรือ WRAP)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz(w), sz(h));
        if (p.getOrientation() == LinearLayout.VERTICAL) lp.gravity = A_CH;
        p.addView(v, lp);
    }

    protected void addW(Box p, View v, float wt) {         // ใส่ v ลงกล่อง p แบ่งที่ว่างตามสัดส่วน wt
        boolean vt = p.getOrientation() == LinearLayout.VERTICAL;
        p.addView(v, new LinearLayout.LayoutParams(vt ? FILL : 0, vt ? 0 : WRAP, wt));
    }

    protected void addFill(Stack s, View v) {              // วาง v เต็มกล่องซ้อน s
        s.addView(v, new FrameLayout.LayoutParams(FILL, FILL));
    }

    protected void addMid(Stack s, View v) {               // วาง v กลางกล่องซ้อน s (ทับของที่ใส่ก่อน)
        s.addView(v, new FrameLayout.LayoutParams(WRAP, WRAP, Gravity.CENTER));
    }

    protected void mar(View v, int l, int t, int r, int b) {   // ระยะห่างด้านนอก ซ้าย บน ขวา ล่าง (dp) เรียกหลัง add
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        lp.setMargins(dp(l), dp(t), dp(r), dp(b));
        v.setLayoutParams(lp);
    }

    protected void pad(View v, int l, int t, int r, int b) {   // ระยะห่างด้านใน ซ้าย บน ขวา ล่าง (dp)
        v.setPadding(dp(l), dp(t), dp(r), dp(b));
    }

    protected void alg(View v, int a) {                    // จัดตำแหน่งของข้างใน (ใช้ค่า A_xx)
        if (v instanceof TextView) ((TextView) v).setGravity(a);
        else if (v instanceof LinearLayout) ((LinearLayout) v).setGravity(a);
    }

    protected void border(View v) {                        // กรอบเส้นดำบาง 1dp พื้นขาว
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setStroke(dp(1), Color.BLACK);
        v.setBackground(g);
    }

    protected void clr(Box b) {                            // ลบของในกล่องทิ้งทั้งหมด (ไว้วาดใหม่)
        b.removeAllViews();
    }

    // ===== ค่า =====

    protected void setTxt(TextView t, String s) {          // เปลี่ยนข้อความ (ใช้กับ Txt และ Inp)
        t.setText(s);
        if (t instanceof EditText) ((EditText) t).setSelection(s.length());   // ช่องกรอก: เคอร์เซอร์ไปท้ายสุด
    }

    protected String getTxt(TextView t) {                  // อ่านข้อความ (ตัดช่องว่างหัวท้ายแล้ว)
        return t.getText() == null ? "" : t.getText().toString().trim();
    }

    protected void setImg(Img i, int id) {                 // เปลี่ยนรูป เช่น R.drawable.bottle_0
        i.setImageResource(id);
    }

    protected void show(View v, boolean s) {               // โชว์ / ซ่อน (ซ่อนแต่ยังกันที่ไว้)
        v.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
    }

    // ===== กด / เวลา =====

    protected void onTap(View v, Runnable r) {             // กด v แล้วทำ r  เช่น onTap(b, () -> go(Screen_3.class))
        v.setOnClickListener(x -> r.run());
    }

    protected void later(Runnable r, long ms) {            // ทำ r หลังผ่านไป ms มิลลิวินาที (1000 = 1 วิ)
        hdl.postDelayed(r, ms);
    }

    protected void cancel(Runnable r) {                    // ยกเลิก r ที่ตั้งเวลาไว้
        hdl.removeCallbacks(r);
    }

    // ===== เปลี่ยนหน้า =====

    protected void go(Class<?> c) {                        // เปิดหน้าอื่น เช่น go(Screen_3.class)
        startActivity(new Intent(this, c));
    }

    protected void goWith(Class<?> c, String k, String v) {   // เปิดหน้าอื่นพร้อมส่งค่า k = ชื่อ, v = ค่า
        startActivity(new Intent(this, c).putExtra(k, v));
    }

    protected String arg(String k) {             // อ่านค่าที่หน้าก่อนส่งมากับ goWith (ไม่มี = null)
        return getIntent().getStringExtra(k);
    }

    // ===== ภายใน =====

    protected int dp(int v) {                              // แปลง dp เป็นพิกเซลของจอนี้
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int sz(int v) {                      // ขนาด: FILL/WRAP ใช้ตรงๆ, ตัวเลขแปลงเป็น dp
        return v < 0 ? v : dp(v);
    }
}
