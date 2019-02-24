package syd.jjj.debtcollector;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

/**
 * This class should be used in conjunction with an EditText set to decimal input. The filter limits
 * the amount of characters both before and after the decimal point to their respective specified maxima.
 */
public class DecimalDigitsInputFilter implements InputFilter {

    private int preDecimalPointMaxLength;
    private int postDecimalPointMaxLength;

    //The default constructor sets the maxima to 5 characters before the decimal place and 2 after it.
    public DecimalDigitsInputFilter() {
        preDecimalPointMaxLength = 5;
        postDecimalPointMaxLength = 2;
    }

    public DecimalDigitsInputFilter(int preDecimalPointMaxLength, int postDecimalPointMaxLength) {
        this.preDecimalPointMaxLength = preDecimalPointMaxLength;
        this.postDecimalPointMaxLength = postDecimalPointMaxLength;

        if (preDecimalPointMaxLength < 0) {
            Log.w("DecimalDigitInputFilter", "Pre-decimal maximum length is less than zero.");
        }
        if (postDecimalPointMaxLength < 0) {
            Log.w("DecimalDigitInputFilter", "Post-decimal maximum length is less than zero.");
        }

    }


    /**
     * This method sets the conditions required to limit user input to the specified maxima constraints.
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {


        // If the user inputs a decimal point in a position which violates the post-decimal maximum
        // limit, return an empty CharSequence.
        int postMaximumLimitPosition = (dest.length() - (postDecimalPointMaxLength + 1));
        if (source.toString().contains(".") && dstart <= postMaximumLimitPosition) {
            return "";
        }

        // If the user attempts to delete the decimal point in a way which would result in the
        // pre-decimal maximum limit being violated, return an empty CharSequence.
        int maximumLimit = preDecimalPointMaxLength + 2;
        if (source == "" && dest.charAt(dstart) == '.' && dest.length() >= maximumLimit) {
            return ".";
        }

        // On the condition that the EditText doesn't contain a decimal point:
        if (!dest.toString().contains(".")) {

            // If the text has reached the pre-decimal point limit, and the user input isn't a
            // decimal point, return an empty CharSequence.
            if (dest.length() >= preDecimalPointMaxLength && !source.toString().contains(".")) {
                return "";
            }

            // Otherwise, if the EditText does contain a decimal point:
        } else {
            int positionOfDecimalPoint = dest.toString().indexOf(".");
            int lastDestIndex = (dest.length() - 1);
            // If the text has reached the post-decimal point limit and the user has attempted to
            // enter input after the decimal point, return an empty CharSequence,
            // OR if the text has reached the pre-decimal point limit and the user has attempted to
            // enter input before the decimal point, return an empty CharSequence.
            if (((dest.subSequence(positionOfDecimalPoint, lastDestIndex).length() >= postDecimalPointMaxLength) &&
                    (dstart > positionOfDecimalPoint)) ||
                    (dest.subSequence(0, (positionOfDecimalPoint)).length() >= preDecimalPointMaxLength &&
                    dstart <= positionOfDecimalPoint)) {
                return "";
            }
        }

        // If all the conditions are met, user input isn't filtered.
        return null;
    }
}
