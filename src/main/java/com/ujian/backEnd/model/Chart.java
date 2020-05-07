package com.ujian.backEnd.model;

public class Chart {
    private int id;
    private String item;
    private String nama;


    public Chart() {
    }

    public Chart(String item, String nama) {
        this.item = item;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


}
