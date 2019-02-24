package syd.jjj.debtcollector;

/**
 * This class can be used to perform simple operations on two debt values. Any negative values are
 * rounded to zero.
 */
public class DebtCalculations {

    public DebtCalculations(String currentDollars, String currentCents, String uIDollars, String uICents){

        //The most recent debt total.
        this.currentDollars = currentDollars;
        this.currentCents = currentCents;
        //The user inputted amount.
        this.uIDollars = uIDollars;
        this.uICents = uICents;
    }

    public DebtCalculations(String currentDollars, String currentCents, String uIDollarsAndCents){

        //The most recent debt total.
        this.currentDollars = currentDollars;
        this.currentCents = currentCents;
        //The user inputted amount.
        setUIValues(uIDollarsAndCents);
    }

    private String currentDollars;
    private String currentCents;
    private String previousDollars;
    private String previousCents;
    private String uIDollars;
    private String uICents;
    private String remainderText;
    private boolean isPaidOff;

    /**
     * Gets the most recent dollar value.
     */
    public String getCurrentDollars(){
        return currentDollars;
    }

    /**
     * Gets the most recent cent value.
     */
    public String getCurrentCents(){
        return currentCents;
    }

    /**
     * Gets remainder text.
     */
    public String getRemainderText() {
        return remainderText;
    }

    /**
     * Gets previous dollar value.
     * @return
     */
    public String getPreviousDollars() {
        return previousDollars;
    }

    /**
     * Gets previous cent value.
     * @return
     */
    public String getPreviousCents() {
        return previousCents;
    }

    /**
     * Sets the UI values when inputted as a single String.
     * @param uIDollarsAndCents The String value containing the UI dollar-cent value.
     */
    public void setUIValues(String uIDollarsAndCents) {

        int dotIndex = uIDollarsAndCents.indexOf(".");
        int stringLength = uIDollarsAndCents.length();

        if (dotIndex == -1) {
            uIDollars = uIDollarsAndCents;
            uICents = "00";
        } else {

            if (dotIndex == (stringLength - 1)) {
                uIDollars = uIDollarsAndCents.substring(0, (stringLength - 1));
                uICents = "00";
            }

            if (dotIndex == (stringLength - 2)) {
                uIDollars = uIDollarsAndCents.substring(0, (stringLength - 2));
                uICents = uIDollarsAndCents.substring(stringLength - 1) + "0";
            }

            if (dotIndex == (stringLength - 3)) {
                uIDollars = uIDollarsAndCents.substring(0, (stringLength - 3));
                uICents = uIDollarsAndCents.substring(stringLength - 2);
            }
        }
    }

    /**
     * Sets the most recent debt total to the user inputted total.
     */
    public void newDebtValue() {

        isPaidOff = false;
        correctZeroValues();
        previousDollars = currentDollars;
        previousCents = currentCents;
        currentDollars = uIDollars;
        currentCents = uICents;
    }

    /**
     * Sets the most recent total to the sum of the previous total and the user inputted total.
     */
    public void addDebt(){

        isPaidOff = false;
        correctZeroValues();
        previousDollars = currentDollars;
        previousCents = currentCents;
        String currentTotal = currentDollars.concat(currentCents);
        String uITotal = uIDollars.concat(uICents);
        int currentTotalInt = Integer.parseInt(currentTotal);
        int uITotalInt = Integer.parseInt(uITotal);
        int newTotalInt = currentTotalInt + uITotalInt;
        String newTotal = Integer.toString(newTotalInt);
        setNewTotal(newTotal);
    }

    /**
     * Sets the most recent total to the difference between the previous total and the user inputted total,
     * unless the latter exceeds the former, in which case the current total is set to default values.
     */
    public void payOffDebt (){

        correctZeroValues();
        previousDollars = currentDollars;
        previousCents = currentCents;
        String currentTotal = currentDollars.concat(currentCents);
        String uITotal = uIDollars.concat(uICents);
        int currentTotalInt = Integer.parseInt(currentTotal);
        int uITotalInt = Integer.parseInt(uITotal);
        int newTotalInt = currentTotalInt - uITotalInt;

        if (newTotalInt > 0) {
            String newTotal = Integer.toString(newTotalInt);
            setNewTotal(newTotal);
        }

        if (newTotalInt == 0) {
            isPaidOff = true;
            remainderText = "";
            setNewTotal("");
        }

        if (newTotalInt < 0) {
            String newTotal = Integer.toString(-newTotalInt);
            setNewTotal(newTotal);
            isPaidOff = true;
            remainderText = "$" + currentDollars + "." +currentCents;
            setNewTotal("");
        }
    }

    /**
     * Corrects for any missing zero values in the user input.
     */
    private void correctZeroValues() {
        //Sets a String value for dollars if none set by user.
        if (uIDollars.length() == 0) {
            uIDollars = "0";
        }
        //Sets a String value for second dp of cents if none set by user.
        if (uICents.length() == 1) {
            uICents = uICents.concat("0");
        }
        //Sets a String value for cents if none set by user.
        if (uICents.length() == 0) {
            uICents = ("00");
        }
    }


    /**
     * Sets the new total, taking into account corrections required for smaller values.
     */
    private void setNewTotal(String newTotal){

        int totalLength = newTotal.length();

        if(totalLength == 0) {
            currentDollars = "0";
            currentCents = "00";
        }

        if(totalLength == 1) {
            currentDollars = "0";
            currentCents = "0" + newTotal;
        }

        if(totalLength == 2) {
            currentDollars = "0";
            currentCents = newTotal;
        }

        if(totalLength > 2) {
            currentDollars = newTotal.substring(0, totalLength - 2);
            currentCents = newTotal.substring(totalLength - 2);
        }
    }

    public boolean isPaidOff() {
        return isPaidOff;
    }
}
