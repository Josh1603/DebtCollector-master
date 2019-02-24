package syd.jjj.debtcollector;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;


/**
 * The main activity for this app. Current debt values are stored as shared preferences as a form of
 * basic data persistence. Calculations are delegated to the DebtCalculation class.
 */
public class DebtCollectorActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        DPIDialogFragmentInterface,
        DCIDialogFragmentInterface
{

    private TextView currentDebtValue;
    private DebtCalculations debtCalculations;

    private SharedPreferences sharedPreferences;
    private String sharedPreferenceName = "DebtCollectorSharedPreferences";

    private final String CURRENT_DOLLAR_TOTAL_KEY = "current_dollar_total";
    private final String CURRENT_CENT_TOTAL_KEY = "current_cent_total";
    private final String CURRENT_PERIOD_KEY = "current_period_key";

    private boolean decimalPointIncluded;
    private DebtValue currentDebtValueObj;
    private RecyclerView mRecyclerView;
    private DebtValueViewModel debtValueViewModel;
    GraphRecyclerViewAdapter adapter;

    /**
     * Displays the current debt value and provides ImageButtons which open fragments to modify the
     * debt value, or in the case of the undo button, immediately undoes the last action.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initialiseTheme();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setCurrentPeriodButton();

        currentDebtValue = findViewById(R.id.current_debt_value);
        displayCurrentDebt();
        setButtonListeners();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.graph_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        adapter = new GraphRecyclerViewAdapter(new ArrayList<DebtValue>(), getPeriod());
        mRecyclerView.setAdapter(adapter);

        debtValueViewModel = ViewModelProviders.of(this).get(DebtValueViewModel.class);
        debtValueViewModel.getData().observe(this, new Observer<List<DebtValue>>() {
            @Override
            public void onChanged(@Nullable List<DebtValue> data) {
                // Returns cached data automatically after a configuration change,
                // and will be fired again if underlying LiveData object is modified
                if (data != null) {
                    adapter.setDataset(data);
                    adapter.notifyDataSetChanged();
                    LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    DisplayMetrics dM = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dM);
                    int width = dM.widthPixels;
                    // Adjusts padding and centers the current RecyclerView child on orientation change.
                    if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT){
                        mRecyclerView.scrollToPosition(llm.findFirstVisibleItemPosition());
                        int padding = width / 30;
                        mRecyclerView.setPadding(padding, 0,padding, 0);
                    }
                    else {
                        mRecyclerView.scrollToPosition(llm.findLastVisibleItemPosition());
                        int padding = width / 60;
                        mRecyclerView.setPadding(padding, 0,padding, 0);
                    }

                }

                if (data.size() > 0 && adapter.getEndDate() != null) {
                    // Inserts a new view into the RecyclerView if current data input exceeds bounds of the view.
                    if (data.get(data.size() - 1).getMDate().getTime() > adapter.getEndDate().getTime()){
                        adapter = new GraphRecyclerViewAdapter(data, getPeriod());
                        mRecyclerView.swapAdapter(adapter, true);
                    }
                }

                // Resets debt counter after data history is deleted.
                if (data.size() < 1) {
                    resetDebtValue();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates the menu. This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action bar item clicks are handled here. The action bar
        // automatically handles clicks on the Home/Up button.
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Navigation view item clicks are handled here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_add_to) {
            if (decimalPointIncluded) {
                FragmentManager fm = getSupportFragmentManager();
                AddToFragmentDCI addFragDCI = new AddToFragmentDCI();
                addFragDCI.show(fm, "ui_add_to_DCI_fragment");
            } else {
                FragmentManager fm = getSupportFragmentManager();
                AddToFragmentDPI addFragDPI = new AddToFragmentDPI();
                addFragDPI.show(fm, "ui_add_to_DPI_fragment");
            }
        }

        if (id == R.id.nav_pay_off) {
            if (decimalPointIncluded) {
                FragmentManager fm = getSupportFragmentManager();
                PayOffFragmentDCI payFragDCI = new PayOffFragmentDCI();
                payFragDCI.show(fm, "ui_pay_off_DCI_fragment");
            } else {
                FragmentManager fm = getSupportFragmentManager();
                PayOffFragmentDPI payFragDPI = new PayOffFragmentDPI();
                payFragDPI.show(fm, "ui_pay_off_DPI_fragment");
            }
        }

        if (id == R.id.nav_set_new) {
            if (decimalPointIncluded) {
                FragmentManager fm = getSupportFragmentManager();
                SetNewFragmentDCI newFragDCI = new SetNewFragmentDCI();
                newFragDCI.show(fm, "ui_set_new_DCI_fragment");
            } else {
                FragmentManager fm = getSupportFragmentManager();
                SetNewFragmentDPI newFragDPI = new SetNewFragmentDPI();
                newFragDPI.show(fm, "ui_set_new_DPI_fragment");
            }
        }

        if(id == R.id.nav_undo) {
            undo();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called by DCIDialogFragmentInterface. This method gets the new debt value and displays it on
     * the UI.
     */
    public void NewDebtValue(String uIDollars, String uICents) {

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        newDebtValue();
    }

    /**
     * Called by DCIDialogFragmentInterface. This method gets the summed debt value and displays it
     * on the UI.
     */
    public void AddDebt (String uIDollars, String uICents){

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        addDebt();
    }

    /**
     * Called by DCIDialogFragmentInterface. This method gets the paid off debt value and displays
     * it on the UI.
     */
    public void PayOffDebt (String uIDollars, String uICents){

        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollars, uICents);
        payOffDebt();
    }

    /**
     * Called by DPIDialogFragmentInterface. This method gets the new debt value and displays it on
     * the UI.
     */
    public void NewDebtValue(String uIDollarCentValue) {
        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollarCentValue);
        newDebtValue();
    }

    /**
     * Called by DPIDialogFragmentInterface. This method gets the summed debt value and displays it
     * on the UI.
     */
    public void AddDebt(String uIDollarCentValue) {
        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollarCentValue);
        addDebt();
    }

    /**
     * Called by DPIDialogFragmentInterface. This method gets the paid off debt value and displays
     * it on the UI.
     */
    public void PayOffDebt(String uIDollarCentValue) {
        debtCalculations = new DebtCalculations(getCurrentDollarValue(), getCurrentCentValue(), uIDollarCentValue);
        payOffDebt();
    }

    public void resetDebtValue(){
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_DOLLAR_TOTAL_KEY, "0");
        editor.putString(CURRENT_CENT_TOTAL_KEY, "00");
        editor.apply();
        displayCurrentDebt();
    }

    /**
     * Stores and displays a new debt value.
     */
    public void newDebtValue() {
        debtCalculations.newDebtValue();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        storeData(getCurrentDollarValue(), getCurrentCentValue());
        displayCurrentDebt();
    }

    /**
     * Stores and displays a new debt value without storing it to the Room database.
     */
    public void newDebtValueNoStore() {
        debtCalculations.newDebtValue();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        displayCurrentDebt();
    }

    /**
     * Adds UI debt value and displays the new total.
     */
    public void addDebt() {
        debtCalculations.addDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        storeData(getCurrentDollarValue(), getCurrentCentValue());
        displayCurrentDebt();
    }

    /**
     * Pays off UI debt value and displays the new total, including a Snackbar message if debt value
     * is completely paid off.
     */
    public void payOffDebt() {
        debtCalculations.payOffDebt();
        storeCurrentDollarValue();
        storeCurrentCentValue();
        storeData(getCurrentDollarValue(), getCurrentCentValue());
        displayCurrentDebt();
        if (debtCalculations.isPaidOff() && debtCalculations.getRemainderText().equals("")) {
            Snackbar debtPaidOffSnackbar = Snackbar.make(findViewById(R.id.coordinator), "Woohoo! You've paid off all your debt.", Snackbar.LENGTH_SHORT);
            centerAlignSnackbarText(debtPaidOffSnackbar);
            TextView snackbarTextView = debtPaidOffSnackbar.getView().
                    findViewById(android.support.design.R.id.snackbar_text);
            snackbarTextView.setTextColor(getResources().getColor(R.color.white));
            debtPaidOffSnackbar.show();
        }

        if (debtCalculations.isPaidOff() && !debtCalculations.getRemainderText().equals("")) {
            Snackbar debtPaidOffSnackbar = Snackbar.make(findViewById(R.id.coordinator), "You've paid off your debt and an additional " + debtCalculations.getRemainderText() + "!", Snackbar.LENGTH_SHORT);
            centerAlignSnackbarText(debtPaidOffSnackbar);
            TextView snackbarTextView = debtPaidOffSnackbar.getView().
                    findViewById(android.support.design.R.id.snackbar_text);
            snackbarTextView.setTextColor(getResources().getColor(R.color.white));
            debtPaidOffSnackbar.show();
        }
    }

    /**
     * Undoes the previous action.
     */
    public void undo() {
        if (debtCalculations != null) {
            String previousDollarValue = debtCalculations.getPreviousDollars();
            String previousCentValue = debtCalculations.getPreviousCents();
            debtCalculations = new DebtCalculations(
                    getCurrentDollarValue(),
                    getCurrentCentValue(),
                    previousDollarValue,
                    previousCentValue);
            adjustMostRecentDebtValue(previousDollarValue, previousCentValue);
            newDebtValueNoStore();
        }
    }

    /**
     * Stores the current dollar value to shared preferences.
     */
    public void storeCurrentDollarValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_DOLLAR_TOTAL_KEY, debtCalculations.getCurrentDollars());
        editor.apply();
    }

    /**
     * Stores the current cent value to shared preferences.
     */
    public void storeCurrentCentValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_CENT_TOTAL_KEY, debtCalculations.getCurrentCents());
        editor.apply();
    }

    /**
     * Stores current debt value to a Room database.
     */
    public void storeData(String dollarValue, String centValue) {
        currentDebtValueObj = new DebtValue(new Date(), dollarValue, centValue);
        new AsyncTask<DebtValue, Void, Void>() {
            @Override
            protected Void doInBackground(DebtValue... debtValues) {
                DebtValueDatabase debtValueDatabase =
                        DebtValueDatabaseAccessor.getInstance(getApplication());
                debtValueDatabase.debtValueDAO().insertDebtValue(debtValues[0]);
                debtValueViewModel.getData();
                return null;
            }
        }.execute(currentDebtValueObj);
    }

    /**
     * Adds or removes most recent debt value from the Room database depending on whether it was an
     * undo or a 'double' undo.
     */
    public void adjustMostRecentDebtValue(String previousDollarValue, String previousCentValue) {
        if (currentDebtValueObj != null) {
            new AsyncTask<DebtValue, Void, Void>() {
                @Override
                protected Void doInBackground(DebtValue... debtValues) {
                    DebtValueDatabase debtValueDatabase =
                            DebtValueDatabaseAccessor.getInstance(getApplication());
                    debtValueDatabase.debtValueDAO().deleteDebtValue(debtValues[0]);
                    debtValueViewModel.getData();
                    return null;
                }
            }.execute(currentDebtValueObj);
            currentDebtValueObj = null;
        } else {
            storeData(previousDollarValue, previousCentValue);
        }
    }

    /**
     * Gets the current dollar value from shared preferences.
     */
    public String getCurrentDollarValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_DOLLAR_TOTAL_KEY, "0");
    }

    /**
     * Gets the current cent value from shared preferences.
     */
    public String getCurrentCentValue() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_CENT_TOTAL_KEY, "00");
    }

    /**
     * Displays the current debt value on the UI.
     */
    public void displayCurrentDebt () {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        String total = "$" + sharedPreferences.getString(CURRENT_DOLLAR_TOTAL_KEY, "0")
                + "." + sharedPreferences.getString(CURRENT_CENT_TOTAL_KEY, "00");
        currentDebtValue.setText(total);
    }

    /**
     * Centrally aligns the text in a given Snackbar.
     * @param snackbar The snackbar whose text is to be centrally aligned.
     */
    public void centerAlignSnackbarText(Snackbar snackbar) {
        View view = snackbar.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    /**
     * Overriden method ensures to be inflated Toolbar is instantiated.
     * @param resid The resource ID for the theme to be set.
     */
    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
        findViewById(R.id.toolbar);
    }

    /**
     * Sets the theme according to the current preference.
     */
    public void initialiseTheme() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String currentTheme = sharedPreferences.getString("theme_settings_list", "defaultTheme");

        if(currentTheme.equals("defaultTheme")){
            setTheme(R.style.DefaultAppTheme);
        }

        if(currentTheme.equals("wisteriaTheme")){
            setTheme(R.style.Wisteria);

            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Sets status bar icons to black on the main activity.
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            */
        }

        if(currentTheme.equals("softPastelTheme")){
            setTheme(R.style.SoftPastel);
        }
    }

    /**
     * Sets listeners for bottom app bar action buttons and period buttons.
     */
    private void setButtonListeners() {
        ImageButton addButton = findViewById(R.id.addButton);
        ImageButton payOffButton = findViewById(R.id.payOffButton);
        ImageButton newTotalButton = findViewById(R.id.newTotalButton);
        ImageButton undoButton = findViewById(R.id.undoButton);

        final Button weeklyButton = findViewById(R.id.weekly_button);
        final Button monthlyButton = findViewById(R.id.monthly_button);
        final Button yearlyButton = findViewById(R.id.yearly_button);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        decimalPointIncluded = sharedPreferences.getBoolean("decimal_point_separator_switch", false);

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo();
            }
        });

        if (decimalPointIncluded) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    AddToFragmentDCI addFragDCI = new AddToFragmentDCI();
                    addFragDCI.show(fm, "ui_add_to_DCI_fragment");
                }
            });
            payOffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    PayOffFragmentDCI payFragDCI = new PayOffFragmentDCI();
                    payFragDCI.show(fm, "ui_pay_off_DCI_fragment");
                }
            });
            newTotalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    SetNewFragmentDCI newFragDCI = new SetNewFragmentDCI();
                    newFragDCI.show(fm, "ui_set_new_DCI_fragment");
                }
            });
        } else {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    AddToFragmentDPI addFragDPI = new AddToFragmentDPI();
                    addFragDPI.show(fm, "ui_add_to_DPI_fragment");
                }
            });
            payOffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    PayOffFragmentDPI payFragDPI = new PayOffFragmentDPI();
                    payFragDPI.show(fm, "ui_pay_off_DPI_fragment");
                }
            });
            newTotalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    SetNewFragmentDPI newFragDPI = new SetNewFragmentDPI();
                    newFragDPI.show(fm, "ui_set_new_DPI_fragment");
                }
            });
        }

        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CURRENT_PERIOD_KEY, "WEEKLY");
                editor.apply();
                v.setBackgroundResource(R.drawable.button_focused);

                Resources.Theme theme = getTheme();
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                ((Button) v).setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                theme.resolveAttribute(R.attr.periodButtonTextAndOutlineColor, typedValue, true);

                monthlyButton.setBackgroundResource(R.drawable.button_unfocused);
                monthlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                yearlyButton.setBackgroundResource(R.drawable.button_unfocused);
                yearlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));


                updateUI();
            }
        });

        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CURRENT_PERIOD_KEY, "MONTHLY");
                editor.apply();
                v.setBackgroundResource(R.drawable.button_focused);

                Resources.Theme theme = getTheme();
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                ((Button) v).setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                theme.resolveAttribute(R.attr.periodButtonTextAndOutlineColor, typedValue, true);

                weeklyButton.setBackgroundResource(R.drawable.button_unfocused);
                weeklyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                yearlyButton.setBackgroundResource(R.drawable.button_unfocused);
                yearlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));


                updateUI();
            }
        });

        yearlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CURRENT_PERIOD_KEY, "YEARLY");
                editor.apply();
                v.setBackgroundResource(R.drawable.button_focused);

                Resources.Theme theme = getTheme();
                TypedValue typedValue = new TypedValue();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                ((Button) v).setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                theme.resolveAttribute(R.attr.periodButtonTextAndOutlineColor, typedValue, true);

                weeklyButton.setBackgroundResource(R.drawable.button_unfocused);
                weeklyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                monthlyButton.setBackgroundResource(R.drawable.button_unfocused);
                monthlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));

                updateUI();
            }
        });
    }

    /**
     * Gets the current period value from shared preferences.
     */
    public String getPeriod() {
        sharedPreferences = getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_PERIOD_KEY, "MONTHLY");
    }

    /**
     * Sets the colour of the period buttons according to current selection.
     */
    public void setCurrentPeriodButton() {

        final Button weeklyButton = findViewById(R.id.weekly_button);
        final Button monthlyButton = findViewById(R.id.monthly_button);
        final Button yearlyButton = findViewById(R.id.yearly_button);

        Resources.Theme theme = getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.background, typedValue, true);

        String period = getPeriod();
        switch (period) {
            case "WEEKLY":
                weeklyButton.setBackgroundResource(R.drawable.button_focused);
                weeklyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));
                break;
            case "MONTHLY":
                monthlyButton.setBackgroundResource(R.drawable.button_focused);
                monthlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));
                break;
            case "YEARLY":
                yearlyButton.setBackgroundResource(R.drawable.button_focused);
                yearlyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), typedValue.resourceId));
                break;
        }
    }

    /**
     * Loads data from the Room database according to the current period setting.
     */
    public void updateUI() {
        List<DebtValue> dataset = adapter.getDataset();
        adapter = new GraphRecyclerViewAdapter(dataset, getPeriod());
        mRecyclerView.swapAdapter(adapter, true);
        debtValueViewModel.getData();

    }
}
