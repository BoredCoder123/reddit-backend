package com.example.redditbackend.utility;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Subtract2Times {
    public static String subtract2Times(Date date){
        long diff = System.currentTimeMillis() - date.getTime();
        TimeUnit time1 = TimeUnit.DAYS, time2=TimeUnit.HOURS, time3=TimeUnit.MINUTES;
        long t1 = time1.convert(diff, TimeUnit.MILLISECONDS);
        long t2 = time2.convert(diff, TimeUnit.MILLISECONDS);
        long t3 = time3.convert(diff, TimeUnit.MILLISECONDS);
        if(t1>=1)
            return t1+" days ago";
        else if(t2>=1)
            return t2+" hours ago";
        else if(t3>=1)
            return t3+" minutes ago";
        else
            return "few seconds ago";
    }
}
