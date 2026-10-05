package com.example.jibnam;

import android.content.Intent;
import android.widget.*;
import android.view.View;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseClass extends AppCompatActivity {

    public static class Box extends LinearLayout {
        public Box(AppCompatActivity a) { super(a); }
    }
    public static class Txt extends TextView {
        public Txt(AppCompatActivity a) { super(a); }
    }
    public static class Inp extends EditText {
        public Inp(AppCompatActivity a) { super(a); }
    }
    public static class Img extends ImageView {
        public Img(AppCompatActivity a) { super(a); }
    }

    abstract void build(Box pg);

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ScrollView sv = new ScrollView(this);
        Box pg = new Box(this);
        sv.addView(pg);
        build(pg);
        setContentView(sv);
    }

    void add(Box p, View v) { p.addView(v); }
    void setTxt(TextView t, String s) { t.setText(s); }
    String getTxt(TextView t) { return t.getText().toString(); }
    void setImg(Img i, int id) { i.setImageResource(id); }
    void onTap(View v, Runnable r) { v.setOnClickListener(x -> r.run()); }
    void go(Class<?> c) { startActivity(new Intent(this, c)); }
}
