package syd.jjj.debtcollector;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface DebtValueDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDebtValue(DebtValue debtValue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDebtValues(List<DebtValue> debtValues);

    @Delete
    void deleteDebtValue(DebtValue debtValue);

    @Query("SELECT * FROM debtvalue ORDER BY datetime(mDate) DESC")
    List<DebtValue> allDebtValues();

    @Query("DELETE FROM debtvalue")
    void deleteAllDebtValues();

    @Query("SELECT * FROM debtvalue WHERE mDate =:date")
    DebtValue getDebtValue(Date date);

    @Query("SELECT * FROM debtvalue WHERE mDate BETWEEN :dateStart AND :dateEnd")
    List<DebtValue> getDebtValuesBetween(Date dateStart, Date dateEnd);

    @Query("SELECT MIN(mDate) FROM debtvalue")
    Date getEarliestDate();
}

