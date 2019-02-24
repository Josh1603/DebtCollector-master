package syd.jjj.debtcollector;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class DeleteAllConfirmationFragment extends DialogFragment {

    private DeleteAllCallback callback;

    /**
     * Assigns references to the EditText views when the the fragment is created.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.delete_all_confirmation_dialog_fragment, container, false);

        return v;
    }

    /**
     * Allows the DCI interface to interact with the activity once the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DeleteAllCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DeleteAllConfirmationFragment");

        }
    }

    /**
     * Adds the button views and assigns listeners to the fragment once the view is created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton confirmationButton = view.findViewById(R.id.confirmDeleteButton);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.DeleteAllData();
                getDialog().dismiss();
            }
        });

        ImageButton cancelButton = view.findViewById(R.id.cancelDeleteButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    public interface DeleteAllCallback {
        void DeleteAllData();
    }
}
