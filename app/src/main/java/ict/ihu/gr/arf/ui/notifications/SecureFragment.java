package ict.ihu.gr.arf.ui.notifications;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ict.ihu.gr.arf.R;

public class SecureFragment extends Fragment {

    public SecureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_secure, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show the password dialog when this fragment is created
        PasswordDialogFragment passwordDialog = new PasswordDialogFragment();
        passwordDialog.show(getParentFragmentManager(), "passwordDialog");
    }
}
