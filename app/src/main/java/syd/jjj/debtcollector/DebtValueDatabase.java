package syd.jjj.debtcollector;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {DebtValue.class}, version = 1)
@TypeConverters({DebtValueTypeConverters.class})
public abstract class DebtValueDatabase extends RoomDatabase {
    public abstract DebtValueDAO debtValueDAO();
}
