package com.example.pai;

public class Statistic {
    private int kiss_one;
    private int marry_one;
    private int kill_one;
    private int kiss_two;
    private int marry_two;
    private int kill_two;
    private int kiss_th;
    private int marry_th;
    private int kill_th;
    private int total;

    public Statistic() {
    }

    public Statistic(int kiss_one, int marry_one, int kill_one, int kiss_two, int marry_two, int kill_two, int kiss_th, int marry_th, int kill_th, int total) {
        this.kiss_one = kiss_one;
        this.marry_one = marry_one;
        this.kill_one = kill_one;
        this.kiss_two = kiss_two;
        this.marry_two = marry_two;
        this.kill_two = kill_two;
        this.kiss_th = kiss_th;
        this.marry_th = marry_th;
        this.kill_th = kill_th;
        this.total = total;
    }


    public int getKiss_one() {
        return kiss_one;
    }

    public void setKiss_one(int kiss_one) {
        this.kiss_one = kiss_one;
    }

    public int getMarry_one() {
        return marry_one;
    }

    public void setMarry_one(int marry_one) {
        this.marry_one = marry_one;
    }

    public int getKill_one() {
        return kill_one;
    }

    public void setKill_one(int kill_one) {
        this.kill_one = kill_one;
    }

    public int getKiss_two() {
        return kiss_two;
    }

    public void setKiss_two(int kiss_two) {
        this.kiss_two = kiss_two;
    }

    public int getMarry_two() {
        return marry_two;
    }

    public void setMarry_two(int marry_two) {
        this.marry_two = marry_two;
    }

    public int getKill_two() {
        return kill_two;
    }

    public void setKill_two(int kill_two) {
        this.kill_two = kill_two;
    }

    public int getKiss_th() {
        return kiss_th;
    }

    public void setKiss_th(int kiss_th) {
        this.kiss_th = kiss_th;
    }

    public int getMarry_th() {
        return marry_th;
    }

    public void setMarry_th(int marry_th) {
        this.marry_th = marry_th;
    }

    public int getKill_th() {
        return kill_th;
    }

    public void setKill_th(int kill_th) {
        this.kill_th = kill_th;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
