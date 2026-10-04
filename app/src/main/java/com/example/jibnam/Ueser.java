package com.example.jibnam;

public class Ueser {
        private double weight ;
        private String timeWakeUp ;
        private String timeSleep ;
        private double waterLit ;

        public Ueser (double weight , String timeWakeUp , String timeSleep , double waterLit )
        {
            setTimeSleep(timeSleep);
            setTimeWakeUp(timeWakeUp);
            setWeight(weight);
        }

        public double getWeight()
        {
            return weight;
        }

        public void setWeight(double weight)
        {
            this.weight = weight;
        }

        public String getTimeWakeUp() {
            return timeWakeUp;
        }

        public void setTimeWakeUp(String timeWakeUp)
        {
            this.timeWakeUp = timeWakeUp;
        }

        public String getTimeSleep()
        {
            return timeSleep;
        }

        public void setTimeSleep(String timeSleep)
        {
            this.timeSleep = timeSleep;
        }

        public double getWaterLit()
        {
            return waterLit;
        }

        public void setWaterLit(double waterLit)
        {
            this.waterLit = waterLit;
        }



}
