package com.example.pumpkin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DbStructure {

    private int id;
    private String expense;
    private String tag;
    private float amount;
    private long date;

    public DbStructure(int id, String expense, String tag, float amount, long date) {
        this.id = id;
        this.expense = expense;
        this.tag = tag;
        this.amount = amount;
        this.date = date;
    }

    public DbStructure() {}

    //tostring

    @Override
    public String toString() {
        return  "id=" + id +
                ", expense='" + expense + '\'' +
                ", tag='" + tag + '\'' +
                ", amount=" + amount + ",date="+ formattedDate(date);
    }


    //getters and setters


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String formattedDate (long date){
        DateFormat dateFormat = new SimpleDateFormat("E, d/MMM/yy");
        String strDate = dateFormat.format(date);
        return strDate;
    }

}
