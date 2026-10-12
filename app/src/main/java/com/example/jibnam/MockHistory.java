
package com.example.jibnam;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MockHistory {

    public static class DayData {
        public String date;
        public double drank;
        public double target;

        public DayData(String date, double drank, double target) {
            this.date = date;
            this.drank = drank;
            this.target = target;
        }

        public int getPercent() {
            if (target <= 0) {
                return 0;
            }

            return (int) Math.round(drank / target * 100);
        }
    }

    private final Map<String, DayData> data = new HashMap<>();

    public MockHistory() {
        add("2026-10-02", 2.2, 2.0);
        add("2026-10-04", 1.0, 2.0);
        add("2026-10-07", 1.5, 2.0);
        add("2026-10-09", 2.0, 2.0);
        add("2026-10-12", 2.2, 2.0);
        add("2026-10-15", 1.0, 2.0);
        add("2026-10-18", 1.5, 2.0);
        add("2026-10-21", 2.0, 2.0);
        add("2026-10-25", 2.2, 2.0);
        add("2026-10-28", 0.8, 2.0);
    }

    private void add(String date, double drank, double target) {
        data.put(date, new DayData(date, drank, target));
    }

    public DayData getDay(int year, int month, int day) {
        String key = String.format(
                Locale.US,
                "%04d-%02d-%02d",
                year,
                month,
                day
        );

        return data.get(key);
    }
}
