package syd.jjj.debtcollector;

/**
 * Provides an interface for fragments which use user inputted decimal point input.
 */
public interface DPIDialogFragmentInterface {

    /**
     * Set a new debt value.
     * @param uIDollarCentValue User input.
     */
    void NewDebtValue(String uIDollarCentValue);

    /**
     * Add to the current debt value.
     * @param uIDollarCentValue User input.
     */
    void AddDebt(String uIDollarCentValue);

    /**
     * Subtract from current debt value.
     * @param uIDollarCentValue User input.
     */
    void PayOffDebt(String uIDollarCentValue);
}
