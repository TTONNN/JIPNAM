package com.example.jibnam;


import java.io.Serializable;

    public class DayRecord implements Serializable {

        private static final long serialVersionUID = 1L;

        private String date;

        public DayRecord(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }


