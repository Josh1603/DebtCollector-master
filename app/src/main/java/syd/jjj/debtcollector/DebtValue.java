package syd.jjj.debtcollector;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class DebtValue {
    @NonNull
    @PrimaryKey
    private Date mDate;
    private String mDollarValue;
    private String mCentValue;

    @NonNull
    public Date getMDate() {
        return mDate;
    }

    public void setMDate(@NonNull Date mDate) {
        this.mDate = mDate;
    }

    public String getMDollarValue() {
        return mDollarValue;
    }

    public void setMDollarValue(String mDollarValue) {
        this.mDollarValue = mDollarValue;
    }

    public String getMCentValue() {
        return mCentValue;
    }

    public void setMCentValue(String mCentValue) {
        this.mCentValue = mCentValue;
    }

    public DebtValue(@NonNull Date mDate, String mDollarValue, String mCentValue) {
        this.mDate = mDate;
        this.mDollarValue = mDollarValue;
        this.mCentValue = mCentValue;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss z", Locale.UK);
        String dateString = sdf.format(mDate);
        return "$" + mDollarValue + "." + mCentValue + " added: " + dateString;
    }

    public long getRawX() {
        return mDate.getTime();
    }

    public long getRawY() {
        String mDollarCentValue = mDollarValue.concat(mCentValue);
        return Long.valueOf(mDollarCentValue);
    }


}
