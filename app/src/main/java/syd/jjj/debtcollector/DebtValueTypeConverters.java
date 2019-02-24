package syd.jjj.debtcollector;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.TypeConverter;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

public class DebtValueTypeConverters {
    @TypeConverter
    public static Date dateFromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
