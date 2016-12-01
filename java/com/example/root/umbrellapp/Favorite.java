package com.example.root.umbrellapp;

public class Favorite {
    private String name, id, date;
    private String[] max, min, icon;

    public Favorite() {

    }

    public Favorite(String name, String id, String date, String[] max, String[] min, String[] icon) {
        this.name = name;
        this.id = id;
        this.date = date;
        this.max = max;
        this.min = min;
        this.icon = icon;
    }

    public void setName(String name) { this.name = name; }

    public void setId(String id) { this.id = id; }

    public void setDate(String date) { this.date = date; }

    public void setMax(String[] max) { this.max = max; }

    public void setMin(String[] min) { this.min = min; }

    public void setIcon(String[] icon) { this.icon = icon; }

    public String getName() { return this.name; }

    public String getId() { return this.id; }

    public String getDate() { return this.date; }

    public String[] getMax() { return this.max; }

    public String[] getMin() { return this.min; }

    public String[] getIcon() { return this.icon; }
}
