package syd.jjj.debtcollector;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DebtValueViewModel extends AndroidViewModel {
    private static final String TAG = "DebtValueUpdate";

    private MutableLiveData<List<DebtValue>> debtValues;

    public DebtValueViewModel(Application application) {
        super(application);
    }

    public LiveData<List<DebtValue>> getData() {
        if (debtValues == null) {
            debtValues = new MutableLiveData<>();
        }
        loadDebtValues();
        return debtValues;
    }

    public void loadDebtValues() {
        new AsyncTask<Void, Void, List<DebtValue>>() {
            @Override
            protected List<DebtValue> doInBackground(Void... voids) {
                DebtValueDatabase debtValueDatabase = DebtValueDatabaseAccessor.getInstance(getApplication());
                List<DebtValue> debtValueListCurrent =
                        debtValueDatabase
                                .debtValueDAO()
                                .allDebtValues();
                return debtValueListCurrent;

            }
            @Override
            protected void onPostExecute(List<DebtValue> data) {
                debtValues.setValue(data);
            }
        }.execute();
    }
}