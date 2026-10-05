package com.example.jibnam;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/*
 * BaseClass : คลาสแม่ของทุกหน้าจอ — Android API ทั้งหมดอยู่ที่ไฟล์นี้ไฟล์เดียว
 *
 * วิธีใช้ (หน้าลูกไม่ต้อง import android อะไรเลย):
 *
 *   public class Screen_X extends BaseClass {
 *       @Override
 *       void build(Box pg) {                       // pg = ทั้งหน้า (เลื่อนขึ้นลงได้แล้ว)
 *           Txt t = txt("สวัสดี", 20, C_BLK, true);
 *           add(pg, t);
 *           onTap(t, () -> setTxt(t, "กดแล้ว"));
 *       }
 *   }
 *   แล้วลงทะเบียนใน AndroidManifest.xml:  <activity android:name=".Screen_X" />
 *
 * ขาดอะไร → มาเพิ่มเมธอดที่ไฟล์นี้ อย่าไป import android ในหน้าลูก
 */
public abstract class BaseClass extends AppCompatActivity {

    // ===== ชนิดชิ้นส่วน (หน้าลูกเรียกชื่อสั้นได้เลย ไม่ต้อง import) =====
    public static class Box extends LinearLayout {          // กล่องเรียงของ (แนวตั้ง/นอน)
        public Box(Context c) { super(c); }
    }
    public static class Txt extends AppCompatTextView {     // ตัวหนังสือ / ปุ่ม
        public Txt(Context c) { super(c); }
    }
    public static class Inp extends AppCompatEditText {     // ช่องกรอก
        public Inp(Context c) { super(c); }
    }
    public static class Img extends AppCompatImageView {    // รูป
        public Img(Context c) { super(c); }
    }

    // ===== ค่าคงที่ =====
    public static final int FILL = ViewGroup.LayoutParams.MATCH_PARENT;  // ขนาด: เต็มกล่องแม่
    public static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;  // ขนาด: เท่าเนื้อหา

    public static final int A_C = Gravity.CENTER;                            // จัด: กลาง
    public static final int A_CH = Gravity.CENTER_HORIZONTAL;                // จัด: กลางแนวนอน
    public static final int A_CV = Gravity.CENTER_VERTICAL;                  // จัด: กลางแนวตั้ง
    public static final int A_L = Gravity.START | Gravity.CENTER_VERTICAL;   // จัด: ชิดซ้าย
    public static final int A_R = Gravity.END | Gravity.CENTER_VERTICAL;     // จัด: ชิดขวา
    public static final int A_TR = Gravity.END | Gravity.TOP;                // จัด: ขวาบน

    public static final int C_BLK = Color.BLACK;               // สี
    public static final int C_WHT = Color.WHITE;
    public static final int C_DGY = Color.DKGRAY;
    public static final int C_BG = Color.rgb(227, 236, 238);   // พื้นหลังหน้า
    public static final int C_BLU = Color.rgb(74, 144, 226);
    public static final int C_GRN = Color.rgb(110, 207, 90);
    public static final int C_LBL = Color.rgb(190, 225, 240);  // ฟ้าอ่อน

    private final Handler hdl = new Handler(Looper.getMainLooper());

    // ===== หน้าลูกต้องเขียนเมธอดนี้: สร้างหน้าจอลงใน pg =====
    abstract void build(Box pg);

    @Override
    protected void onCreate(Bundle b) {          // ระบบเรียกตอนเปิดหน้า: สร้างหน้าเลื่อนได้ แล้วให้ลูก build
        super.onCreate(b);
        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        sv.setBackgroundColor(C_BG);
        Box pg = col();
        pad(pg, 20, 16, 20, 24);
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

    /** ตัวหนังสือ (ใช้เป็นปุ่มด้วย) — s ข้อความ, sp ขนาด, c สี, bd ตัวหนา */
    Txt txt(String s, int sp, int c, boolean bd) {
        Txt t = new Txt(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(c);
        if (bd) t.setTypeface(null, Typeface.BOLD);
        t.setGravity(A_C);
        return t;
    }

    /** ช่องกรอกตัวเลข (มีทศนิยมได้) ตัวหนา ชิดขวา ไม่มีเส้นใต้ */
    Inp numInp(int sp) {
        Inp e = new Inp(this);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setTextSize(sp);
        e.setTextColor(C_BLK);
        e.setTypeface(null, Typeface.BOLD);
        e.setBackground(null);
        e.setPadding(0, 0, 0, 0);
        e.setGravity(A_R);
        return e;
    }

    /** ช่องรูป (ย่อให้พอดีกรอบ ไม่บิด) */
    Img img() {
        Img i = new Img(this);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return i;
    }

    /** กล่องเรียงบนลงล่าง */
    Box col() {
        Box b = new Box(this);
        b.setOrientation(LinearLayout.VERTICAL);
        return b;
    }

    /** กล่องเรียงซ้ายไปขวา */
    Box row() {
        Box b = new Box(this);
        b.setOrientation(LinearLayout.HORIZONTAL);
        return b;
    }

    // ===== ใส่ลงกล่อง / ขนาด / ระยะ =====

    /** ใส่ v ลงกล่อง p (ขนาดอัตโนมัติ) */
    void add(Box p, View v) {
        add(p, v, p.getOrientation() == LinearLayout.VERTICAL ? FILL : WRAP, WRAP);
    }

    /** ใส่ v ลงกล่อง p กว้าง w สูง h (หน่วย dp หรือ FILL / WRAP) */
    void add(Box p, View v, int w, int h) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz(w), sz(h));
        if (p.getOrientation() == LinearLayout.VERTICAL) lp.gravity = A_CH;
        p.addView(v, lp);
    }

    /** ใส่ v ลงกล่อง p แบบแบ่งที่ว่างตามสัดส่วน wt (เช่น 1f, 1.2f) */
    void addW(Box p, View v, float wt) {
        boolean vt = p.getOrientation() == LinearLayout.VERTICAL;
        p.addView(v, new LinearLayout.LayoutParams(vt ? FILL : 0, vt ? 0 : WRAP, wt));
    }

    /** ระยะห่างด้านนอก (dp) — เรียกหลัง add เท่านั้น */
    void mar(View v, int l, int t, int r, int b) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        lp.setMargins(dp(l), dp(t), dp(r), dp(b));
        v.setLayoutParams(lp);
    }

    /** ระยะห่างด้านใน (dp) */
    void pad(View v, int l, int t, int r, int b) {
        v.setPadding(dp(l), dp(t), dp(r), dp(b));
    }

    /** จัดตำแหน่งของข้างใน (ใช้ค่า A_xx) */
    void alg(View v, int a) {
        if (v instanceof TextView) ((TextView) v).setGravity(a);
        else if (v instanceof LinearLayout) ((LinearLayout) v).setGravity(a);
    }

    /** ลบของในกล่องทิ้งทั้งหมด (ไว้วาดใหม่) */
    void clr(Box b) {
        b.removeAllViews();
    }

    // ===== หน้าตา =====

    /** พื้นหลังกล่องมุมโค้ง — fl สีพื้น, rd ความโค้ง dp, sw ขอบหนา dp (0 = ไม่มีขอบ), sc สีขอบ */
    void bg(View v, int fl, int rd, int sw, int sc) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fl);
        g.setCornerRadius(dp(rd));
        if (sw > 0) g.setStroke(dp(sw), sc);
        v.setBackground(g);
    }

    /** สร้างสีเอง (แดง, เขียว, น้ำเงิน 0-255) */
    int rgb(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }

    // ===== ค่า / การแสดงผล =====

    /** เปลี่ยนข้อความ (ใช้ได้ทั้ง Txt และ Inp) */
    void setTxt(TextView t, String s) {
        t.setText(s);
        if (t instanceof EditText) ((EditText) t).setSelection(s.length());
    }

    /** อ่านข้อความ (เช่น ที่พิมพ์ในช่องกรอก) */
    String getTxt(TextView t) {
        return t.getText() == null ? "" : t.getText().toString();
    }

    /** เปลี่ยนรูป — id เช่น R.drawable.bottle_0 */
    void setImg(Img i, int id) {
        i.setImageResource(id);
    }

    /** โชว์ / ซ่อน (ซ่อนแต่ยังกันที่ไว้ หน้าไม่กระตุก) */
    void show(View v, boolean s) {
        v.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
    }

    // ===== การกด / เวลา / เปลี่ยนหน้า =====

    /** กด v แล้วทำ r — เช่น onTap(btn, () -> ... ) */
    void onTap(View v, Runnable r) {
        v.setOnClickListener(x -> r.run());
    }

    /** ทำ r หลังผ่านไป ms มิลลิวินาที (1000 = 1 วิ) */
    void later(Runnable r, long ms) {
        hdl.postDelayed(r, ms);
    }

    /** ยกเลิก r ที่ตั้งเวลาไว้ */
    void cancel(Runnable r) {
        hdl.removeCallbacks(r);
    }

    /** เปิดหน้าอื่น — เช่น go(Screen_3.class) */
    void go(Class<?> c) {
        startActivity(new Intent(this, c));
    }

    /** แปลง dp → พิกเซล (ใช้ภายใน) */
    int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int sz(int v) {
        return v < 0 ? v : dp(v);
    }
}
