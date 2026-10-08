package com.example.jibnam;
    public class WaterLog {
        private int id;
        private int amount;
        private String time;

        // สำหรับดึงจาก DB
        public WaterLog(int id, int amount, String time) {
            this.id = id;
            this.amount = amount;
            this.time = time;
        }

        // สำหรับสร้างใหม่ก่อนลง DB
        public WaterLog(int amount, String time) {
            this.amount = amount;
            this.time = time;
        }

        public int getId() { return id; }
        public int getAmount() { return amount; }
        public String getTime() { return time; }
    }

