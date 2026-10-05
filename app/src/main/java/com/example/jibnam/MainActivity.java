package com.example.jibnam;

public class MainActivity extends BaseClass {

    @Override
    void build(Box pg) {
        Txt t = new Txt(this);
        t.setText("Hello1");
        add(pg, t);
        go(Screen_2.class);
    }
}
