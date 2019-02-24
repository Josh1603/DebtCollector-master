package syd.jjj.debtcollector;

/**
 * Provides an interface for fragments which use separated dollar cent input.
 */
public interface DCIDialogFragmentInterface {


    /**
     * Set new debt value.
     * @param uIDollarValue User input.
     * @param uICentValue User input.
     */
    void NewDebtValue(String uIDollarValue, String uICentValue);

    /**
     * Add to currrent debt value.
     * @param uIDollarValue User input.
     * @param uICentValue User input.
     */
    void AddDebt(String uIDollarValue, String uICentValue);

    /**
     * Subtract from current debt value.
     * @param uIDollarValue User input.
     * @param uICentValue User input.
     */
    void PayOffDebt(String uIDollarValue, String uICentValue);
}
