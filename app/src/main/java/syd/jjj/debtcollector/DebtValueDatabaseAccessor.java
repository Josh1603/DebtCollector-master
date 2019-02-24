package syd.jjj.debtcollector;


import android.arch.persistence.room.Room;
import android.content.Context;

public class DebtValueDatabaseAccessor {

    private static DebtValueDatabase debtValueDatabaseInstance;
    private static final String DEBT_VALUE_DB_NAME = "debtvalue_db";


    private DebtValueDatabaseAccessor() {}

    public static DebtValueDatabase getInstance(Context context) {
        if (debtValueDatabaseInstance == null) {
            // Create or open a new SQLite database, and return it as a Room Database instance.
            debtValueDatabaseInstance = Room
                    .databaseBuilder(context, DebtValueDatabase.class, DEBT_VALUE_DB_NAME).build();
            }
        return debtValueDatabaseInstance;
    }
}
