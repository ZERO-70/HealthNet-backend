package com.server.HealthNet.Model;

import java.time.LocalTime;

public class Avalibility {

    public Avalibility() {
    }

    public Avalibility(LocalTime mon_startTime, LocalTime mon_endTime, LocalTime tue_startTime, LocalTime tue_endTime,
            LocalTime wed_startTime, LocalTime wed_endTime, LocalTime thu_startTime, LocalTime thu_endTime,
            LocalTime fri_startTime, LocalTime fri_endTime, LocalTime sat_startTime, LocalTime sat_endTime,
            LocalTime sun_startTime, LocalTime sun_endTime) {
        Mon_startTime = mon_startTime;
        Mon_endTime = mon_endTime;
        Tue_startTime = tue_startTime;
        Tue_endTime = tue_endTime;
        Wed_startTime = wed_startTime;
        Wed_endTime = wed_endTime;
        Thu_startTime = thu_startTime;
        Thu_endTime = thu_endTime;
        Fri_startTime = fri_startTime;
        Fri_endTime = fri_endTime;
        Sat_startTime = sat_startTime;
        Sat_endTime = sat_endTime;
        Sun_startTime = sun_startTime;
        Sun_endTime = sun_endTime;
    }

    private Long doctor_id;

    private LocalTime Mon_startTime;
    private LocalTime Mon_endTime;
    private LocalTime Tue_startTime;
    private LocalTime Tue_endTime;
    private LocalTime Wed_startTime;
    private LocalTime Wed_endTime;
    private LocalTime Thu_startTime;
    private LocalTime Thu_endTime;
    private LocalTime Fri_startTime;
    private LocalTime Fri_endTime;
    private LocalTime Sat_startTime;
    private LocalTime Sat_endTime;
    private LocalTime Sun_startTime;
    private LocalTime Sun_endTime;

    public Long getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public LocalTime getMon_startTime() {
        return Mon_startTime;
    }

    public void setMon_startTime(LocalTime mon_startTime) {
        Mon_startTime = mon_startTime;
    }

    public LocalTime getMon_endTime() {
        return Mon_endTime;
    }

    public void setMon_endTime(LocalTime mon_endTime) {
        Mon_endTime = mon_endTime;
    }

    public LocalTime getTue_startTime() {
        return Tue_startTime;
    }

    public void setTue_startTime(LocalTime tue_startTime) {
        Tue_startTime = tue_startTime;
    }

    public LocalTime getTue_endTime() {
        return Tue_endTime;
    }

    public void setTue_endTime(LocalTime tue_endTime) {
        Tue_endTime = tue_endTime;
    }

    public LocalTime getWed_startTime() {
        return Wed_startTime;
    }

    public void setWed_startTime(LocalTime wed_startTime) {
        Wed_startTime = wed_startTime;
    }

    public LocalTime getWed_endTime() {
        return Wed_endTime;
    }

    public void setWed_endTime(LocalTime wed_endTime) {
        Wed_endTime = wed_endTime;
    }

    public LocalTime getThu_startTime() {
        return Thu_startTime;
    }

    public void setThu_startTime(LocalTime thu_startTime) {
        Thu_startTime = thu_startTime;
    }

    public LocalTime getThu_endTime() {
        return Thu_endTime;
    }

    public void setThu_endTime(LocalTime thu_endTime) {
        Thu_endTime = thu_endTime;
    }

    public LocalTime getFri_startTime() {
        return Fri_startTime;
    }

    public void setFri_startTime(LocalTime fri_startTime) {
        Fri_startTime = fri_startTime;
    }

    public LocalTime getFri_endTime() {
        return Fri_endTime;
    }

    public void setFri_endTime(LocalTime fri_endTime) {
        Fri_endTime = fri_endTime;
    }

    public LocalTime getSat_startTime() {
        return Sat_startTime;
    }

    public void setSat_startTime(LocalTime sat_startTime) {
        Sat_startTime = sat_startTime;
    }

    public LocalTime getSat_endTime() {
        return Sat_endTime;
    }

    public void setSat_endTime(LocalTime sat_endTime) {
        Sat_endTime = sat_endTime;
    }

    public LocalTime getSun_startTime() {
        return Sun_startTime;
    }

    public void setSun_startTime(LocalTime sun_startTime) {
        Sun_startTime = sun_startTime;
    }

    public LocalTime getSun_endTime() {
        return Sun_endTime;
    }

    public void setSun_endTime(LocalTime sun_endTime) {
        Sun_endTime = sun_endTime;
    }

    @Override
    public String toString() {
        return "Avalibility [doctor_id=" + doctor_id + ", Mon_startTime=" + Mon_startTime + ", Mon_endTime="
                + Mon_endTime + ", Tue_startTime=" + Tue_startTime + ", Tue_endTime=" + Tue_endTime + ", Wed_startTime="
                + Wed_startTime + ", Wed_endTime=" + Wed_endTime + ", Thu_startTime=" + Thu_startTime + ", Thu_endTime="
                + Thu_endTime + ", Fri_startTime=" + Fri_startTime + ", Fri_endTime=" + Fri_endTime + ", Sat_startTime="
                + Sat_startTime + ", Sat_endTime=" + Sat_endTime + ", Sun_startTime=" + Sun_startTime + ", Sun_endTime="
                + Sun_endTime + "]";
    }

}
