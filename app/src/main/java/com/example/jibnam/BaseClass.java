package com.example.jibnam;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;

/*
 * BaseClass : คลาสแม่ของทุกหน้าจอ — Android API ทั้งหมดอยู่ที่ไฟล์นี้ไฟล์เดียว
 * หน้าลูกไม่ต้อง import android อะไรเลย แค่ extends BaseClass แล้วเขียน build()
 *
 *   public class Screen_X extends BaseClass {
 *       @Override
 *       void build(Box pg) {                     // pg = ทั้งหน้า (เลื่อนขึ้นลงได้แล้ว)
 *           Txt t = txt("สวัสดี", 20, C_BLK, true);
 *           add(pg, t);
 *           Txt b = btn("กด", C_BLU, C_WHT);
 *           add(pg, b, FILL, 48);
 *           onTap(b, () -> msg("กดแล้ว"));
 *       }
 *   }
 *   หน้าใหม่ต้องลงทะเบียนใน AndroidManifest.xml:  <activity android:name=".Screen_X" />
 *
 * ---------------- สารบัญ ----------------
 * สร้าง      : txt  btn  numInp  txtInp  img  chk  col  row  space
 * วาง        : add  addW  mar  pad  alg  clr
 * หน้าตา     : bg  grad  rgb  + สี C_xxx, จัดตำแหน่ง A_xxx, ขนาด FILL / WRAP
 * ค่า        : setTxt  getTxt  getNum  setImg  show  isOn
 * กด / เวลา  : onTap  later  cancel
 * หน้า       : go  goWith  arg  back
 * เด้ง       : msg  ask  pickTime
 * มือถือ     : vibrate  noti
 * เก็บข้อมูล : save  load  saveNum  loadNum   (ปิดแอปแล้วยังอยู่)
 *
 * ขาดอะไร → มาเพิ่มเมธอดที่ไฟล์นี้ อย่าไป import android ในหน้าลูก
 */
public abstract class BaseClass extends AppCompatActivity {

    // ===== ชนิดชิ้นส่วน (หน้าลูกเรียกชื่อสั้นได้เลย) =====
    public static class Box extends LinearLayout {          // กล่องเรียงของ
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
    public static class Chk extends AppCompatCheckBox {     // ช่องติ๊ก
        public Chk(Context c) { super(c); }
    }

    /** รับเวลาที่เลือก (ใช้กับ pickTime) */
    public interface OnTime { void set(int h, int m); }

    // ===== ค่าคงที่ =====
    public static final int FILL = ViewGroup.LayoutParams.MATCH_PARENT;  // เต็มกล่องแม่
    public static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;  // เท่าเนื้อหา

    public static final int A_C = Gravity.CENTER;                            // กลาง
    public static final int A_CH = Gravity.CENTER_HORIZONTAL;                // กลางแนวนอน
    public static final int A_CV = Gravity.CENTER_VERTICAL;                  // กลางแนวตั้ง
    public static final int A_L = Gravity.START | Gravity.CENTER_VERTICAL;   // ชิดซ้าย
    public static final int A_R = Gravity.END | Gravity.CENTER_VERTICAL;     // ชิดขวา
    public static final int A_TR = Gravity.END | Gravity.TOP;                // ขวาบน

    public static final int C_BLK = Color.BLACK;
    public static final int C_WHT = Color.WHITE;
    public static final int C_DGY = Color.DKGRAY;
    public static final int C_GRY = Color.LTGRAY;
    public static final int C_BG = Color.rgb(227, 236, 238);   // พื้นหลังหน้า
    public static final int C_BLU = Color.rgb(74, 144, 226);
    public static final int C_LBL = Color.rgb(190, 225, 240);  // ฟ้าอ่อน
    public static final int C_GRN = Color.rgb(110, 207, 90);
    public static final int C_YEL = Color.rgb(245, 200, 60);
    public static final int C_RED = Color.rgb(230, 80, 70);

    private final Handler hdl = new Handler(Looper.getMainLooper());

    // ===== หน้าลูกต้องเขียนเมธอดนี้ =====
    abstract void build(Box pg);

    @Override
    protected void onCreate(Bundle b) {          // เปิดหน้า: สร้างหน้าเลื่อนได้ แล้วให้ลูก build
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

    /** ตัวหนังสือ — s ข้อความ, sp ขนาด, c สี, bd ตัวหนา */
    Txt txt(String s, int sp, int c, boolean bd) {
        Txt t = new Txt(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(c);
        if (bd) t.setTypeface(null, Typeface.BOLD);
        t.setGravity(A_C);
        return t;
    }

    /** ปุ่มสำเร็จรูป มุมโค้ง — s ข้อความ, fl สีปุ่ม, tc สีตัวอักษร */
    Txt btn(String s, int fl, int tc) {
        Txt t = txt(s, 16, tc, true);
        bg(t, fl, 24, 0, 0);
        pad(t, 16, 10, 16, 10);
        return t;
    }

    /** ช่องกรอกตัวเลข (ทศนิยมได้) */
    Inp numInp(int sp) {
        Inp e = inp(sp);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return e;
    }

    /** ช่องกรอกข้อความ — hint = ข้อความจางๆ ตอนยังว่าง */
    Inp txtInp(int sp, String hint) {
        Inp e = inp(sp);
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        e.setHint(hint);
        e.setGravity(A_L);
        return e;
    }

    /** ช่องรูป (ย่อพอดีกรอบ ไม่บิด) */
    Img img() {
        Img i = new Img(this);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return i;
    }

    /** ช่องติ๊ก ✓ พร้อมข้อความ */
    Chk chk(String s) {
        Chk c = new Chk(this);
        c.setText(s);
        return c;
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

    /** ที่ว่างเปล่า สูง/กว้าง d (dp) ใส่คั่นระหว่างของ */
    void space(Box p, int d) {
        add(p, new View(this), d, d);
    }

    // ===== วาง / ขนาด / ระยะ =====

    /** ใส่ v ลงกล่อง p (ขนาดอัตโนมัติ) */
    void add(Box p, View v) {
        add(p, v, p.getOrientation() == LinearLayout.VERTICAL ? FILL : WRAP, WRAP);
    }

    /** ใส่ v ลงกล่อง p กว้าง w สูง h (dp หรือ FILL / WRAP) */
    void add(Box p, View v, int w, int h) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sz(w), sz(h));
        if (p.getOrientation() == LinearLayout.VERTICAL) lp.gravity = A_CH;
        p.addView(v, lp);
    }

    /** ใส่ v ลงกล่อง p แบ่งที่ว่างตามสัดส่วน wt (เช่น 1f, 1.2f) */
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

    /** จัดตำแหน่งของข้างใน (ค่า A_xx) */
    void alg(View v, int a) {
        if (v instanceof TextView) ((TextView) v).setGravity(a);
        else if (v instanceof LinearLayout) ((LinearLayout) v).setGravity(a);
    }

    /** ลบของในกล่องทิ้งทั้งหมด (ไว้วาดใหม่) */
    void clr(Box b) {
        b.removeAllViews();
    }

    // ===== หน้าตา =====

    /** พื้นหลังมุมโค้ง — fl สีพื้น, rd ความโค้ง, sw ขอบหนา (0 = ไม่มี), sc สีขอบ */
    void bg(View v, int fl, int rd, int sw, int sc) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fl);
        g.setCornerRadius(dp(rd));
        if (sw > 0) g.setStroke(dp(sw), sc);
        v.setBackground(g);
    }

    /** พื้นหลังไล่สีบน→ล่าง c1 → c2 มุมโค้ง rd (ใช้กับ pg ทั้งหน้าก็ได้) */
    void grad(View v, int c1, int c2, int rd) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{c1, c2});
        g.setCornerRadius(dp(rd));
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

    /** อ่านข้อความ */
    String getTxt(TextView t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }

    /** อ่านเป็นตัวเลข — ว่าง/ไม่ใช่ตัวเลข ได้ def */
    double getNum(TextView t, double def) {
        try {
            return Double.parseDouble(getTxt(t));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /** เปลี่ยนรูป — id เช่น R.drawable.bottle_0 */
    void setImg(Img i, int id) {
        i.setImageResource(id);
    }

    /** โชว์ / ซ่อน (ซ่อนแต่ยังกันที่ไว้) */
    void show(View v, boolean s) {
        v.setVisibility(s ? View.VISIBLE : View.INVISIBLE);
    }

    /** ช่องติ๊กถูกติ๊กอยู่ไหม */
    boolean isOn(Chk c) {
        return c.isChecked();
    }

    // ===== กด / เวลา =====

    /** กด v แล้วทำ r — onTap(btn, () -> ... ) */
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

    // ===== เปลี่ยนหน้า =====

    /** เปิดหน้าอื่น — go(Screen_3.class) */
    void go(Class<?> c) {
        startActivity(new Intent(this, c));
    }

    /** เปิดหน้าอื่นพร้อมส่งค่า — goWith(Screen_3.class, "wt", "60") */
    void goWith(Class<?> c, String k, String v) {
        startActivity(new Intent(this, c).putExtra(k, v));
    }

    /** หน้าที่ถูกเปิด อ่านค่าที่ส่งมา — arg("wt", "0") */
    String arg(String k, String def) {
        String v = getIntent().getStringExtra(k);
        return v == null ? def : v;
    }

    /** ปิดหน้านี้ กลับหน้าก่อน */
    void back() {
        finish();
    }

    // ===== เด้ง =====

    /** ข้อความเด้งสั้นๆ ด้านล่างจอ */
    void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /** กล่องถาม ตกลง/ยกเลิก — กดตกลงแล้วทำ yes */
    void ask(String s, Runnable yes) {
        new AlertDialog.Builder(this)
                .setMessage(s)
                .setPositiveButton("ตกลง", (d, w) -> yes.run())
                .setNegativeButton("ยกเลิก", null)
                .show();
    }

    /** หน้าต่างเลือกเวลา — pickTime(7, 30, (h, m) -> ... ) */
    void pickTime(int h, int m, OnTime r) {
        new TimePickerDialog(this, (tp, hh, mm) -> r.set(hh, mm), h, m, true).show();
    }

    // ===== ฟีเจอร์มือถือ =====

    /** สั่น ms มิลลิวินาที */
    void vibrate(int ms) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) return;
        if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        else v.vibrate(ms);
    }

    /** แจ้งเตือนขึ้นแถบด้านบน (ครั้งแรกจะถามสิทธิ์ก่อน แล้วค่อยเรียกใหม่) */
    void noti(String title, String s) {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(new NotificationChannel("jibnam", "JIBNAM", NotificationManager.IMPORTANCE_DEFAULT));
        }
        nm.notify((int) (System.currentTimeMillis() % 100000), new NotificationCompat.Builder(this, "jibnam")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(s)
                .setAutoCancel(true)
                .build());
    }

    // ===== เก็บข้อมูลลงเครื่อง (ปิดแอปแล้วยังอยู่) =====

    private SharedPreferences sp() {
        return getSharedPreferences("jibnam", MODE_PRIVATE);
    }

    /** เก็บข้อความ — save("name", "ton") */
    void save(String k, String v) {
        sp().edit().putString(k, v).apply();
    }

    /** อ่านข้อความที่เก็บ — ไม่มีได้ def */
    String load(String k, String def) {
        return sp().getString(k, def);
    }

    /** เก็บตัวเลข — saveNum("wt", 60) */
    void saveNum(String k, double v) {
        save(k, String.valueOf(v));
    }

    /** อ่านตัวเลขที่เก็บ — ไม่มีได้ def */
    double loadNum(String k, double def) {
        try {
            return Double.parseDouble(load(k, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    // ===== ภายใน =====

    private Inp inp(int sp) {
        Inp e = new Inp(this);
        e.setTextSize(sp);
        e.setTextColor(C_BLK);
        e.setTypeface(null, Typeface.BOLD);
        e.setBackground(null);
        e.setPadding(0, 0, 0, 0);
        e.setGravity(A_R);
        return e;
    }

    /** แปลง dp → พิกเซล */
    int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int sz(int v) {
        return v < 0 ? v : dp(v);
    }
}
