package syd.jjj.debtcollector;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddToFragmentDPI extends DialogFragment {

        String uIDollarCent;
        DPIDialogFragmentInterface dataPass;
        EditText dollarCentView;
        ImageButton addButton;

        /**
         * Assigns references to the EditText views when the the fragment is created.
         */
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dpi_add_to_dialog_fragment, container);
            dollarCentView = v.findViewById(R.id.dollar_cent);
            dollarCentView.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6, 2)});

            //Automatically displays the soft input keyboard.
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;
        }

        /**
         * Allows the DPI interface to interact with the activity once the fragment is attached.
         */
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            try {
                dataPass = (DPIDialogFragmentInterface) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement DPIDialogFragmentInterface");

            }
        }

        /**
         * Adds the button views and assigns listeners to the fragment once the view is created.
         */
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            addButton = view.findViewById(R.id.addButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uIDollarCent = dollarCentView.getText().toString();
                    dataPass.AddDebt(uIDollarCent);
                    getDialog().dismiss();
                }
            });

            dollarCentView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        addButton.performClick();
                        return true;
                    }
                    return false;
                }
            });

            ImageButton cancelButton = view.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }
}
