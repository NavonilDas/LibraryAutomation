package com.ganesh.library;

import android.util.Log;

import java.lang.Object;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Book {
    public String bookname;
    public String issuedate;
    private Date issue;
    public Book(String book,String issue){
        this.bookname = book;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dd = new SimpleDateFormat("dd/MM/yyyy");
            Date d = dateFormat.parse(issue);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DATE,25);
            this.issue = c.getTime();
            this.issuedate = dd.format(c.getTime());
        }catch (ParseException ex){
            ex.printStackTrace();
        }
    }
    public long getTime(){
        return this.issue.getTime();
    }
    public long calcFine(){
        int rate = 2;
        // Suppose The fine is 2 INR per day
        long diff = System.currentTimeMillis() - this.issue.getTime();
        if(diff > 0) {
            diff = diff / 3600000 / 24;
            return  rate*diff;
        }
        return  0;
    }
}
